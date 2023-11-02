package io.github.rethab.semanticrelease

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SemanticAnalyzerTest {

    @ParameterizedTest
    @CsvSource(
        "chore: change config,NONE",
        "fix: fixed bug,PATCH",
        "feat: new thing,MINOR",
        "feat!: big change,MAJOR",
    )
    fun shouldIncrementMajorVersion(commitMessage: String, expectedVersionBump: VersionBump) {
        assertEquals(expectedVersionBump, SemanticAnalyzer().analyzeCommit(Commit(commitMessage, listOf())))
    }

    @Test
    fun shouldNotIncrementChore() {
        assertNull(
            SemanticAnalyzer().incrementVersion(
                Version(1, 1, 1),
                listOf(Commit("chore: foo", listOf())),
            ),
        )
    }

    @Test
    fun shouldNotIncrementEmptyCommits() {
        assertNull(
            SemanticAnalyzer().incrementVersion(
                Version(1, 1, 1),
                listOf(),
            ),
        )
    }

    @Test
    fun shouldIncrementMajorForBreakingChange() {
        assertEquals(
            Version(2, 0, 0),
            SemanticAnalyzer().incrementVersion(
                Version(1, 1, 1),
                listOf(
                    Commit("chore: foo", listOf()),
                    Commit("fix!: foo", listOf()),
                    Commit("chore: foo", listOf()),
                ),
            ),
        )
    }

    @Test
    fun shouldIncrementMinor() {
        assertEquals(
            Version(1, 2, 0),
            SemanticAnalyzer().incrementVersion(
                Version(1, 1, 1),
                listOf(
                    Commit("fix: foo", listOf()),
                    Commit("feat: foo", listOf()),
                    Commit("chore: foo", listOf()),
                ),
            ),
        )
    }

    @Test
    fun shouldIncrementPatch() {
        assertEquals(
            Version(1, 1, 2),
            SemanticAnalyzer().incrementVersion(
                Version(1, 1, 1),
                listOf(
                    Commit("fix: foo", listOf()),
                    Commit("chore: foo", listOf()),
                    Commit("fix: foo", listOf()),
                ),
            ),
        )
    }
}
