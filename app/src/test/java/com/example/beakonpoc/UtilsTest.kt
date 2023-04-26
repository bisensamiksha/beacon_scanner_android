package com.example.beakonpoc


import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.beakonpoc.utils.Utils
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UtilsTest {
    @Test
    fun test_hexToByte_withValidHexString() {
        val hexString = "0123456789abcdef"
        val expected = byteArrayOf(
            0x01,
            0x23,
            0x45,
            0x67,
            0x89.toByte(),
            0xab.toByte(),
            0xcd.toByte(),
            0xef.toByte()
        )
        val result = Utils.hexToByte(hexString)
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun test_hexToByte_withInvalidHexString() {
        val hexString = "0123456789abcde"
        val expected = byteArrayOf(
            0x01,
            0x23,
            0x45,
            0x67,
            0x89.toByte(),
            0xab.toByte(),
            0xcd.toByte(),
            0xef.toByte()
        )
        val result = Utils.hexToByte(hexString)
        assertThat(result).isNotEqualTo(expected)
    }
}