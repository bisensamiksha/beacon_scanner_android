package com.example.beakonpoc.viewmodels

import android.app.Application
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
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
class MainActivityViewModelTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainActivityViewModel

    @Inject
    @TestBLEManager
    lateinit var bleManager: BLEManager

    @Before
    fun setup() {
        hiltRule.inject()
        val context = ApplicationProvider.getApplicationContext<Context>()
        viewModel = MainActivityViewModel(bleManager,context as Application)
    }

    @Test
    fun test_startScan() {
        assertFalse(viewModel.bleManager.isScanning())
        viewModel.startScan()

        assertTrue(viewModel.bleManager.isScanning())
        viewModel.stopScan()
    }

    @Test
    fun test_stopScan() {
        viewModel.startScan()
        assertTrue(viewModel.bleManager.isScanning())

        viewModel.stopScan()
        assertFalse(viewModel.bleManager.isScanning())
    }
}
