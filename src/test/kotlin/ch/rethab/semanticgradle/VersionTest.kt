package ch.rethab.semanticgradle

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VersionTest {

    @Test
    fun shouldShowNiceString() {
        assertEquals("v1.2.3", Version(1, 2, 3).toString())
    }

    @Test
    fun shouldParseTag() {
        assertEquals(Version(1, 2, 3), Version.parseTag("v1.2.3"))
    }

    @Test
    fun shouldParseVersion() {
        assertEquals(Version(1, 2, 3), Version.parse("1.2.3"))
    }

}
