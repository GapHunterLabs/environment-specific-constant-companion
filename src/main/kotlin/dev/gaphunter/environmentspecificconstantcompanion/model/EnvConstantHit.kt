package dev.gaphunter.environmentspecificconstantcompanion.model

import com.intellij.psi.PsiElement

/**
 * One hardcoded URL/host:port [literal] found in source that also
 * appears, textually, as a config value in [configFilePath] (relative
 * to the project base path when available, else the raw file name).
 */
data class EnvConstantHit(
    val anchor: PsiElement,
    val literalValue: String,
    val configFilePath: String,
)
