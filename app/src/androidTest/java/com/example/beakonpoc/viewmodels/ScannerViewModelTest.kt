package com.example.beakonpoc.viewmodels

import com.example.beakonpoc.di.TestBLEManager
import com.example.beakonpoc.models.BLEManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class ScannerViewModelTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var viewModel: ScannerViewModel

    @Inject
    @TestBLEManager
    lateinit var bleManager: BLEManager

    @Before
    fun setup() {
        hiltRule.inject()
        viewModel = ScannerViewModel(bleManager)
    }

    // To test if the scanning is started on call of startScan()
    @Test
    fun test_startScan() {
        assertFalse(bleManager.isScanning())
        viewModel.startScan()
        assertTrue(bleManager.isScanning())
    }

    // To test if the scanning is stopped on call of stopScan()
    @Test
    fun test_stopScan() {
        viewModel.startScan()
        assertTrue(bleManager.isScanning())

        viewModel.stopScan()
        assertFalse(bleManager.isScanning())
    }
}