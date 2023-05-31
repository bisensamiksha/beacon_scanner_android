package com.example.beakonpoc.utils

import android.bluetooth.BluetoothManager
import android.content.Context
import android.util.Log

object Utils {

    fun isBluetoothEnabled(context: Context?): Boolean {
        val bluetoothManager =
            context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        return bluetoothAdapter.isEnabled
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

    fun hexStringToByteArray(data: String): ByteArray {
        val length = data.length / 2
        val byteArray = ByteArray(length)
        for (i in 0 until length) {
            val index = i * 2
            val hex = data.substring(index, index + 2)
            val byte = hex.toInt(16).toByte()
            byteArray[i] = byte
        }
        Log.d("BLE Logs", "byteArray is  $byteArray")
        return byteArray
    }
}