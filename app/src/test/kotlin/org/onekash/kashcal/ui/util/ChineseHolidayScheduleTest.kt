package org.onekash.kashcal.ui.util

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ChineseHolidayScheduleTest {
    @Test
    fun marksOfficialHolidayRangesIncludingWeekends() {
        assertEquals(ChineseHolidayMark.REST, chineseHolidayMark(LocalDate.of(2025, 2, 4)))
        assertEquals(ChineseHolidayMark.REST, chineseHolidayMark(LocalDate.of(2026, 2, 15)))
        assertEquals(ChineseHolidayMark.REST, chineseHolidayMark(LocalDate.of(2027, 10, 7)))
    }

    @Test
    fun marksOfficialMakeupWorkdays() {
        assertEquals(ChineseHolidayMark.WORK, chineseHolidayMark(LocalDate.of(2025, 4, 27)))
        assertEquals(ChineseHolidayMark.WORK, chineseHolidayMark(LocalDate.of(2026, 2, 14)))
        assertEquals(ChineseHolidayMark.WORK, chineseHolidayMark(LocalDate.of(2027, 10, 9)))
    }

    @Test
    fun leavesRegularDaysAndUnpublishedYearsUnmarked() {
        assertNull(chineseHolidayMark(LocalDate.of(2027, 10, 8)))
        assertNull(chineseHolidayMark(LocalDate.of(2027, 10, 10)))
        assertNull(chineseHolidayMark(LocalDate.of(2028, 1, 1)))
    }
}
