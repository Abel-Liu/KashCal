package org.onekash.kashcal.ui.util

import android.icu.util.Calendar
import android.icu.util.ChineseCalendar
import android.icu.util.TimeZone
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

private val chinaOffset = ZoneOffset.ofHours(8)
private val lunarMonths = listOf(
    "正月", "二月", "三月", "四月", "五月", "六月",
    "七月", "八月", "九月", "十月", "冬月", "腊月",
)
private val lunarDays = listOf(
    "初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十",
    "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
    "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十",
)

internal fun chineseLunarLabel(date: LocalDate): String {
    // Convert the displayed civil date at noon in China, independent of the device time zone.
    val lunar = ChineseCalendar().apply {
        timeZone = TimeZone.getTimeZone("GMT+08:00")
        timeInMillis = date.atTime(LocalTime.NOON).toInstant(chinaOffset).toEpochMilli()
    }
    val day = lunar.get(Calendar.DAY_OF_MONTH)
    if (day != 1) return lunarDays[day - 1]

    val month = lunarMonths[lunar.get(Calendar.MONTH)]
    return if (lunar.get(Calendar.IS_LEAP_MONTH) == 1) "闰$month" else month
}
