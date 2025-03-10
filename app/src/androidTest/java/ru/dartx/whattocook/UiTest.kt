package ru.dartx.whattocook

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test

class UiTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun userFlowTest() {
        composeTestRule.onNodeWithTag("search_button", true).performClick()
        composeTestRule.onNodeWithTag("search_field", true)
            .assertTextContains("")
            .performTextInput("Beaver")

        composeTestRule.waitUntil(10_000) {
            composeTestRule
                .onAllNodesWithText("BeaverTails")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("BeaverTails")
            .performClick()

        composeTestRule.waitUntil(10_000) {
            composeTestRule
                .onAllNodesWithText("BeaverTails")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("recalc_button", true).performClick()

        composeTestRule.waitUntil(10_000) {
            composeTestRule
                .onAllNodesWithTag("ingredients_recalc_button", true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("coefficient", true)
            .assertTextContains("")
            .performTextInput("1.5")

        composeTestRule.onNodeWithTag("ingredients_recalc_button", true).performClick()

        composeTestRule.onNodeWithTag("back_arrow", true).performClick()

        composeTestRule.waitUntil(10_000) {
            composeTestRule
                .onAllNodesWithText("BeaverTails")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("save_button", true).performClick()

        composeTestRule.onNodeWithTag("back_arrow", true).performClick()

        composeTestRule.waitUntil(10_000) {
            composeTestRule
                .onAllNodesWithText("BeaverTails")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("item_save_button", true).performClick()

        composeTestRule.waitUntil(10_000) {
            composeTestRule
                .onAllNodesWithText("BeaverTails")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("close_search_button", true).performClick()
    }
}