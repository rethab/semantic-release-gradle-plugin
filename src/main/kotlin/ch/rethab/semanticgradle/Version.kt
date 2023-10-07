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
        private val regex = Regex("v(\\d+)\\.(\\d+)\\.(\\d+)")
        fun parse(tag: String): Version? =
            regex.matchEntire(tag)?.let { result ->
                val (major, minor, patch) = result.groupValues.drop(1).map { it.toInt() }

                Version(major, minor, patch)
            }
    }
}
