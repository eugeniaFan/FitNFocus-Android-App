package com.example.fitnfocus

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextClearance
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.fitnfocus.data.datastore.userPreferencesDataStore
import com.example.fitnfocus.data.repository.UserPreferencesRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GoalsAddLearningGoalTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() = runBlocking(kotlinx.coroutines.Dispatchers.IO) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = UserPreferencesRepository(context.userPreferencesDataStore)
        prefs.setOnboardingCompleted()
    }

    @Test
    fun startWorkflowLearningGoal() {
        // Part 1 : Add Learning goal
        composeTestRule.onNodeWithTag("nav_goals").performClick()
        composeTestRule.onNodeWithTag("screen_goals_overview").assertIsDisplayed()

        composeTestRule.onNodeWithTag("goals_add_goal_button").performClick()
        composeTestRule.onNodeWithTag("goals_add_goal_sheet").assertIsDisplayed()

        composeTestRule.onNodeWithTag("goal_input_module").performTextInput("MAD1")
        composeTestRule.onNodeWithTag("goal_input_exam_date").performTextInput("01.02.2026")
        composeTestRule.onNodeWithTag("goal_input_topic").performTextInput("Präsentation üben")
        composeTestRule.onNodeWithTag("goal_add_topic_button").performClick()

        composeTestRule.onNodeWithTag("goal_save_button").performScrollTo().performClick()
        composeTestRule.onNodeWithText("MAD1").assertIsDisplayed()

        // Part 2: Edit topic in learning goal
        composeTestRule.onNodeWithTag("goal_item_MAD1").performClick()

        composeTestRule.onNodeWithTag("goal_edit_button").performClick()
        composeTestRule.onNodeWithTag("goals_edit_goal_sheet").assertIsDisplayed()

        composeTestRule.onNodeWithTag("edit_goal_input_exam_date").performTextClearance()
        composeTestRule.onNodeWithTag("edit_goal_input_exam_date").performTextInput("05.01.2026")
        composeTestRule.onNodeWithTag("edit_goal_input_topic")
            .performTextInput("Projekt vorstellen")
        composeTestRule.onNodeWithTag("edit_goal_add_topic_button").performClick()

        composeTestRule.onNodeWithTag("edit_goal_save_button").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Projekt vorstellen").assertIsDisplayed()

        // Part 3: create session
        composeTestRule.onNodeWithTag("topic_item_Projekt_vorstellen").performScrollTo()
            .performClick()
        composeTestRule.onNodeWithTag("screen_topic_sessions").assertIsDisplayed()

        composeTestRule.onNodeWithTag("add_session_button").performClick()

        composeTestRule.onNodeWithTag("add_session_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("add_session_dialog_topic")
            .assertTextContains("Projekt vorstellen")

        composeTestRule.onNodeWithTag("add_session_duration").performTextInput("a")
        composeTestRule.onNodeWithTag("add_session_save").assertIsNotEnabled()

        composeTestRule.onNodeWithTag("add_session_duration").performTextClearance()

        composeTestRule.onNodeWithTag("add_session_duration").performTextInput("1")

        composeTestRule.onNodeWithTag("add_session_save").assertIsEnabled()
        composeTestRule.onNodeWithTag("add_session_save").performClick()
        composeTestRule.onNodeWithTag("add_session_dialog").assertDoesNotExist()

        composeTestRule.onNodeWithText("1 min").assertIsDisplayed()


        // Part 4: start session
        composeTestRule.onNodeWithTag("nav_home").performClick()
        composeTestRule.onNodeWithTag("home_sessions_today_title").assertIsDisplayed()
        composeTestRule.onNodeWithTag("home_session_card_Projekt_vorstellen").assertIsDisplayed()

        composeTestRule.onNodeWithTag("home_start_session_Projekt_vorstellen").performClick()
        composeTestRule.onNodeWithTag("screen_session_timer").assertIsDisplayed()

        composeTestRule.onNodeWithTag("timer_play_button").performClick()
        composeTestRule.waitForIdle()
        Thread.sleep(2000)
        composeTestRule.onNodeWithTag("screen_session_timer").assertIsDisplayed()

        composeTestRule.waitUntil(timeoutMillis = 70_000) {
            composeTestRule
                .onAllNodesWithTag("timer_finished_text")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithTag("timer_finished_text").assertIsDisplayed()

        composeTestRule.onNodeWithTag("timer_complete_session_button").assertIsDisplayed()
    }
}