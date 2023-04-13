package com.example.beakonpoc.models

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import java.nio.ByteBuffer
import java.nio.ByteOrder

class BLEManager(
    private val context: Context
) {

    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val bluetoothScanner = bluetoothAdapter.bluetoothLeScanner
    private var iBeaconData = MutableLiveData<BeaconDataModel?>()

    private var callback: ScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            Log.d("BLE Logs", "Success ${result.toString()}")
            result?.let { scanResult ->
                processPayload(scanResult)
            }

        }

        override fun onScanFailed(errorCode: Int) {
            Toast.makeText(context, "Scanning Failed", Toast.LENGTH_LONG).show()
            Log.d("BLE Logs", "Failed $errorCode")
            super.onScanFailed(errorCode)

        }
    }

    @SuppressLint("MissingPermission")
    fun startScan(){
        bluetoothScanner?.startScan(callback)
    }

    @SuppressLint("MissingPermission")
    fun stopScan(){
        bluetoothScanner?.stopScan(callback)
    }

    fun updateBeacon(): MutableLiveData<BeaconDataModel?> {
        return iBeaconData
    }

    fun processPayload(scanResult: ScanResult) {
        val payload = scanResult.scanRecord?.manufacturerSpecificData?.get(76)
        if (payload != null && payload.size >= 23) {
            val uuidBytes =
                ByteBuffer.wrap(payload, 2, 18).order(ByteOrder.LITTLE_ENDIAN).long
            val major =
                ((payload[18].toInt() and 0xff) shl 8) or (payload[19].toInt() and 0xff)
            val minor =
                ((payload[20].toInt() and 0xff) shl 8) or (payload[21].toInt() and 0xff)
            val txPowerBeacon = payload[22].toInt()
            val rssi = scanResult.rssi

            Log.d(
                "BLE Logs",
                "UUID: $uuidBytes, Major: $major, Minor: $minor, TxPower: $txPowerBeacon"
            )

            iBeaconData.postValue(BeaconDataModel(
                uuidBytes.toString(),
                major.toString(),
                minor.toString(),
                rssi.toString()
            ))

            //for Eddystone
            //val manufactuerIdGoogle = scanResult.scanRecord?.manufacturerSpecificData.get(0x0118)

        }
    }
}