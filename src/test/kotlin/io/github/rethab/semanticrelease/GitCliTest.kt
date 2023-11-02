package io.github.rethab.semanticrelease

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GitCliTest {

    @Test
    fun shouldParseCommitFromString() {
        val commit =
            GitCli.parseCommit("1591e0 (HEAD -> main, tag: v1.2.3, origin/main, origin/HEAD) fix(component): in bar")

        assertEquals(Commit("fix(component): in bar", listOf("v1.2.3")), commit)
    }

    @Test
    fun shouldParseCommitWithoutTags() {
        val commit = GitCli.parseCommit("1591e0 fix: delta")

        assertEquals(Commit("fix: delta", listOf()), commit)
    }
}
