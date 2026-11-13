package org.onekash.kashcal.ui.components.hub

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.onekash.kashcal.BuildConfig
import org.onekash.kashcal.ui.theme.KashCalTheme
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Locale

/**
 * Compose tests for [AccountHubScreen]: each row invokes its own callback, the back arrow, the
 * hero's edit and save of initials, the three section headers with no nested Widgets sub-header,
 * the app-lock and app-permissions rows, and the version footer. Opening and closing the
 * overlay lives in the caller and isn't tested here. The `makeItYours` slot is stubbed so no Hilt
 * graph is needed, and a test checks it renders under its section header. Run the class alone:
 * Robolectric runs of more than one class hit a native crash.
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [34], qualifiers = "w360dp-h9999dp-mdpi")
class AccountHubScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var originalLocale: Locale? = null

    @Before
    fun pinLocale() {
        originalLocale = Locale.getDefault()
        Locale.setDefault(Locale.US)
    }

    @After
    fun restoreLocale() {
        originalLocale?.let { Locale.setDefault(it) }
    }

    private class Callbacks {
        var invites = 0
        var settings = 0
        var back = 0
        var initials: String? = null
        var appLockToggled: Boolean? = null
        var appPermissions = 0
        var recentChanges = 0
        var printWeek = 0
    }

    private fun render(
        userInitials: String = "KC",
        pendingInvitesCount: Int = 0,
        appLockEnabled: Boolean = false,
        callbacks: Callbacks = Callbacks(),
    ) {
        composeTestRule.setContent {
            KashCalTheme {
                AccountHubScreen(
                    pendingInvitesCount = pendingInvitesCount,
                    userInitials = userInitials,
                    onInitialsChange = { callbacks.initials = it },
                    onInvitesClick = { callbacks.invites++ },
                    onSettingsClick = { callbacks.settings++ },
                    onBack = { callbacks.back++ },
                    appLockEnabled = appLockEnabled,
                    onToggleAppLock = { callbacks.appLockToggled = it },
                    onAppPermissionsClick = { callbacks.appPermissions++ },
                    onRecentChangesClick = { callbacks.recentChanges++ },
                    onPrintWeekClick = { callbacks.printWeek++ },
                    // Stub the VM-backed personalization slot so no Hilt graph is needed.
                    makeItYours = { Text("make-it-yours-stub") },
                )
            }
        }
    }

    @Test
    fun `each destination row invokes only its own callback`() {
        val cb = Callbacks()
        render(callbacks = cb)

        composeTestRule.onNodeWithText("Invites").performClick()
        assertEquals(1, cb.invites)

        composeTestRule.onNodeWithText("Print your week").performClick()
        assertEquals(1, cb.printWeek)

        composeTestRule.onNodeWithText("Accounts & Settings").performClick()
        assertEquals(1, cb.settings)

        composeTestRule.onNodeWithText("Recent changes").performClick()
        assertEquals(1, cb.recentChanges)

        // No row's tap fired a sibling's callback.
        assertEquals(1, cb.invites)
        assertEquals(1, cb.printWeek)
        assertEquals(0, cb.back)
    }

    @Test
    fun `recent changes sits directly below invites with no count badge`() {
        render(pendingInvitesCount = 3)

        val invites = composeTestRule.onNodeWithText("Invites").getUnclippedBoundsInRoot()
        val recent = composeTestRule.onNodeWithText("Recent changes").getUnclippedBoundsInRoot()
        val printWeek = composeTestRule.onNodeWithText("Print your week").getUnclippedBoundsInRoot()
        assertTrue("below Invites", recent.top > invites.top)
        assertTrue("above Print your week", recent.top < printWeek.top)
        // Invites shows its count; Recent changes adds no second badge.
        composeTestRule.onAllNodesWithText("3").assertCountEquals(1)
    }

    @Test
    fun `back arrow invokes the back callback`() {
        val cb = Callbacks()
        render(callbacks = cb)
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assertEquals(1, cb.back)
    }

    @Test
    fun `hero edit then save persists normalized initials`() {
        val cb = Callbacks()
        render(userInitials = "KC", callbacks = cb)

        // Tap the avatar to enter edit mode.
        composeTestRule.onNodeWithContentDescription("Edit your initials").performClick()
        composeTestRule.waitForIdle()

        // Replace the draft and save; the input is normalized to two uppercase letters.
        composeTestRule.onNodeWithText("KC").performTextClearance()
        composeTestRule.onNodeWithText("Initials").performTextInput("ann")
        composeTestRule.onNodeWithText("Save").performClick()

        assertEquals("AN", cb.initials)
    }

    @Test
    fun `personalization slot renders under its section header`() {
        render()
        composeTestRule.onNodeWithText("Make it yours").assertIsDisplayed()
        composeTestRule.onNodeWithText("make-it-yours-stub").assertIsDisplayed()
    }

    @Test
    fun `all three section headers render`() {
        render()
        composeTestRule.onNodeWithText("Make it yours").assertIsDisplayed()
        composeTestRule.onNodeWithText("Own your calendar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Privacy & Security").assertIsDisplayed()
    }

    @Test
    fun `the nested Widgets sub-header is gone`() {
        render()
        composeTestRule.onAllNodesWithText("Widgets").assertCountEquals(0)
    }

    @Test
    fun `app-lock row reflects state and toggling invokes the callback`() {
        val cb = Callbacks()
        render(appLockEnabled = false, callbacks = cb)
        composeTestRule.onNodeWithText("App lock").performClick()
        assertEquals(true, cb.appLockToggled)
    }

    @Test
    fun `app-permissions row renders and invokes its callback`() {
        val cb = Callbacks()
        render(callbacks = cb)
        composeTestRule.onNodeWithText("App permissions").performClick()
        assertEquals(1, cb.appPermissions)
    }

    @Test
    fun `removed entries are absent and version has no click action`() {
        render()
        composeTestRule.onNodeWithText("Go to date").assertDoesNotExist()
        composeTestRule.onNodeWithText("Share availability").assertDoesNotExist()
        composeTestRule.onNodeWithText("Manage tags").assertDoesNotExist()
        composeTestRule.onNodeWithText("About").assertDoesNotExist()
        composeTestRule.onNodeWithText("How your data stays yours").assertDoesNotExist()
        composeTestRule.onNodeWithText("Version ${BuildConfig.VERSION_NAME}")
            .assertIsDisplayed()
            .assertHasNoClickAction()
    }
}
