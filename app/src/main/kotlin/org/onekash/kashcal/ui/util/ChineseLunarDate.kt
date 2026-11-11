package org.onekash.kashcal.ui.util

import java.time.LocalDate
import kotlin.math.abs

private val lunarMonths = listOf(
    "正月", "二月", "三月", "四月", "五月", "六月",
    "七月", "八月", "九月", "十月", "冬月", "腊月",
)
private val lunarDays = listOf(
    "初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十",
    "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
    "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十",
)

private data class LunarMonthStart(val epochDay: Long, val month: Int)

private val lunarMonthStarts by lazy {
    buildList {
        for (row in CHINESE_LUNAR_MONTH_STARTS.trim().lineSequence()) {
            val fields = row.split(' ')
            val year = fields.first().toInt()
            for (field in fields.drop(1)) {
                val (monthDay, lunarMonth) = field.split(':')
                val dateCode = monthDay.toInt()
                val date = LocalDate.of(year, dateCode / 100, dateCode % 100)
                add(LunarMonthStart(date.toEpochDay(), lunarMonth.toInt()))
            }
        }
    }
}

/** Returns the label for a Gregorian civil date covered by the published table. */
internal fun chineseLunarLabel(date: LocalDate): String? {
    if (date.year !in 1901..2100) return null

    val epochDay = date.toEpochDay()
    val index = lunarMonthStarts.binarySearchBy(epochDay) { it.epochDay }
    if (index < 0) {
        // The insertion point is one past the start of the current lunar month.
        val monthStart = lunarMonthStarts[-index - 2]
        return lunarDays[(epochDay - monthStart.epochDay).toInt()]
    }

    val month = lunarMonthStarts[index].month
    val label = lunarMonths[abs(month) - 1]
    return if (month < 0) "闰$label" else label
}
