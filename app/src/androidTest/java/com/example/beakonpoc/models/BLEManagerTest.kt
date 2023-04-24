package com.example.beakonpoc.models

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanRecord
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.SparseArray
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.example.beakonpoc.getOrAwaitValue
import com.example.beakonpoc.utils.Utils
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockkClass
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BLEManagerTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var bleManager: BLEManager

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        bleManager = BLEManager(context)
    }

    @Test
    fun stoppedScanning_isScanningFalse() {
        bleManager.stopScan()
        val result = bleManager.isScanning()
        assertThat(result).isFalse()
    }

    @Test
    fun startedScanning_isScanningTrue() {
        bleManager.startScan()
        val result = bleManager.isScanning()
        assertThat(result).isTrue()
    }

    @Test
    fun processPayload_invalidScanResult_shouldReturnNull() {

        val scanResult = ScanResult(null, 0, 0, 0, 0, 0, -50, 0, null, 0)

        bleManager.processPayload(scanResult)

        val result = bleManager.updateBeacon().getOrAwaitValue()

        assertThat(result).isNull()


    }

    @Test
    fun processPayload_validScanResult_updateLiveData() {
        val device = mockkClass(BluetoothDevice::class)
        every { device["getAddress"]() } returns "BE:FF:FA:00:22:33"
        every { device["getName"]() } returns null

        val scanRecord = mockkClass(ScanRecord::class)

        val byteArrayUUID = Utils.hexToByte("00112233445566778899AABBCCDDEEFF")
        val majorByteArray = Utils.hexToByte("1122")
        val minorByteArray = Utils.hexToByte("AABB")

        val msByteArray = byteArrayOf(
            0x02, 0x15, *byteArrayUUID, *majorByteArray, *minorByteArray,
            0xC5.toByte(), 0x00
        )

        val mfArray = SparseArray<ByteArray>().also {
            it.append(76, msByteArray)
        }
        every { scanRecord["getManufacturerSpecificData"]() } returns mfArray
        every { scanRecord.getManufacturerSpecificData(76) } returns msByteArray

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
        assertThat(result?.rssi).isEqualTo("-50")

    }

}