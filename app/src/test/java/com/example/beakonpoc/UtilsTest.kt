package com.example.beakonpoc


import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.beakonpoc.utils.Utils
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UtilsTest {

    //To test hexStringToByteArray() function with valid hex string input
    @Test
    fun test_hexStringToByteArray_withValidHexString() {
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
        val result = Utils.hexStringToByteArray(hexString)
        assertThat(result).isEqualTo(expected)
    }

    //To test hexStringToByteArray() function with empty string input
    @Test
    fun test_hexStringToByteArray_withEmptyString() {
        val expected = byteArrayOf()
        val result = Utils.hexStringToByteArray("")
        assertThat(result).isEqualTo(expected)
    }


    //To test bytesToHex() function with valid hex string input
    @Test
    fun test_bytesToHex_withValidArray() {
        val bytes = byteArrayOf(0x12, 0x34, 0xAB.toByte(), 0xCD.toByte())
        val expectedHex = "1234ABCD"
        val result = Utils.bytesToHex(bytes)
        assertThat(result).isEqualTo(expectedHex)
    }

    //To test bytesToHex() function with valid hex string input
    @Test
    fun test_bytesToHex_withEmptyArray() {
        val bytes = byteArrayOf()
        val expectedHex = ""
        val result = Utils.bytesToHex(bytes)
        assertThat(result).isEqualTo(expectedHex)
    }
}