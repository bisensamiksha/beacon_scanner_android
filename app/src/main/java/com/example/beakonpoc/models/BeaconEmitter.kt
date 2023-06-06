package com.example.beakonpoc.models

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.content.pm.PackageManager
import android.os.ParcelUuid
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.beakonpoc.utils.Constants
import com.example.beakonpoc.utils.Utils
import javax.inject.Inject

class BeaconEmitter @Inject constructor(
    private val context: Context
) {

    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    private var callbackMap: MutableMap<String, AdvertiseCallback> = mutableMapOf()
    private var advertiserMap: MutableMap<String, BluetoothLeAdvertiser> = mutableMapOf()

    private var isEmitting = false

    fun startIBeacon(uuid: String, major: Int, minor: Int) {
        if (uuid.isEmpty()) return //TODO: show error to the user
        val uuidHex = uuid.replace("-", "")
        val majorHex = String.format("%04X", major)
        val minorHex = String.format("%04X", minor)

        val iBeaconDataString = "$uuidHex$majorHex$minorHex"
        startAdvertising(iBeaconDataString, BeaconType.IBEACON, uuid)
    }

    fun startEddystone(namespace: String, instance: String?) {
        if (namespace.isEmpty()) return //TODO: show error to the user
        val eddystoneData = "$namespace$instance"
        startAdvertising(eddystoneData, BeaconType.EDDYSTONE, eddystoneData)
    }

    fun stopEmitting(uuid: String) {

        val callback = callbackMap[uuid]
        val advertiser = advertiserMap[uuid]

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_ADVERTISE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        advertiser?.stopAdvertising(callback)
        isEmitting = false

        advertiserMap.remove(uuid)
        callbackMap.remove(uuid)
    }


    private fun startAdvertising(data: String, type: BeaconType, uuid: String) {
        val advertisingCallback = object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                super.onStartSuccess(settingsInEffect)
                Log.d("BLE Logs", "Advertising started successfully ${settingsInEffect.toString()}")
            }

            override fun onStartFailure(errorCode: Int) {
                super.onStartFailure(errorCode)
                Log.d("BLE Logs", "Advertising failed to start $errorCode")
            }
        }


        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setConnectable(false)
            .setTimeout(0)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .build()

        val bluetoothAdapter = bluetoothManager.adapter
        val bluetoothLeAdvertiser: BluetoothLeAdvertiser? =
            bluetoothAdapter?.bluetoothLeAdvertiser

        when (type) {
            BeaconType.IBEACON -> {
                val advertiseData = AdvertiseData.Builder().addManufacturerData(
                    Constants.IBEACON_MANUFACTURE_ID,
                    byteArrayOf(2, 21) + Utils.hexStringToByteArray(data)
                ).build()
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_ADVERTISE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                bluetoothLeAdvertiser?.startAdvertising(
                    settings,
                    advertiseData,
                    advertisingCallback
                )
                isEmitting = true
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
                isEmitting = true
            }
        }
        bluetoothLeAdvertiser?.let { advertiserMap[uuid] = it }
        callbackMap[uuid] = advertisingCallback
    }

    fun isEmitting(): Boolean{
        return isEmitting
    }
}
