package dev.gaphunter.environmentspecificconstantcompanion.inspection

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile
import dev.gaphunter.environmentspecificconstantcompanion.detect.JavaEnvConstantFinder
import dev.gaphunter.environmentspecificconstantcompanion.detect.ProjectConfigValueScanner
import dev.gaphunter.environmentspecificconstantcompanion.review.ReviewPrompt

/**
 * Flags a hardcoded URL/host:port string literal in Java source that
 * also appears, textually, as a value in one of the project's own
 * config files (`.properties`/`.yml`/`.yaml`/`.env`) -- the value
 * clearly belongs to config, and the code copy can now silently drift
 * out of sync with it.
 *
 * Runs via `checkFile` (same shape as every other inspection in this
 * catalog); [ProjectConfigValueScanner] builds the project-wide config
 * value set fresh on each call (backed by IntelliJ's own filename
 * index, so this stays cheap without a separate cache), and
 * [JavaEnvConstantFinder] does the real PSI walk of the file being
 * checked.
 */
class EnvironmentSpecificConstantInspection : LocalInspectionTool() {

    companion object {
        const val MAX_FILE_LENGTH = 500_000
    }

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file.text.length > MAX_FILE_LENGTH) return null
        if (file !is PsiJavaFile) return null

        val configValues = ProjectConfigValueScanner.scan(file.project)
        val hits = JavaEnvConstantFinder.findAll(file, configValues)
        if (hits.isEmpty()) return null

        val problems = hits.map { hit ->
            manager.createProblemDescriptor(
                hit.anchor,
                "This literal ('${hit.literalValue}') also appears as a config value in ${hit.configFilePath} -- " +
                    "it can silently drift out of sync with config, since nothing enforces they stay equal",
                isOnTheFly,
                emptyArray(),
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
            )
        }

        val path = file.virtualFile?.path
        if (path != null) {
            for (hit in hits) {
                val lineNumber = file.viewProvider.document?.getLineNumber(hit.anchor.textRange.startOffset) ?: -1
                ReviewPrompt.recordHit(file.project, "$path:$lineNumber")
            }
        }

        return problems.toTypedArray()
    }
}
