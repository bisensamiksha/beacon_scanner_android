package com.example.beakonpoc.views

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.beakonpoc.R
import com.example.beakonpoc.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class ScannerFragmentTest{

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var fragment: ScannerFragment

    @Before
    fun setUp() {
        hiltRule.inject()
        fragment = ScannerFragment()
        launchFragmentInHiltContainer<ScannerFragment>()
    }

    @Test
    fun test_isRecyclerViewVisible() {
        onView(withId(R.id.beaconRecyclerView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_onClickStartButton(){
        onView(withId(R.id.startScan)).check(matches(isDisplayed()))
        onView(withId(R.id.startScan)).check(matches(isEnabled()))
        onView(withId(R.id.startScan)).perform(click())
        onView(withId(R.id.startScan)).check(matches(isNotEnabled()))
        onView(withId(R.id.stopScan)).check(matches(isEnabled()))
    }

    @Test
    fun test_onClickStopButton(){
        onView(withId(R.id.stopScan)).check(matches(isDisplayed()))
        onView(withId(R.id.stopScan)).perform(click())
        onView(withId(R.id.stopScan)).check(matches(isNotEnabled()))
        onView(withId(R.id.startScan)).check(matches(isEnabled()))
    }

}