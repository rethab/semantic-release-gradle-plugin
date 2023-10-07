package ch.rethab.semanticgradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.lang.ProcessBuilder.Redirect.*
import java.util.concurrent.TimeUnit
import kotlin.test.assertContains

class SemanticGradleIntegrationTest {

    @TempDir
    lateinit var buildDir: File

    lateinit var buildFile: File

    @BeforeEach
    fun setup() {
        buildFile = File(buildDir, "build.gradle")
        buildFile.createNewFile()
        buildFile.writeText("""
            plugins {
              id 'ch.rethab.semantic-gradle'
              id 'maven-publish'
            }
            
        """.trimIndent())
    }

    @Test
    fun showsCommitUntilLatestVersion() {
        buildFile.appendText("""
            semanticGradle {
              
            }
        """.trimIndent())
        createRepository()
        createCommit("alpha", "v1.0.0")
        createCommit("beta", "v1.1.0")
        createCommit("chore: gamma")
        createCommit("fix: delta")
        createCommit("chore: epsilon")

        println(buildFile.readText())

        val result = GradleRunner.create()
            .withProjectDir(buildDir)
            .withArguments("semanticGradleSetVersion", "--stacktrace")
            .withPluginClasspath()
            .build()

        assertFalse(result.output.contains("alpha"), "'${result.output}' should not contain 'alpha'")
        assertFalse(result.output.contains("beta"), "'${result.output}' should not contain 'beta'")
        assertContains(result.output, "chore: gamma --> BUMP: NONE")
        assertContains(result.output, "fix: delta --> BUMP: PATCH")
        assertContains(result.output, "chore: epsilon --> BUMP: NONE")
        assertContains(result.output, "Next Version: v1.1.1")
        assertEquals(TaskOutcome.SUCCESS, result.task(":semanticGradleSetVersion")?.outcome)
    }

    @Test
    fun shouldSetVersionInPom() {
        buildFile.appendText("""
            version = "0.0.0-SNAPSHOT"
            
            publishing {
              publications {
                mavenJava(MavenPublication) {}
              }
            }
        """.trimIndent())

        createRepository()
        createCommit("alpha", "v1.0.0")
        createCommit("fix: delta")

        println(buildFile.readText())

        val result = GradleRunner.create()
            .withProjectDir(buildDir)
            .withArguments("generatePomFileForMavenJavaPublication", "--stacktrace")
            .withPluginClasspath()
            .build()

        println("OUTPUT: " + result.output)
        val pomFile = File(buildDir, "build/publications/mavenJava/pom-default.xml")
        assertContains(pomFile.readText(), "<version>1.0.1</version>")
        assertEquals(TaskOutcome.SUCCESS, result.task(":generatePomFileForMavenJavaPublication")?.outcome)
    }

    private fun createCommit(message: String, tag: String? = null) {
        runCommand(listOf("git", "commit", "--allow-empty", "-m", message))
        if (tag != null) {
            runCommand(listOf("git", "tag", tag))
        }
    }

    private fun createRepository() {
        runCommand(listOf("git", "init"))
        runCommand(listOf("git", "config", "user.email", "you@example.com"))
    }

    private fun runCommand(command: List<String>) {
        val process = ProcessBuilder(command)
            .directory(buildDir)
            .redirectOutput(INHERIT)
            .redirectError(INHERIT)
            .start()

        assertTrue(process.waitFor(2, TimeUnit.SECONDS), "Command failed: $command")
        assertEquals(0, process.exitValue())

    }

}
