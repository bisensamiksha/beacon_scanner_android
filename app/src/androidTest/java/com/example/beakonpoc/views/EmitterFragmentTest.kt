package com.example.beakonpoc.views

import android.app.Activity
import android.app.Instrumentation
import android.bluetooth.BluetoothAdapter
import android.view.View
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
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
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
        launchFragmentInHiltContainer<EmitterFragment>(){
            fragment = this as EmitterFragment
        }
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

    //Following tests require real device
    @Test
    fun test_checkBluetoothState_withBluetoothEnabled() {
        val state = fragment.checkBluetoothState()
        assertTrue(state)
    }

    @Test
    fun test_checkBluetoothState_withBluetoothDisabled() {
        val state = fragment.checkBluetoothState()
        assertFalse(state)
    }

    @Test
    fun test_requestBluetoothEnable() {
        assertFalse(fragment.checkBluetoothState())

        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, null)
        val intentMatcher = IntentMatchers.hasAction(BluetoothAdapter.ACTION_REQUEST_ENABLE)

        Intents.init()

        val resultLauncher = fragment.requireActivity().activityResultRegistry.register("key",
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result })

        fragment.bluetoothActivityResultLauncher = resultLauncher

        fragment.requestBluetoothEnable()
        Intents.intended(intentMatcher)
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

