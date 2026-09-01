package dev.gaphunter.environmentspecificconstantcompanion.detect

import com.intellij.psi.JavaRecursiveElementWalkingVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiLiteralExpression
import dev.gaphunter.environmentspecificconstantcompanion.model.EnvConstantHit

/**
 * Walks a Java file for string literals shaped like a full URL
 * (`http(s)://...`) or an explicit `host:port` pair, and reports each
 * one whose exact text also appears as a value somewhere in the
 * project's own config files -- see [ProjectConfigValueScanner] for
 * how that set is built and [ConfigValueScanner] for how a config
 * line's value is extracted.
 *
 * **v0.1 scope, stated honestly:** exact string match only -- a code
 * literal that differs from the config value by so much as trailing
 * whitespace, a trailing slash, or a scheme-less variant is not
 * matched. Deliberately conservative: matching only what's provably
 * identical keeps false positives near zero, at the cost of missing
 * near-duplicates. Never a bare port number (no host context) --
 * required by the `HOST_PORT` shape itself, which always needs a host
 * part before the colon.
 */
object JavaEnvConstantFinder {

    private val FULL_URL = Regex("""^https?://\S+$""")
    private val HOST_PORT = Regex("""^([A-Za-z0-9.-]+):([0-9]{2,5})$""")

    fun findAll(file: PsiFile, configValues: Map<String, String>): List<EnvConstantHit> {
        if (configValues.isEmpty()) return emptyList()

        val hits = mutableListOf<EnvConstantHit>()
        file.accept(object : JavaRecursiveElementWalkingVisitor() {
            override fun visitLiteralExpression(expression: PsiLiteralExpression) {
                super.visitLiteralExpression(expression)
                val text = expression.value as? String ?: return
                if (!isCandidateShape(text)) return

                val configFilePath = configValues[text] ?: return
                hits += EnvConstantHit(expression, text, configFilePath)
            }
        })
        return hits
    }

    private fun isCandidateShape(text: String): Boolean {
        if (FULL_URL.matches(text)) return true

        val match = HOST_PORT.matchEntire(text) ?: return false
        val host = match.groupValues[1]
        // A coincidental all-digit "12:34" (a time, a ratio, a version
        // fragment) must not read as a false "host:port" match -- a
        // dot still lets a bare IP address ("10.0.0.5:5432") through.
        return host.any { it.isLetter() } || host.contains('.')
    }
}
