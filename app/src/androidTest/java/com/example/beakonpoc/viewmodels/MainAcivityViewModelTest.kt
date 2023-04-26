package com.example.beakonpoc.viewmodels

import android.app.Application
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainActivityViewModel

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        viewModel = MainActivityViewModel(context as Application)
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
