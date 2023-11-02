package io.github.rethab.semanticrelease

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

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

    @ParameterizedTest
    @CsvSource(
        "1.2.3,1.2.3,false",
        "1.2.3,1.2.2,true",
        "1.3.0,1.2.4,true",
        "1.3.0,1.2.3,true",
        "2.0.0,1.4.4,true",
        "2.0.0,1.0.1,true",
    )
    fun shouldImplementComparable(first: String, second: String, bigger: Boolean) {
        if (bigger) {
            assertEquals(Version.parse(first), maxOf(Version.parse(first)!!, Version.parse(second)!!))
            assertEquals(Version.parse(first), maxOf(Version.parse(second)!!, Version.parse(first)!!))
        } else {
            assertEquals(0, Version.parse(first)!!.compareTo(Version.parse(second)!!))
            assertEquals(0, Version.parse(second)!!.compareTo(Version.parse(first)!!))
        }
    }
}
