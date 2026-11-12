package org.onekash.kashcal.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.onekash.kashcal.data.db.entity.Calendar
import org.onekash.kashcal.data.db.entity.Event
import org.onekash.kashcal.data.preferences.DefaultCalendar
import org.onekash.kashcal.domain.initializer.LocalCalendarInitializer
import org.onekash.kashcal.domain.model.AccountProvider
import org.onekash.kashcal.ui.model.CalendarGroup
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "en-rUS-w360dp-h9999dp-mdpi")
class EventFormInlineColorTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun pinLocale() = Locale.setDefault(Locale.US)

    private val local = Calendar(
        id = 1L,
        accountId = 1L,
        caldavUrl = LocalCalendarInitializer.LOCAL_CALENDAR_URL,
        displayName = LocalCalendarInitializer.LOCAL_CALENDAR_DISPLAY_NAME,
        color = 0xFF9E9E9E.toInt(),
    )
    private val offline = CalendarGroup(
        accountName = "Offline",
        accountId = 1L,
        calendars = listOf(local),
        provider = AccountProvider.LOCAL,
    )
    private val savedEvent = Event(id = 9L, uid = "form@test", calendarId = 1L, title = "Dentist", startTs = 0L, endTs = 0L, dtstamp = 0L)
    private val dodgerBlue = 0xFF1E90FF.toInt()
    @Test
    fun `editing an event preserves its existing color without a color picker`() {
        var captured: EventFormState? = null
        composeTestRule.setContent {
            MaterialTheme {
                EventFormContent(
                    modifier = Modifier.fillMaxSize(),
                    onSavingChange = {},
                    eventId = savedEvent.id,
                    onLoadEvent = { savedEvent.copy(color = dodgerBlue) },
                    calendars = listOf(local),
                    calendarGroups = listOf(offline),
                    defaultCalendar = DefaultCalendar.Room(1L),
                    onDismiss = {},
                    onSave = { captured = it; Result.success(savedEvent) },
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Event color", useUnmergedTree = true).assertDoesNotExist()
        composeTestRule.onNodeWithText("Save Event").performClick()
        composeTestRule.waitForIdle()

        assertEquals(dodgerBlue, checkNotNull(captured).eventColor)
    }
}
