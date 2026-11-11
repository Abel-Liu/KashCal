package org.onekash.kashcal.ui.util

import java.time.LocalDate

internal enum class ChineseHolidayMark { REST, WORK }

private class HolidayYear(
    val holidays: List<IntRange>,
    val makeupWorkdays: Set<Int>,
)

// The month/day values use MMDD. Each year is transcribed from its State Council notice.
private val holidaySchedule = mapOf(
    // https://www.gov.cn/zhengce/zhengceku/202411/content_6986383.htm
    2025 to HolidayYear(
        holidays = listOf(101..101, 128..204, 404..406, 501..505, 531..602, 1001..1008),
        makeupWorkdays = setOf(126, 208, 427, 928, 1011),
    ),
    // https://www.gov.cn/zhengce/zhengceku/202511/content_7047091.htm
    2026 to HolidayYear(
        holidays = listOf(101..103, 215..223, 404..406, 501..505, 619..621, 925..927, 1001..1007),
        makeupWorkdays = setOf(104, 214, 228, 509, 920, 1010),
    ),
    // https://www.gov.cn/zhengce/content/202611/content_7085101.htm
    2027 to HolidayYear(
        holidays = listOf(101..103, 205..213, 403..405, 501..505, 609..609, 915..915, 1001..1007),
        makeupWorkdays = setOf(131, 214, 508, 926, 1009),
    ),
)

internal fun chineseHolidayMark(date: LocalDate): ChineseHolidayMark? {
    val schedule = holidaySchedule[date.year] ?: return null
    val monthDay = date.monthValue * 100 + date.dayOfMonth
    return when {
        monthDay in schedule.makeupWorkdays -> ChineseHolidayMark.WORK
        schedule.holidays.any { monthDay in it } -> ChineseHolidayMark.REST
        else -> null
    }
}
