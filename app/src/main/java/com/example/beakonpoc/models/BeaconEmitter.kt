package com.example.beakonpoc.models

import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import com.example.beakonpoc.utils.Constants
import com.example.beakonpoc.utils.Utils
import javax.inject.Inject

class BeaconEmitter @Inject constructor(
    private val context: Context
) {

    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private var bluetoothLeAdvertiser: BluetoothLeAdvertiser? =
        bluetoothAdapter?.bluetoothLeAdvertiser

    private val advertisingCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            super.onStartSuccess(settingsInEffect)
            Log.d("BLE Logs", "Advertising started successfully ${settingsInEffect.toString()}")
        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            Log.d("BLE Logs", "Advertising failed to start $errorCode")
        }
    }

    fun startIBeacon(uuid: String, major: Int, minor: Int) {
        val uuidHex = uuid.replace("-", "")
        val majorHex = String.format("%04X", major)
        val minorHex = String.format("%04X", minor)

        val iBeaconDataString = "$uuidHex$majorHex$minorHex"
        startAdvertising(iBeaconDataString, BeaconType.IBEACON)
    }

    fun startEddystone(namespace: String, instance: String?) {
        val eddystoneData = "$namespace$instance"
        startAdvertising(eddystoneData, BeaconType.EDDYSTONE)
    }

    fun stopAdvertising() {
        bluetoothLeAdvertiser?.stopAdvertising(advertisingCallback)
    }

    private fun startAdvertising(data: String, type: BeaconType) {
        if (bluetoothLeAdvertiser == null) {
            bluetoothLeAdvertiser = bluetoothManager.adapter.bluetoothLeAdvertiser
        }
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setConnectable(false)
            .setTimeout(0)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .build()

        when (type) {
            BeaconType.IBEACON -> {
                val advertiseData = AdvertiseData.Builder().addManufacturerData(
                    Constants.IBEACON_MANUFACTURE_ID,
                    byteArrayOf(2, 21) + Utils.hexStringToByteArray(data)
                ).build()
                bluetoothLeAdvertiser?.startAdvertising(
                    settings,
                    advertiseData,
                    advertisingCallback
                )

            }
            BeaconType.EDDYSTONE -> {
                val parcelUuid = ParcelUuid.fromString(Constants.EDDYSTONE_SERVICE_UUID)
                val advertiseData = AdvertiseData.Builder().addServiceUuid(parcelUuid)
                    .addServiceData(
                        parcelUuid,
                        byteArrayOf(0, -18) + Utils.hexStringToByteArray(data)
                    ).build()
                bluetoothLeAdvertiser?.startAdvertising(
                    settings,
                    advertiseData,
                    advertisingCallback
                )
            }
        }
    }
}
