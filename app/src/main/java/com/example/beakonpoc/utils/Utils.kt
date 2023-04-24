package com.example.beakonpoc.utils

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import com.example.beakonpoc.views.MainActivity

object Utils {

    fun isBLESupported(activity: MainActivity): Boolean {
        val bluetoothManager =
            activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        return bluetoothAdapter.isEnabled && activity.packageManager.hasSystemFeature(
            PackageManager.FEATURE_BLUETOOTH_LE
        )
    }

    fun calculateProximity(rssi: Int, txPower: Int): String {
        val ratio = rssi.toDouble() / txPower.toDouble()
        if (ratio < 1.0) {
            return "Immediate"
        }
        val accuracy = 20 * Math.log10(ratio) + 20
        return when {
            accuracy < 0.5 -> "Immediate"
            accuracy < 0 -> "Near"
            else -> "Far"
        }
    }

    fun hexToByte(hexString: String): ByteArray {

        return hexString.chunked(2).map { it.toInt(16).toByte() }.toByteArray()

    }
}