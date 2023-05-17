package com.example.beakonpoc.models

import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.beakonpoc.R
import com.example.beakonpoc.utils.Constants
import com.example.beakonpoc.utils.Utils
import java.util.*
import javax.inject.Inject

class BLEManager @Inject constructor(
    private val context: Context
) : BluetoothAdapterWrapper {

    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private var bluetoothScanner = bluetoothAdapter.bluetoothLeScanner
    private var iBeaconData = MutableLiveData<MutableList<BeaconDataModel>?>()

    private var isScanningStarted = false

    private var callback: ScanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            Log.d("BLE Logs", "Success ${result.toString()}")
            result?.let { scanResult ->
                processPayload(scanResult)
                isScanningStarted = true
            }

        }

        override fun onScanFailed(errorCode: Int) {
            Toast.makeText(context, context.getString(R.string.scanning_failed), Toast.LENGTH_LONG).show()
            Log.d("BLE Logs", "Failed $errorCode")
            isScanningStarted = false
            super.onScanFailed(errorCode)

        }
    }

    override fun isEnable(): Boolean {
        return bluetoothAdapter.isEnabled
    }

    init {
        iBeaconData.postValue(ArrayList())
    }

    override fun startScan() {
        if (bluetoothScanner == null) {
            bluetoothScanner = bluetoothManager.adapter.bluetoothLeScanner
            bluetoothScanner.startScan(callback)
        } else bluetoothScanner.startScan(callback)
        isScanningStarted = true
    }


    override fun stopScan() {
        if (isEnable()) {
            bluetoothScanner?.stopScan(callback)
        }
        isScanningStarted = false
    }

    override fun updateBeacon(): MutableLiveData<MutableList<BeaconDataModel>?> {
        return iBeaconData
    }

    fun processPayload(scanResult: ScanResult) {
        //For iBeacon
        val payload = scanResult.scanRecord?.manufacturerSpecificData?.get(Constants.IBEACON_MANUFACTURE_ID)
        if (payload != null && payload.size >= 22) {

            val uuidBytes = payload.slice(2..17).toByteArray()
            val uuidString = uuidBytes.joinToString(separator = "") { byte ->
                String.format("%02X", byte)
            }
            val major =
                ((payload[18].toInt() and 0xff) shl 8) or (payload[19].toInt() and 0xff)
            val minor =
                ((payload[20].toInt() and 0xff) shl 8) or (payload[21].toInt() and 0xff)
            val rssi = scanResult.rssi

            Log.d(
                "BLE Logs",
                "UUID: $uuidString, Major: $major, Minor: $minor"
            )

            var hasBeacon = false
            val currentList = iBeaconData.value
            val newBeacon = BeaconDataModel(
                BeaconType.IBEACON,
                uuidString,
                major.toString(),
                minor.toString(),
                rssi.toString()
            )

            for (i in currentList?.indices!!) {
                if (currentList[i].uuid == newBeacon.uuid) {
                    currentList[i] = newBeacon
                    hasBeacon = true
                    break
                }
            }

            if (!hasBeacon) {
                currentList.add(newBeacon)
            }
            iBeaconData.postValue(currentList)

        }

        //for Eddystone
        val serviceUuidEddystone = scanResult.scanRecord?.serviceUuids
        val eddystoneServiceID = ParcelUuid.fromString(Constants.EDDYSTONE_SERVICE_UUID)

        if (serviceUuidEddystone != null && serviceUuidEddystone.contains(eddystoneServiceID)) {
            val eddystoneData = scanResult.scanRecord?.getServiceData(eddystoneServiceID)
            if (eddystoneData != null) {
                val eddystoneUUID =
                    Utils.bytesToHex(Arrays.copyOfRange(eddystoneData, 2, 18))
                val namespace = String(eddystoneUUID.toCharArray().sliceArray(0..19))
                val instance = String(
                    eddystoneUUID.toCharArray()
                        .sliceArray(20 until eddystoneUUID.toCharArray().size)
                )
                val rssi = scanResult.rssi

                Log.d(
                    "BLE Logs",
                    "Eddystone found eddyStoneUUID:$eddystoneUUID, Namespace: $namespace, Instance: $instance"
                )
                var hasBeacon = false
                val currentList = iBeaconData.value
                val newBeacon = BeaconDataModel(
                    BeaconType.EDDYSTONE,
                    eddystoneUUID,
                    null,
                    null,
                    rssi.toString(),
                    namespace,
                    instance
                )

                for (i in currentList?.indices!!) {
                    if (currentList[i].uuid == newBeacon.uuid) {
                        currentList[i] = newBeacon
                        hasBeacon = true
                        break
                    }
                }

                if (!hasBeacon) {
                    currentList.add(newBeacon)
                }
                iBeaconData.postValue(currentList)
            }
        }
    }

    override fun isScanning(): Boolean {
        return isScanningStarted
    }

}