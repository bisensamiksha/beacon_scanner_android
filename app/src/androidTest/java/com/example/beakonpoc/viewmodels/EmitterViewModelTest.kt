package com.example.beakonpoc.viewmodels

import com.example.beakonpoc.di.TestBeaconEmitter
import com.example.beakonpoc.models.BeaconDataModel
import com.example.beakonpoc.models.BeaconEmitter
import com.example.beakonpoc.models.BeaconType
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class EmitterViewModelTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var viewModel: EmitterViewModel

    @Inject
    @TestBeaconEmitter
    lateinit var beaconEmitter: BeaconEmitter

    @Before
    fun setUp(){
        hiltRule.inject()
        viewModel = EmitterViewModel(beaconEmitter)
    }

    @Test
    fun test_startIBeacon() {
        assertFalse(beaconEmitter.isEmitting())
        viewModel.startIBeacon(BeaconDataModel(BeaconType.IBEACON, "2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6", "1","2"))
        assertTrue(beaconEmitter.isEmitting())
    }

    @Test
    fun test_startEddyStone() {
        assertFalse(beaconEmitter.isEmitting())
        viewModel.startEddyStone(BeaconDataModel(BeaconType.EDDYSTONE, "0102030405060708090a000000000002", null,null,null, "0102030405060708090a", "000000000002"))
        assertTrue(beaconEmitter.isEmitting())
    }

    @Test
    fun test_stopEmitter() {
        viewModel.startEddyStone(BeaconDataModel(BeaconType.EDDYSTONE, "0102030405060708090a000000000002", null,null,null, "0102030405060708090a", "000000000002"))
        assertTrue(beaconEmitter.isEmitting())
        viewModel.stopEmitter("0102030405060708090a000000000002")
        assertFalse(beaconEmitter.isEmitting())
    }
}