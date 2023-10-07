package ch.rethab.semanticgradle

class GitFacade(private val cli: GitCli) {

    fun findLatestVersion(): Version? =
        cli.listCommits()
            .firstOrNull { commit -> commit.tags.any { it.isAVersion() } }
            ?.tags?.firstNotNullOf { Version.parse(it) }

    fun findCommitsSince(version: Version): List<Commit> =
        cli.listCommits().takeWhile { it.tags.containsNot(version) }

    private fun List<String>.containsNot(version: Version): Boolean =
        this.none { tag -> Version.parse(tag)?.equals(version) ?: false }

    private fun String.isAVersion(): Boolean = Version.parse(this) != null

}

data class Commit (
    val message: String,
    val tags: List<String>
)

