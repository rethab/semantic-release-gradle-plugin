package ch.rethab.semanticgradle

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class GitFacadeTest {

    @Mock
    lateinit var gitCli: GitCli

    @InjectMocks
    lateinit var gitFacade: GitFacade

    @Test
    fun shouldReturnLatestVersion() {
        `when`(gitCli.listTags()).thenReturn(listOf(
            Version(1, 1, 5),
            Version(1, 2, 3),
            Version(0, 1, 3),
        ))

        val latestVersion = gitFacade.findLatestVersion()

        assertEquals(Version(1, 2, 3), latestVersion)
    }

    @Test
    fun shouldReturnCommitsSinceVersion() {
        `when`(gitCli.listCommits()).thenReturn(listOf(
            Commit("a", listOf()),
            Commit("b", listOf("v2.2.0")),
            Commit("c", listOf()),
            Commit("d", listOf("v1.2.0")),
            Commit("e", listOf("v1.1.0")),
            Commit("f", listOf("v1.0.0")),
        ))

        val commits = gitFacade.findCommitsSince(Version(1, 1, 0))

        assertEquals(listOf("a", "b", "c", "d"), commits.map { it.message })
    }

}
