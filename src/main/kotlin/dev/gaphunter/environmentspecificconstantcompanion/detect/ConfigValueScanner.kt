package dev.gaphunter.environmentspecificconstantcompanion.detect

/**
 * Plain-text `key=value` (`.properties`/`.env`) and simple YAML
 * (`key: value`) line scanner -- same shape as
 * `config-secrets-file-companion`'s `ConfigLineScanner`, kept as an
 * independent copy per this catalog's standing convention of
 * duplicating small text-scan helpers rather than sharing a
 * dependency across plugins. Here the *value* itself is what's
 * collected (no secret-shape heuristics involved) -- the whole point
 * is comparing it, verbatim, against string literals found in source.
 *
 * **v0.1 scope:** single-line values only, same as the sibling
 * scanner -- YAML block scalars (`|`, `>`) and multi-line values are
 * not specially handled. Surrounding single/double quotes on the
 * value are stripped so `url: "https://api.example.com"` and
 * `url: https://api.example.com` both yield the same collected value.
 */
object ConfigValueScanner {

    private val PROPERTIES_OR_ENV_LINE = Regex("""^([A-Za-z_][A-Za-z0-9_.-]*)\s*=\s*(.+)$""")
    private val YAML_LINE = Regex("""^\s*[A-Za-z_][A-Za-z0-9_.-]*\s*:\s+(.+)$""")

    /** Every distinct, trimmed, unquoted config value found in [text]. */
    fun scanValues(text: String): Set<String> {
        val values = mutableSetOf<String>()
        for (rawLine in text.lineSequence()) {
            val trimmed = rawLine.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue

            val value = PROPERTIES_OR_ENV_LINE.find(trimmed)?.groupValues?.get(2)
                ?: YAML_LINE.find(rawLine)?.groupValues?.get(1)
                ?: continue

            val unquoted = unquote(value.trim())
            if (unquoted.isNotEmpty() && unquoted != "|" && unquoted != ">") values += unquoted
        }
        return values
    }

    private fun unquote(value: String): String =
        if (value.length >= 2 && ((value.startsWith('"') && value.endsWith('"')) || (value.startsWith('\'') && value.endsWith('\'')))) {
            value.substring(1, value.length - 1)
        } else {
            value
        }
}
