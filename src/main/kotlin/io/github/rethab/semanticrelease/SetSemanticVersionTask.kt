package io.github.rethab.semanticrelease

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class SetSemanticVersionTask : DefaultTask() {
    @TaskAction
    fun setNextVersion() {
        val git = GitFacade(GitCli(project.projectDir))
        val semanticAnalyzer = SemanticAnalyzer()
        val latestVersion = git.findLatestVersion()

        if (latestVersion == null) {
            logger.warn("No previous version found in git tags. Not sure how to determine next version..")
            return
        }

        logger.quiet("Latest Version: $latestVersion")
        logger.quiet("Commits since then:")
        val commits = git.findCommitsSince(latestVersion)
        commits.forEach { commit ->
            logger.quiet("\t${commit.message} --> BUMP: ${semanticAnalyzer.analyzeCommit(commit)}")
        }

        val nextVersion = semanticAnalyzer.incrementVersion(latestVersion, commits)
        logger.quiet("Next Version: $nextVersion")

        if (nextVersion == null) {
            // TODO properly fail how plugins should fail
            throw Exception("Nothing to publish")
        }

        project.version = nextVersion.toString(vPrefix = false)
    }
}

