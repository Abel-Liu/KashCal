package org.onekash.kashcal.ui.util

import java.security.MessageDigest
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ChineseLunarDateTest {
    @Test
    fun matchesEveryDateInThePublishedTable() {
        val digest = MessageDigest.getInstance("SHA-256")
        val end = LocalDate.of(2101, 1, 1)
        var date = LocalDate.of(1901, 1, 1)
        while (date < end) {
            val label = checkNotNull(chineseLunarLabel(date)) { "Missing lunar date for $date" }
            digest.update("$label\n".toByteArray(Charsets.UTF_8))
            date = date.plusDays(1)
        }
        // Computed from the Observatory's 200 original daily text tables, one normalized label per line.
        assertEquals(
            "d4ab3c7162f7d3d4c342c7935afac6456450af828a6590c3377dbfac1e7a1e16",
            digest.digest().joinToString("") { "%02x".format(it.toInt() and 0xff) },
        )
    }

    @Test
    fun leavesDatesOutsideThePublishedRangeUnlabeled() {
        assertNull(chineseLunarLabel(LocalDate.of(1900, 12, 31)))
        assertNull(chineseLunarLabel(LocalDate.of(2101, 1, 1)))
    }

    @Test
    fun formatsNewYearAndFollowingDay() {
        assertEquals("正月", chineseLunarLabel(LocalDate.of(2025, 1, 29)))
        assertEquals("初二", chineseLunarLabel(LocalDate.of(2025, 1, 30)))
    }

    @Test
    fun followsPublishedDatesForThe2027NewYear() {
        assertEquals("廿九", chineseLunarLabel(LocalDate.of(2027, 2, 5)))
        assertEquals("正月", chineseLunarLabel(LocalDate.of(2027, 2, 6)))
        assertEquals("初二", chineseLunarLabel(LocalDate.of(2027, 2, 7)))
        assertEquals("三十", chineseLunarLabel(LocalDate.of(2027, 3, 7)))
        assertEquals("二月", chineseLunarLabel(LocalDate.of(2027, 3, 8)))
    }

    @Test
    fun distinguishesRegularAndLeapMonths() {
        assertEquals("六月", chineseLunarLabel(LocalDate.of(2025, 6, 25)))
        assertEquals("闰六月", chineseLunarLabel(LocalDate.of(2025, 7, 25)))
    }

    @Test
    fun formatsMidMonthDay() {
        assertEquals("十五", chineseLunarLabel(LocalDate.of(2025, 10, 6)))
    }
}
