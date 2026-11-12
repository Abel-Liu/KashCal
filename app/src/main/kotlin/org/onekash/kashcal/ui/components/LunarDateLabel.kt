package org.onekash.kashcal.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import org.onekash.kashcal.ui.util.DayPagerUtils
import org.onekash.kashcal.ui.util.chineseLunarDisplayLabel

internal val LocalShowChineseLunarDate = staticCompositionLocalOf { false }

internal val lunarDateStyle = TextStyle(fontSize = 10.sp, lineHeight = 12.sp)

private val lunarObservanceRedOnLight = Color(0xFFB3261E)
private val lunarObservanceRedOnDark = Color(0xFFFFB4AB)

@Composable
internal fun LunarDateLabel(
    date: LocalDate,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    style: TextStyle = lunarDateStyle,
) {
    if (!LocalShowChineseLunarDate.current) return
    val label = remember(date) { chineseLunarDisplayLabel(date) } ?: return
    val labelColor = if (label.isObservance) {
        if (MaterialTheme.colorScheme.surface.luminance() < 0.5f) {
            lunarObservanceRedOnDark
        } else {
            lunarObservanceRedOnLight
        }
    } else {
        color.copy(alpha = color.alpha * 0.75f)
    }
    Text(
        text = label.text,
        modifier = modifier,
        color = labelColor,
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
