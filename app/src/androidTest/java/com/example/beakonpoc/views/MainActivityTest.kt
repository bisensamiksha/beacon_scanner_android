package com.example.beakonpoc.views

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.beakonpoc.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.*
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MainActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    private lateinit var activity: MainActivity

    //Following tests require real device
    @Before
    fun setUp() {
        activity = activityRule.activity
    }

    @Test
    fun test_checkPermission_withPermissionsNotGranted() {
        val result = activity.checkPermissions()
        assertFalse(result)
    }

    @Test
    fun test_checkPermission_withPermissionsGranted() {
        val result = activity.checkPermissions()
        assertTrue(result)
        onView(withId(R.id.fragmentContainerView)).check(matches(isDisplayed()))
        onView(withId(R.id.errorText)).check(matches(not(isDisplayed())))
    }

    @Test
    fun test_showRationale() {
        assertFalse(activity.checkPermissions())
        onView(withText("Please grant permissions to function properly.")).check(matches(isDisplayed()))
        onView(withText("CANCEL")).perform(click())
        onView(withText("Please grant permissions to function properly.")).check(doesNotExist())
    }

}