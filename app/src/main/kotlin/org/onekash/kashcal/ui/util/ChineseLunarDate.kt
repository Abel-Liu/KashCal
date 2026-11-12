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

internal data class ChineseLunarDisplayLabel(val text: String, val isObservance: Boolean = false)

internal fun chineseLunarLabel(date: LocalDate, showFestivals: Boolean = false): String? =
    chineseLunarDisplayLabel(date, showObservances = showFestivals)?.text

/** Returns the label for a Gregorian civil date covered by the published table. */
internal fun chineseLunarDisplayLabel(
    date: LocalDate,
    showObservances: Boolean = true,
): ChineseLunarDisplayLabel? {
    if (date.year !in 1901..2100) return null

    val epochDay = date.toEpochDay()
    val index = lunarMonthIndex(epochDay)
    val monthStart = lunarMonthStarts[index]
    val day = (epochDay - monthStart.epochDay).toInt() + 1
    val month = monthStart.month
    if (showObservances) {
        // Lunar New Year's Eve is the day before the next first month, even in a short twelfth month.
        val nextMonth = lunarMonthStarts.getOrNull(index + 1)
        if (nextMonth?.month == 1 && nextMonth.epochDay == epochDay + 1) {
            return ChineseLunarDisplayLabel("除夕", isObservance = true)
        }
        val festival = when (month * 100 + day) {
            101 -> "春节"
            115 -> "元宵"
            505 -> "端午"
            707 -> "七夕"
            715 -> "中元"
            815 -> "中秋"
            909 -> "重阳"
            1208 -> "腊八"
            else -> null
        }
        val observance = festival ?: chineseSolarTerm(date)
        if (observance != null) return ChineseLunarDisplayLabel(observance, isObservance = true)
    }

    val label = if (day != 1) lunarDays[day - 1] else lunarMonthLabel(month)
    return ChineseLunarDisplayLabel(label)
}

/** The month and day together, for surfaces that only show one Gregorian date. */
internal fun chineseLunarFullDate(date: LocalDate): String? {
    if (date.year !in 1901..2100) return null
    val epochDay = date.toEpochDay()
    val monthStart = lunarMonthStarts[lunarMonthIndex(epochDay)]
    return lunarMonthLabel(monthStart.month) + lunarDays[(epochDay - monthStart.epochDay).toInt()]
}

private fun lunarMonthIndex(epochDay: Long): Int {
    val searchResult = lunarMonthStarts.binarySearchBy(epochDay) { it.epochDay }
    // For a non-start date, the insertion point is one past the current lunar month.
    return if (searchResult >= 0) searchResult else -searchResult - 2
}

private fun lunarMonthLabel(month: Int): String {
    val label = lunarMonths[abs(month) - 1]
    return if (month < 0) "闰$label" else label
}
