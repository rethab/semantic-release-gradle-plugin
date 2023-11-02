package io.github.rethab.semanticrelease

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.lang.ProcessBuilder.Redirect.INHERIT
import java.util.concurrent.TimeUnit
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SemanticReleaseIntegrationTest {

    @TempDir
    lateinit var projectDir: File

    @TempDir
    lateinit var upstreamRepository: File

    lateinit var buildFile: File

    @BeforeEach
    fun setup() {
        buildFile = File(projectDir, "build.gradle")
        buildFile.createNewFile()
        buildFile.writeText(
            """
            plugins {
              id 'io.github.rethab.semantic-release'
              id 'maven-publish'
            }
            
            """.trimIndent(),
        )
    }

    @Test
    fun showsCommitUntilLatestVersion() {
        createRepository(
            listOf(
                "alpha" to "v1.0.0",
                "beta" to "v1.1.0",
                "chore: gamma" to null,
                "fix: delta" to null,
                "chore: epsilon" to null,
            ),
        )

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("semanticReleaseSetVersion", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertFalse(result.output.contains("alpha"), "'${result.output}' should not contain 'alpha'")
        assertFalse(result.output.contains("beta"), "'${result.output}' should not contain 'beta'")
        assertContains(result.output, "chore: gamma --> BUMP: NONE")
        assertContains(result.output, "fix: delta --> BUMP: PATCH")
        assertContains(result.output, "chore: epsilon --> BUMP: NONE")
        assertContains(result.output, "Next Version: v1.1.1")
        assertEquals(TaskOutcome.SUCCESS, result.task(":semanticReleaseSetVersion")?.outcome)
    }

    @Test
    fun shouldSetVersionInPom() {
        buildFile.appendText(
            """
            version = "0.0.0-SNAPSHOT"
            
            publishing {
              publications {
                mavenJava(MavenPublication) {}
              }
            }
            """.trimIndent(),
        )

        createRepository(
            listOf(
                "alpha" to "v1.0.0",
                "fix: delta" to null,
            ),
        )

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("generatePomFileForMavenJavaPublication", "--stacktrace")
            .withPluginClasspath()
            .build()

        println("OUTPUT: " + result.output)
        val pomFile = File(projectDir, "build/publications/mavenJava/pom-default.xml")
        assertContains(pomFile.readText(), "<version>1.0.1</version>")
        assertEquals(TaskOutcome.SUCCESS, result.task(":generatePomFileForMavenJavaPublication")?.outcome)
    }

    @Test
    fun shouldTagCommitAndPush() {
        buildFile.appendText(
            """
            version = "0.0.0-SNAPSHOT"
            
            tasks.named('semanticReleaseSetVersion') {
                finalizedBy semanticReleaseTagVersion
            }
            """.trimIndent(),
        )

        createRepository(
            listOf(
                "alpha" to "v1.0.0",
                "fix: delta" to null,
            ),
        )

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("semanticReleaseSetVersion", "--stacktrace")
            .withPluginClasspath()
            .build()

        println("OUTPUT: " + result.output)
        assertEquals(TaskOutcome.SUCCESS, result.task(":semanticReleaseSetVersion")?.outcome)
        assertContains(showTag(upstreamRepository, "v1.0.1"), "fix: delta")
    }

    private fun createRepository(commitsAndTags: List<Pair<String, String?>>) {
        runCommand(upstreamRepository, listOf("git", "init", "-b", "main"))
        runCommand(upstreamRepository, listOf("git", "config", "user.email", "test@example.com"))
        runCommand(upstreamRepository, listOf("git", "config", "user.name", "test"))

        commitsAndTags.forEach { (commit, tag) ->
            runCommand(upstreamRepository, listOf("git", "commit", "--allow-empty", "--message", commit))
            if (tag != null) {
                runCommand(upstreamRepository, listOf("git", "tag", tag))
            }
        }

        runCommand(projectDir, listOf("git", "init", "-b", "main"))
        runCommand(projectDir, listOf("git", "remote", "add", "origin", upstreamRepository.absolutePath))
        runCommand(projectDir, listOf("git", "pull", "origin", "main"))
    }

    private fun runCommand(dir: File, command: List<String>) {
        val process = ProcessBuilder(command)
            .directory(dir)
            .redirectOutput(INHERIT)
            .redirectError(INHERIT)
            .start()

        assertTrue(process.waitFor(2, TimeUnit.SECONDS), "Command failed: $command")
        assertEquals(0, process.exitValue())
    }

    private fun showTag(dir: File, tag: String): String {
        val process = ProcessBuilder(listOf("git", "show", tag)).directory(dir).start()
        val outputLines = process.inputStream.reader().readLines()
        assertTrue(process.waitFor(5, TimeUnit.SECONDS))
        assertEquals(0, process.exitValue())

        return outputLines.joinToString("\n")
    }
}
