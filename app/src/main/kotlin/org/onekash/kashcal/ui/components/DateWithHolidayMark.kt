package org.onekash.kashcal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import org.onekash.kashcal.R
import org.onekash.kashcal.ui.util.ChineseHolidayMark
import org.onekash.kashcal.ui.util.DayPagerUtils
import org.onekash.kashcal.ui.util.chineseHolidayMark

@Composable
internal fun DateWithHolidayMark(
    date: LocalDate,
    markSize: TextUnit = 8.sp,
    compact: Boolean = false,
    content: @Composable () -> Unit,
) {
    val mark = remember(date) { chineseHolidayMark(date) }
    if (mark == null) {
        content()
        return
    }
    val isRest = mark == ChineseHolidayMark.REST
    val description = stringResource(if (isRest) R.string.cd_chinese_holiday_rest else R.string.cd_chinese_holiday_work)
    Row {
        Box(modifier = Modifier.weight(1f, fill = false)) { content() }
        Text(
            text = if (isRest) "休" else "班",
            color = Color.White,
            style = TextStyle(fontSize = markSize, lineHeight = markSize * 1.2f),
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier
                .clearAndSetSemantics { contentDescription = description }
                .background(if (isRest) Color(0xFFAD303C) else Color(0xFF205DB0), RoundedCornerShape(2.dp))
                .padding(horizontal = if (compact) 0.dp else 1.dp),
        )
    }
}

@Composable
internal fun DateWithHolidayMark(
    dayCode: Int,
    markSize: TextUnit = 8.sp,
    compact: Boolean = false,
    content: @Composable () -> Unit,
) {
    val date = remember(dayCode) { DayPagerUtils.dayCodeToLocalDate(dayCode) }
    DateWithHolidayMark(date, markSize, compact, content)
}
