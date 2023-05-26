package com.example.beakonpoc.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ScannerViewModel

    @Inject
    @TestBLEManager
    lateinit var bleManager: BLEManager

    @Before
    fun setup() {
        hiltRule.inject()
        viewModel = ScannerViewModel(bleManager)
    }

    @Test
    fun test_startScan() {
        assertFalse(bleManager.isScanning())
        viewModel.startScan()

        assertTrue(bleManager.isScanning())
        viewModel.stopScan()
    }

    @Test
    fun test_stopScan() {
        viewModel.startScan()
        assertTrue(bleManager.isScanning())

        viewModel.stopScan()
        assertFalse(bleManager.isScanning())
    }
}