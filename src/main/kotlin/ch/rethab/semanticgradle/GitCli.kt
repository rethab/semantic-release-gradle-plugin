package ch.rethab.semanticgradle

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class GitCli(private val projectDir: File) {

    fun listCommits(): List<Commit> =
        runCommand(listOf("git", "log", "--pretty=oneline", "--decorate")).map { parseCommit(it) }

    fun tag(version: Version) =
        runCommand(listOf("git", "tag", version.toString(vPrefix = true)))

    fun push() =
        runCommand(listOf("git", "push", "--tags"))

    private fun runCommand(command: List<String>): List<String> {
        val process = ProcessBuilder(command).directory(projectDir).start()
        val errorLines = process.errorStream.reader().readLines()
        val outputLines = process.inputStream.reader().readLines()
        val completed = process.waitFor(5, TimeUnit.SECONDS)
        val exitCode = process.exitValue()

        if (!completed) {
            throw IOException("Command '${command.joinToString(" ")}' did not complete within 5 seconds")
        }

        if (exitCode != 0) {
            throw IOException("Command '${command.joinToString(" ")}' failed to run: " + errorLines.joinToString("\n"))
        }

        return outputLines
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
