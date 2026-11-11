package org.onekash.kashcal.ui.util

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ChineseLunarDateTest {
    @Test
    fun formatsNewYearAndFollowingDay() {
        assertEquals("正月", chineseLunarLabel(LocalDate.of(2025, 1, 29)))
        assertEquals("初二", chineseLunarLabel(LocalDate.of(2025, 1, 30)))
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
