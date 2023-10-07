package ch.rethab.semanticgradle

class SemanticAnalyzer {

    fun incrementVersion(currentVersion: Version, commits: List<Commit>): Version? {
        val highestBump = commits.maxOfOrNull { analyzeCommit(it) } ?: return null

        return when (highestBump) {
            VersionBump.MAJOR -> Version(currentVersion.major + 1, 0, 0)
            VersionBump.MINOR -> Version(currentVersion.major, currentVersion.minor + 1, 0)
            VersionBump.PATCH -> Version(currentVersion.major, currentVersion.minor, currentVersion.patch + 1)
            VersionBump.NONE -> null
        }
    }

    fun analyzeCommit(commit: Commit): VersionBump {
        val elements = commit.message.split(':')
        if (elements.size < 2) {
            return VersionBump.NONE
        }

        val type = elements[0]

        if (type.endsWith('!')) return VersionBump.MAJOR
        return when (type) {
            "feat" -> VersionBump.MINOR
            "fix" -> VersionBump.PATCH
            else -> VersionBump.NONE
        }
    }
}

enum class VersionBump {
    NONE, PATCH, MINOR, MAJOR
}
