package io.github.rethab.semanticrelease

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GitFacade(private val cli: GitCli) {

    private val logger: Logger = LoggerFactory.getLogger(GitFacade::class.java)

    fun findLatestVersion(): Version? =
        cli.listTags().maxByOrNull { it }

    fun findCommitsSince(version: Version): List<Commit> =
        cli.listCommits().takeWhile { commit ->

            val commitIsVersioned = commit.tags.containsNot(version)

            logger.debug("Commit with tags [${commit.tags.joinToString()}] '${commit.message}' contains a version? $commitIsVersioned")

            commitIsVersioned
        }

    fun tagCurrentHead(version: Version) {
        cli.tag(version)
        logger.info("Tagged $version")
    }

    fun push() {
        cli.push()
        logger.info("Pushed")
    }

    private fun List<String>.containsNot(version: Version): Boolean =
        this.none { tag -> Version.parseTag(tag)?.equals(version) ?: false }
}

data class Commit(
    val message: String,
    val tags: List<String>
)

