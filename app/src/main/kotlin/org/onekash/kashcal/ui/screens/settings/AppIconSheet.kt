package org.onekash.kashcal.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.onekash.kashcal.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.onekash.kashcal.ui.appicon.AppIconPreset
import org.onekash.kashcal.ui.appicon.TodayDateShortcut
import org.onekash.kashcal.ui.appicon.isDateIconDark
import org.onekash.kashcal.ui.appicon.renderTodayDateIcon
import java.time.LocalDate

/** The heart color of the supporter launcher icon, reused for the donate cue in the sheet. */
private val SupporterHeartRed = Color(0xFFD6304A)

/**
 * Shows launcher icon choices and a row to pin the dynamic today shortcut.
 *
 * Layout: the icon options as one uninterrupted list, a divider, a "Support KashCal" footer link
 * (the honor-system nudge tied to the supporter icons), and an inline note on what switching
 * does. The note stays in the sheet instead of a blocking dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppIconSheet(
    sheetState: SheetState,
    currentPreset: AppIconPreset,
    onPresetSelect: (AppIconPreset) -> Unit,
    onSupportClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val shortcutError = stringResource(R.string.app_icon_today_shortcut_error)
    // Preview the shortcut for the day the sheet opened.
    val today = remember { LocalDate.now() }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
                // Groups the icon option rows for TalkBack "N of M"; the footer link and
                // info note aren't selectable, so they stay outside the radio group.
                .selectableGroup(),
        ) {
            Text(
                text = stringResource(R.string.settings_app_icon),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            )

            AppIconPreset.entries.forEach { preset ->
                AppIconOptionRow(
                    preset = preset,
                    today = today,
                    isSelected = currentPreset == preset,
                    onSelect = {
                        onPresetSelect(preset)
                        onDismiss()
                    },
                )
            }

            AppIconOptionRow(
                preset = AppIconPreset.TODAYS_DATE,
                today = today,
                isSelected = false,
                isShortcut = true,
                onSelect = {
                    scope.launch {
                        val added = withContext(Dispatchers.IO) {
                            runCatching { TodayDateShortcut.addOrRefresh(context) }.getOrDefault(false)
                        }
                        if (added) onDismiss()
                        else Toast.makeText(context, shortcutError, Toast.LENGTH_SHORT).show()
                    }
                },
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 8.dp),
            )

            // Donate nudge for the supporter icons.
            val supportCta = stringResource(R.string.app_icon_support_cta)
            val opensInBrowser = stringResource(R.string.cd_opens_in_browser)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(role = Role.Button) { onSupportClick() }
                    // The OpenInNew glyph is the only "leaves the app" cue and is
                    // decorative to TalkBack, so fold it into the row's merged label.
                    .semantics(mergeDescendants = true) {
                        contentDescription = "$supportCta, $opensInBrowser"
                    }
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    // A red heart matching the supporter launcher icon reads as a donate cue,
                    // not a UI accent.
                    tint = SupporterHeartRed,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = supportCta,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            }

            // What to expect when switching.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp).padding(top = 2.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.app_icon_switch_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun AppIconOptionRow(
    preset: AppIconPreset,
    today: LocalDate,
    isSelected: Boolean,
    onSelect: () -> Unit,
    isShortcut: Boolean = false,
) {
    val interaction = if (isShortcut) {
        Modifier.clickable(role = Role.Button, onClick = onSelect)
    } else {
        Modifier.selectable(selected = isSelected, role = Role.RadioButton, onClick = onSelect)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(interaction)
            .background(
                if (isSelected && !isShortcut) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else Color.Transparent,
            )
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Composites the adaptive icon's foreground over its background color, as
            // AppLockVeil does: painterResource can't inflate the adaptive-icon XML itself.
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        colorResource(
                            if (isShortcut || preset == AppIconPreset.DEFAULT) R.color.ic_launcher_date_background
                            else R.color.ic_launcher_background
                        )
                    ),
                contentAlignment = Alignment.Center,
            ) {
                val iconModifier = Modifier
                    .fillMaxSize()
                    .scale(AppIconPreset.PREVIEW_FOREGROUND_SCALE)
                if (isShortcut) {
                    val configuration = LocalConfiguration.current
                    val locale = configuration.locales[0]
                    val dark = configuration.isDateIconDark()
                    val bitmap = remember(today, locale, dark) {
                        renderTodayDateIcon(today, locale, dark).asImageBitmap()
                    }
                    Image(bitmap = bitmap, contentDescription = null, modifier = iconModifier)
                } else {
                    Image(
                        painter = painterResource(preset.previewForegroundFor(today)),
                        contentDescription = null,
                        modifier = iconModifier,
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = stringResource(if (isShortcut) R.string.app_icon_today_shortcut_title else preset.labelRes),
                    style = MaterialTheme.typography.bodyLarge,
                )
                if (isShortcut) {
                    Text(
                        text = stringResource(R.string.app_icon_today_shortcut_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        if (isSelected && !isShortcut) {
            Icon(
                imageVector = Icons.Default.Check,
                // Decorative: the row's radio-button selected state already announces selection.
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}
