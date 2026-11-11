package org.onekash.kashcal.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import org.onekash.kashcal.ui.util.DayPagerUtils
import org.onekash.kashcal.ui.util.chineseLunarLabel

internal val LocalShowChineseLunarDate = staticCompositionLocalOf { false }

internal val lunarDateStyle = TextStyle(fontSize = 10.sp, lineHeight = 12.sp)

@Composable
internal fun LunarDateLabel(
    date: LocalDate,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    style: TextStyle = lunarDateStyle,
) {
    if (!LocalShowChineseLunarDate.current) return
    val label = remember(date) { chineseLunarLabel(date, showFestivals = true) } ?: return
    Text(
        text = label,
        modifier = modifier,
        color = color.copy(alpha = color.alpha * 0.75f),
        style = style,
        textAlign = TextAlign.Center,
        maxLines = 1,
        softWrap = false,
    )
}

@Composable
internal fun LunarDateLabel(
    dayCode: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    style: TextStyle = lunarDateStyle,
) {
    if (!LocalShowChineseLunarDate.current) return
    val date = remember(dayCode) { DayPagerUtils.dayCodeToLocalDate(dayCode) }
    LunarDateLabel(date, modifier, color, style)
}
