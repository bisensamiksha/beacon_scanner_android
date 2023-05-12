package com.example.beakonpoc.utils

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

    fun hexToByte(hexString: String): ByteArray {
        return hexString.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }

     fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (i in bytes.indices) {
            val v = bytes[i].toInt() and 0xFF
            hexChars[i * 2] = hexArray[v ushr 4]
            hexChars[i * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }
    private val hexArray = "0123456789ABCDEF".toCharArray()
}