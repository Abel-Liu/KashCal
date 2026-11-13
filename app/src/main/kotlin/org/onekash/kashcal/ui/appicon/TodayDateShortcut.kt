package org.onekash.kashcal.ui.appicon

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Icon
import android.util.Log
import org.onekash.kashcal.MainActivity
import org.onekash.kashcal.R
import org.onekash.kashcal.ui.util.chineseLunarFullDate
import org.onekash.kashcal.widget.ACTION_GO_TO_TODAY
import org.onekash.kashcal.widget.EXTRA_ACTION
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

private const val TODAY_DATE_SHORTCUT_ID = "today_date_icon"
private const val BITMAP_SIZE = 216

internal object TodayDateShortcut {
    private var lastUpdate: Pair<LocalDate, Locale>? = null

    @Synchronized
    fun addOrRefresh(context: Context, date: LocalDate = LocalDate.now()): Boolean {
        val manager = context.getSystemService(ShortcutManager::class.java) ?: return false
        if (manager.isPinned()) return update(context, manager, date)
        if (!manager.isRequestPinShortcutSupported) return false
        return manager.requestPinShortcut(shortcut(context, date), null)
    }

    @Synchronized
    fun refresh(context: Context, date: LocalDate = LocalDate.now()) {
        try {
            val manager = context.getSystemService(ShortcutManager::class.java) ?: return
            val key = date to context.resources.configuration.locales[0]
            if (lastUpdate != key && manager.isPinned() && !update(context, manager, date)) {
                Log.d("TodayDateShortcut", "Shortcut update deferred until the next refresh")
            }
        } catch (e: RuntimeException) {
            Log.w("TodayDateShortcut", "Shortcut refresh failed", e)
        }
    }

    private fun ShortcutManager.isPinned() = pinnedShortcuts.any { it.id == TODAY_DATE_SHORTCUT_ID }

    private fun update(context: Context, manager: ShortcutManager, date: LocalDate): Boolean {
        if (!manager.updateShortcuts(listOf(shortcut(context, date)))) return false
        lastUpdate = date to context.resources.configuration.locales[0]
        return true
    }

    private fun shortcut(context: Context, date: LocalDate): ShortcutInfo =
        ShortcutInfo.Builder(context, TODAY_DATE_SHORTCUT_ID)
            .setShortLabel(context.getString(R.string.app_icon_label_calendar))
            .setIcon(Icon.createWithAdaptiveBitmap(renderTodayDateIcon(date, context.resources.configuration.locales[0])))
            .setIntent(
                Intent(Intent.ACTION_VIEW, null, context, MainActivity::class.java)
                    .putExtra(EXTRA_ACTION, ACTION_GO_TO_TODAY)
            )
            .build()
}

/** Uses the adaptive icon's center safe area, leaving the launcher to apply its mask. */
internal fun renderTodayDateIcon(date: LocalDate, locale: Locale): Bitmap {
    val bitmap = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(0xFFFAFAFC.toInt())
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER }

    fun line(text: String, size: Float, centerY: Float, color: Int, face: Typeface) {
        paint.typeface = face
        paint.textSize = size
        val width = paint.measureText(text)
        if (width > 104f) paint.textSize *= 104f / width
        paint.color = color
        val baseline = centerY - (paint.ascent() + paint.descent()) / 2
        canvas.drawText(text, BITMAP_SIZE / 2f, baseline, paint)
    }

    line(date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale), 21f, 68f, 0xFFB32642.toInt(), Typeface.DEFAULT_BOLD)
    line(date.dayOfMonth.toString(), 66f, 106f, 0xFF191F32.toInt(), Typeface.create("sans-serif-medium", Typeface.NORMAL))
    chineseLunarFullDate(date)?.let {
        line(it, 19f, 150f, 0xFF455778.toInt(), Typeface.DEFAULT)
    }
    return bitmap
}
