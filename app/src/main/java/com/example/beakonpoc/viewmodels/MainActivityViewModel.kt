package com.example.beakonpoc.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.beakonpoc.models.BluetoothAdapterWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val bleManager: BluetoothAdapterWrapper,
    application: Application
) : AndroidViewModel(application) {

    val beaconLiveData = bleManager.updateBeacon()

    fun startScan() {
        bleManager.stopScan()
        bleManager.startScan()
    }

    fun stopScan() {
        bleManager.stopScan()
    }

    fun isBluetoothEnable(): Boolean {
        return bleManager.isEnable()
    }

    override fun onCleared() {
        super.onCleared()
        bleManager.stopScan()
    }

}