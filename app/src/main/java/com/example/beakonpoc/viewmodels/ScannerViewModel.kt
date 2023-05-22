package com.example.beakonpoc.viewmodels

import androidx.lifecycle.ViewModel
import com.example.beakonpoc.models.BluetoothAdapterWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val bleManager: BluetoothAdapterWrapper
) : ViewModel() {
    val beaconLiveData = bleManager.updateBeacon()

    fun startScan() {
        bleManager.stopScan()
        bleManager.startScan()
    }

    fun stopScan() {
        bleManager.stopScan()
    }

    override fun onCleared() {
        super.onCleared()
        bleManager.stopScan()
    }
}
