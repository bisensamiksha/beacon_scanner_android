package com.example.beakonpoc.views

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.beakonpoc.R
import com.example.beakonpoc.launchFragmentInHiltContainer
import com.example.beakonpoc.models.BeaconDataModel
import com.example.beakonpoc.models.BeaconType
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class EmitterFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var fragment: EmitterFragment


    @Before
    fun setup() {
        hiltRule.inject()
        fragment = EmitterFragment()
        launchFragmentInHiltContainer<EmitterFragment>()
    }


    @Test
    fun test_isRecyclerViewVisible() {
        onView(withId(R.id.emitterRecyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun test_onIbeaconClick(){
        val beacon = BeaconDataModel(BeaconType.IBEACON, "2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6", "13", "15")
        fragment.beaconList = ArrayList()
        fragment.beaconList.add(beacon)
        onView(withId(R.id.emitterRecyclerView)).perform(actionOnItemAtPosition<BeaconEmitterListAdapter.BeaconViewHolder>(0,
            clickOnChildView(R.id.emitSwitch)))
    }

    @Test
    fun test_onEddystoneClick(){
        val beacon = BeaconDataModel(
            BeaconType.EDDYSTONE,
            "0102030405060708090a000000000002",
            null,
            null,
            null,
            "0102030405060708090a",
            "000000000002"
        )
        fragment.beaconList = ArrayList()
        fragment.beaconList.add(beacon)
        onView(withId(R.id.emitterRecyclerView)).perform(actionOnItemAtPosition<BeaconEmitterListAdapter.BeaconViewHolder>(0,
            clickOnChildView(R.id.emitSwitch)))
    }

    private fun clickOnChildView(viewId: Int): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "Click on a child view with specified ID"
            }

            override fun getConstraints(): Matcher<View> {
                return allOf(isDisplayed(), isAssignableFrom(View::class.java))
            }

            override fun perform(uiController: UiController, view: View) {
                val childView = view.findViewById<View>(viewId)
                childView.performClick()
            }
        }
    }
}

