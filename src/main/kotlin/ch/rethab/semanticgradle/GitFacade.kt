package ch.rethab.semanticgradle

class GitFacade(private val cli: GitCli) {

    fun findLatestVersion(): Version? =
        cli.listTags().maxByOrNull { it }

    fun findCommitsSince(version: Version): List<Commit> =
        cli.listCommits().takeWhile { it.tags.containsNot(version) }

    fun tagCurrentHead(version: Version) {
        cli.tag(version)
        println("Tagged $version")
    }

    fun push() {
        cli.push()
        println("Pushed")
    }

    private fun List<String>.containsNot(version: Version): Boolean =
        this.none { tag -> Version.parseTag(tag)?.equals(version) ?: false }

    private fun String.isAVersion(): Boolean = Version.parseTag(this) != null

}

data class Commit (
    val message: String,
    val tags: List<String>
)

