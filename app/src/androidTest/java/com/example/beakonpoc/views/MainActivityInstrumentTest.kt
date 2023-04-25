package com.example.beakonpoc.views

import android.app.Activity
import android.app.Instrumentation
import android.bluetooth.BluetoothAdapter
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.beakonpoc.R
import junit.framework.TestCase.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityInstrumentTest {


    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)


    private lateinit var activity: MainActivity
    private lateinit var startScanButton: Button
    private lateinit var errorTextView: TextView
    private lateinit var stopScanButton: Button


    //Following tests require real device
    @Before
    fun setUp() {
        activity = activityRule.activity

        startScanButton = activity.findViewById(R.id.startScan)
        errorTextView = activity.findViewById(R.id.errorText)
        stopScanButton = activity.findViewById(R.id.stopScan)

    }

    @Test
    fun testInitialConditions() {
        assertFalse(startScanButton.isEnabled)
        assertFalse(stopScanButton.isEnabled)
        assertEquals(View.GONE, errorTextView.visibility)
    }

    @Test
    fun checkPermission_permissionNotGranted_shouldReturnFalse() {
        val result = activity.checkPermissions()
        assertFalse(result)
    }


    @Test
    fun checkPermission_permissionGranted_startScanEnabled() {

        val result = activity.checkPermissions()

        assertTrue(result)
        assertTrue(startScanButton.isEnabled)
        assertEquals(View.GONE, errorTextView.visibility)
    }

    @Test
    fun checkBluetoothState_bluetoothEnabled_startScanningEnable() {
        val state = activity.checkBluetoothState()
        assertTrue(state)
        assertTrue(startScanButton.isEnabled)
        assertEquals(View.GONE, errorTextView.visibility)
    }

    @Test
    fun checkBluetoothState_bluetoothDisabled_shouldReturnFalse() {
        val state = activity.checkBluetoothState()
        assertFalse(state)
    }

    @Test
    fun startScanning_onClick_stopScanBtnEnable() {

        assertTrue(activity.checkBluetoothState())
        assertTrue(activity.checkPermissions())

        activity.runOnUiThread { startScanButton.performClick() }

        assertFalse(stopScanButton.isEnabled)
        //assertTrue(activity.isScanning) //needs to implement this later

    }


    @Test
    fun bluetoothIsDisabled_startsEnableDialogIntent() {
        assertFalse(activity.checkBluetoothState())

        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, null)
        val intentMatcher = hasAction(BluetoothAdapter.ACTION_REQUEST_ENABLE)

        Intents.init()

        val resultLauncher = activity.activityResultRegistry.register("key",
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result })

        activity.bluetoothActivityResultLauncher = resultLauncher


        activity.requestBluetoothEnable()
        intended(intentMatcher)

    }

    @Test
    fun onRequestNotGranted_shouldShowRationale() {

        assertFalse(activity.checkPermissions())

        onView(withText("Please grant permissions to function properly.")).check(matches(isDisplayed()))

        onView(withText("Cancel")).perform(click())

        onView(withText("Please grant permissions to function properly.")).check(doesNotExist())

    }

}