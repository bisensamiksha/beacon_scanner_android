package com.example.beakonpoc.models

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanRecord
import android.bluetooth.le.ScanResult
import android.os.ParcelUuid
import android.util.SparseArray
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.beakonpoc.di.TestBLEManager
import com.example.beakonpoc.getOrAwaitValue
import com.example.beakonpoc.utils.Utils
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockkClass
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
class BLEManagerTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    @TestBLEManager
    lateinit var bleManager: BLEManager

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun test_stopScanning() {
        bleManager.stopScan()
        val result = bleManager.isScanning()
        assertThat(result).isFalse()
    }

    @Test
    fun test_startScanning() {
        bleManager.startScan()
        val result = bleManager.isScanning()
        assertThat(result).isTrue()
    }

    @Test
    fun test_processPayload_withInvalidScanResult() {
        val scanResult = ScanResult(null, 0, 0, 0, 0, 0, -50, 0, null, 0)
        bleManager.processPayload(scanResult)

        val result = bleManager.updateBeacon().getOrAwaitValue()
        assertThat(result).isEmpty()
    }

    @Test
    fun test_processPayload_withValidScanResult_forIBeacon() {
        val device = mockkClass(BluetoothDevice::class)
        every { device["getAddress"]() } returns "BE:FF:FA:00:22:33"
        every { device["getName"]() } returns null

        val scanRecord = mockkClass(ScanRecord::class)
        val msByteArray = byteArrayOf(2, 21, 47, 35, 68, 84, -49, 109, 74, 15, -83, -14, -12, -111, 27, -87, -1, -90, 0, 1, 0, 1, -59)
        val mfArray = SparseArray<ByteArray>().also {
            it.append(76, msByteArray)
        }
        every { scanRecord["getManufacturerSpecificData"]() } returns mfArray
        every { scanRecord.getManufacturerSpecificData(76) } returns msByteArray
        every { scanRecord.serviceUuids } returns null
        val scanResult = ScanResult(
            /* device = */ device,
            /* eventType = */ 27,
            /* primaryPhy = */ 1,
            /* secondaryPhy = */ 0,
            /* advertisingSid = */ 255,
            /* txPower = */ -2147483648,
            /* rssi = */ -50,
            /* periodicAdvertisingInterval = */ 0,
            /* scanRecord = */ scanRecord,
            /* timestampNanos = */ 1894004710612013
        )

        bleManager.processPayload(scanResult)

        val result = bleManager.updateBeacon().getOrAwaitValue()
        assertThat(result).isNotNull()
        assertThat(result?.get(0)?.rssi).isEqualTo("-50")
        assertThat(result?.get(0)?.type).isEqualTo(BeaconType.IBEACON)
        assertThat(result?.get(0)?.uuid).isEqualTo("2F234454CF6D4A0FADF2F4911BA9FFA6")
        assertThat(result?.get(0)?.major).isEqualTo("1")
        assertThat(result?.get(0)?.minor).isEqualTo("1")

    }

    @Test
    fun test_processPayload_withValidScanResult_forEddyStone() {
        val device = mockkClass(BluetoothDevice::class)
        every { device["getAddress"]() } returns "BE:FF:FA:00:22:33"
        every { device["getName"]() } returns null

        val eddystoneData  = byteArrayOf(0, -18, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 0, 0, 0, 0, 0, 1, 0, 0)
        val scanRecord = mockkClass(ScanRecord::class)
        every { scanRecord["getManufacturerSpecificData"]() } returns null
        every { scanRecord.getManufacturerSpecificData(76) } returns null

        val eddystoneServiceID = ParcelUuid.fromString("0000FEAA-0000-1000-8000-00805F9B34FB")
        every { scanRecord.serviceUuids } returns listOf(eddystoneServiceID)
        every { scanRecord.getServiceData(eddystoneServiceID) } returns eddystoneData
        val scanResult = ScanResult(
            /* device = */ device,
            /* eventType = */ 27,
            /* primaryPhy = */ 1,
            /* secondaryPhy = */ 0,
            /* advertisingSid = */ 255,
            /* txPower = */ -2147483648,
            /* rssi = */ -53,
            /* periodicAdvertisingInterval = */ 0,
            /* scanRecord = */ scanRecord,
            /* timestampNanos = */ 1894004710612013
        )

        bleManager.processPayload(scanResult)

        val result = bleManager.updateBeacon().getOrAwaitValue()
        assertThat(result).isNotNull()
        assertThat(result?.get(0)?.rssi).isEqualTo("-53")
        assertThat(result?.get(0)?.type).isEqualTo(BeaconType.EDDYSTONE)
        assertThat(result?.get(0)?.uuid).isEqualTo("0102030405060708090A000000000001")
        assertThat(result?.get(0)?.namespace).isEqualTo("0102030405060708090A")
        assertThat(result?.get(0)?.instance).isEqualTo("000000000001")
    }

}