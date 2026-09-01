package dev.gaphunter.environmentspecificconstantcompanion.detect

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope

/**
 * Project-wide index of config-file values (`.properties`/`.yml`/
 * `.yaml`/`.env`), rebuilt fresh on every call from IntelliJ's own
 * filename index -- never a raw whole-tree VFS walk. Filename-index
 * lookups are backed by an index the platform already maintains
 * (kept up to date as files change), so this stays cheap enough to
 * call directly from `checkFile` -- no separate cache/refresh-action
 * machinery is needed here, unlike a full source-file scan such as
 * `feature-flag-reference-companion`'s `FlagReferenceIndex` requires.
 *
 * **v0.1 scope, stated honestly:** covers `.properties`, `.yml`, and
 * `.yaml` files (found via [FilenameIndex.getAllFilesByExt], an
 * extension-based index lookup) plus the single exact filename
 * `.env` (found via [FilenameIndex.getVirtualFilesByName], a
 * name-based index lookup -- there is no equally cheap by-suffix
 * index query for a dotfile with no extension). Variant `.env.*`
 * files (`.env.local`, `.env.production`, etc.) are not covered in
 * this version.
 */
object ProjectConfigValueScanner {

    /** Defensive cap even though filename-index lookups are already narrow -- never let a pathological project (thousands of matching files) make this slow. */
    private const val MAX_CONFIG_FILES = 300
    private const val MAX_TOTAL_BYTES = 5_000_000L
    private val CONFIG_EXTENSIONS = listOf("properties", "yml", "yaml")

    /** Every distinct config value found project-wide, mapped to the (project-relative when possible) path of one file it was found in. */
    fun scan(project: Project): Map<String, String> {
        val scope = GlobalSearchScope.projectScope(project)
        val psiManager = PsiManager.getInstance(project)
        val basePath = project.basePath

        val candidateFiles = LinkedHashSet<VirtualFile>()
        for (ext in CONFIG_EXTENSIONS) {
            candidateFiles += FilenameIndex.getAllFilesByExt(project, ext, scope)
        }
        candidateFiles += FilenameIndex.getVirtualFilesByName(".env", scope)

        val result = LinkedHashMap<String, String>()
        var filesScanned = 0
        var bytesScanned = 0L

        for (virtualFile in candidateFiles) {
            if (filesScanned >= MAX_CONFIG_FILES || bytesScanned >= MAX_TOTAL_BYTES) break
            if (virtualFile.isDirectory) continue
            val text = psiManager.findFile(virtualFile)?.text ?: continue
            filesScanned++
            bytesScanned += text.length

            val displayPath = displayPathFor(virtualFile, basePath)
            for (value in ConfigValueScanner.scanValues(text)) {
                result.putIfAbsent(value, displayPath)
            }
        }
        return result
    }

    private fun displayPathFor(virtualFile: VirtualFile, basePath: String?): String {
        val path = virtualFile.path
        return if (basePath != null && path.startsWith("$basePath/")) path.removePrefix("$basePath/") else virtualFile.name
    }
}
