package com.example.beakonpoc.views

import android.bluetooth.BluetoothAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
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
        launchFragmentInHiltContainer<ScannerFragment>{
            fragment = this as ScannerFragment
        }
    }

    //To test if the views are displayed properly
    @Test
    fun test_isViewDisplayed(){
        onView(withId(R.id.beaconRecyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.stopScan)).check(matches(isDisplayed()))
        onView(withId(R.id.startScan)).check(matches(isDisplayed()))
    }

    //To test view changes on click of startScan button
    @Test
    fun test_onClickStartButton(){
        onView(withId(R.id.startScan)).check(matches(isDisplayed()))
        onView(withId(R.id.startScan)).perform(click())
        onView(withId(R.id.startScan)).check(matches(isNotEnabled()))
        onView(withId(R.id.stopScan)).check(matches(isEnabled()))
    }

    //To test view changes on click of stopScan button
    @Test
    fun test_onClickStopButton(){
        onView(withId(R.id.stopScan)).check(matches(isDisplayed()))
        onView(withId(R.id.stopScan)).perform(click())
        onView(withId(R.id.stopScan)).check(matches(isNotEnabled()))
        onView(withId(R.id.startScan)).check(matches(isEnabled()))
    }

    //Following tests require real device

    // To test checkBluetoothState() method when bluetooth is ON
    @Test
    fun test_checkBluetoothState_withBluetoothEnabled() {
        val state = fragment.checkBluetoothState()
        assertTrue(state)
    }

    // To test checkBluetoothState() method when bluetooth is OFF
    @Test
    fun test_checkBluetoothState_withBluetoothDisabled() {
        val state = fragment.checkBluetoothState()
         assertFalse(state)
    }

    // To if requestBluetoothEnable() method requests user to switch ON bluetooth
    // This test requires Bluetooth to be kept OFF
    @Test
    fun test_requestBluetoothEnable() {
        assertFalse(fragment.checkBluetoothState())

        val intentMatcher = IntentMatchers.hasAction(BluetoothAdapter.ACTION_REQUEST_ENABLE)

        Intents.init()

        val resultLauncher = fragment.requireActivity().activityResultRegistry.register("key",
            ActivityResultContracts.StartActivityForResult()
        ) { }

        fragment.bluetoothActivityResultLauncher = resultLauncher

        fragment.requestBluetoothEnable()
        Intents.intended(intentMatcher)
    }

}