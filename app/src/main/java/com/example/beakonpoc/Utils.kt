package com.example.beakonpoc

import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager

object Utils {

    fun isBLESupported(adapter: BluetoothAdapter, activity: MainActivity): Boolean{
        return adapter.isEnabled && activity.packageManager.hasSystemFeature(
            PackageManager.FEATURE_BLUETOOTH_LE)
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
}