package com.example.beakonpoc.views

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.beakonpoc.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.*
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MainActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var scenario: ActivityScenario<MainActivity>

    //TODO: check if following tests can be run without connecting real device

    @Before
    fun setUp() {
        hiltRule.inject()
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    //To test  checkPermissions() method when permission is not granted.
    // To simulate the scenario for this test, remove all permissions from the app
    @Test
    fun test_checkPermission_withNoPermissionsNotGranted() {
        scenario.onActivity {activity ->
            val result = activity.checkPermissions()
            assertFalse(result)
        }
    }

    //To test  checkPermissions() method when some permissions are not granted.
    // To simulate the scenario for this test, remove one permissions from the app
    @Test
    fun test_checkPermission_withSomePermissionsNotGranted() {
        scenario.onActivity {activity ->
            val result = activity.checkPermissions()
            assertFalse(result)
        }
    }

    //To test if showRationale() method displays rationale when permission is not granted.
    // To simulate the scenario for this test, remove permissions from the app
    @Test
    fun test_showRationale() {
        scenario.onActivity{activity ->
            assertFalse(activity.checkPermissions())
        }
        onView(withText("Please grant permissions to function properly.")).check(matches(isDisplayed()))
        onView(withText("CANCEL")).perform(click())
        onView(withText("Please grant permissions to function properly.")).check(doesNotExist())
    }

    //To test  checkPermissions() method when all the permissions are granted
    // To simulate the scenario for this test, grant all the required permissions to the app
    @Test
    fun test_checkPermission_withAllPermissionsGranted() {
        scenario.onActivity{activity ->
            val result = activity.checkPermissions()
            assertTrue(result)
        }
    }

    //To test if correct view is displayed when all the permissions are granted
    // To simulate the scenario for this test, grant all the required permissions to the app
    @Test
    fun test_viewDisplayed_whenPermissionsGranted(){
        onView(withId(R.id.fragmentContainerView)).check(matches(isDisplayed()))
        onView(withId(R.id.errorText)).check(matches(not(isDisplayed())))
    }

    @After
    fun tearDown() {
        scenario.close()
    }

}