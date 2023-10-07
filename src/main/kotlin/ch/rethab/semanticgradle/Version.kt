package ch.rethab.semanticgradle

data class Version(
    val major: Int,
    val minor: Int,
    val patch: Int,
) {

    override fun toString(): String {
        return toString(true)
    }

    fun toString(vPrefix: Boolean): String {
        return "${if (vPrefix) "v" else ""}${major}.${minor}.${patch}"
    }

    companion object {
        private val versionRegex = Regex("(\\d+)\\.(\\d+)\\.(\\d+)")

        private val tagRegex = Regex("v(\\d+)\\.(\\d+)\\.(\\d+)")

        fun parseTag(tag: String): Version? = parse(tag, tagRegex)

        fun parse(version: String): Version? = parse(version, versionRegex)

        private fun parse(version: String, regex: Regex): Version? =
            regex.matchEntire(version)?.let { result ->
                val (major, minor, patch) = result.groupValues.drop(1).map { it.toInt() }

                Version(major, minor, patch)
            }
    }
}
