package com.example.beakonpoc.models

import com.example.beakonpoc.di.TestBeaconEmitter
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class BeaconEmitterTest {

    @get: Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @TestBeaconEmitter
    lateinit var beaconEmitter: BeaconEmitter

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun test_startIBeacon_withInvalidData() {
        beaconEmitter.startIBeacon("",
            0,
            0)
        val result = beaconEmitter.isEmitting()
        assertThat(result).isFalse()
    }

    @Test
    fun test_startIBeacon_withValidData() {
        beaconEmitter.startIBeacon("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6",
            13,
            15)
        val result = beaconEmitter.isEmitting()
        assertThat(result).isTrue()
    }

    @Test
    fun test_startEddystone_withValidData() {
        beaconEmitter.startEddystone("0102030405060708090a",
            "000000000002")
        val result = beaconEmitter.isEmitting()
        assertThat(result).isTrue()
    }

    @Test
    fun test_startEddystone_withInvalidData() {
        beaconEmitter.startEddystone("",
            "")
        val result = beaconEmitter.isEmitting()
        assertThat(result).isFalse()
    }


    @Test
    fun stopEmitting() {
        beaconEmitter.startEddystone("0102030405060708090a",
            "000000000002")
        beaconEmitter.stopEmitting("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6")
        val result = beaconEmitter.isEmitting()
        assertThat(result).isFalse()
    }
}