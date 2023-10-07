package ch.rethab.semanticgradle

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VersionTest {

    @Test
    fun shouldShowNiceString() {
        assertEquals("v1.2.3", Version(1, 2, 3).toString())
    }

}
