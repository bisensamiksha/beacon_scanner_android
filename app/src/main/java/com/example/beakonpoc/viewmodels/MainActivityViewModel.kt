package com.example.beakonpoc.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.beakonpoc.models.BLEManager


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    val bleManager = BLEManager(application)
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