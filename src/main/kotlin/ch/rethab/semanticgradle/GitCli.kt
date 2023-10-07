package ch.rethab.semanticgradle

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class GitCli(private val projectDir: File) {

    fun listCommits(): List<Commit> {
        val process = ProcessBuilder(listOf("git", "log", "--pretty=oneline", "--decorate"))
            .directory(projectDir)
            .start()
        val errorLines = process.errorStream.reader().readLines()
        val outputLines = process.inputStream.reader().readLines()
        val completed = process.waitFor(5, TimeUnit.SECONDS)
        val exitCode = process.exitValue()

        if (!completed) {
            throw IOException("git log did not complete within 5 seconds")
        }

        if (exitCode != 0) {
            // TODO: error message is not shown
            throw IOException("git log failed to run: " + errorLines.joinToString { "," })
        }

        return outputLines.map { parseCommit(it) }
    }

    companion object {

        private val commitRegex = Regex("""\w+ (?:\(([^)]+)\) )?(.*)""")

        fun parseCommit(line: String): Commit {
            // TODO fails to parse commit messages that start with ( but have no tags
            // TODO remove !!
            return commitRegex.matchEntire(line)!!.let { result ->
                val (refs, message) = result.groupValues.drop(1)
                val tags = refs.split(',').map { it.trim() }.filter { it.startsWith("tag: ") }.map { it.drop(5) }
                Commit(message, tags)
            }
        }
    }

}
