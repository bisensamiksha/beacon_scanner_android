package com.example.beakonpoc.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.beakonpoc.models.BLEManager
import com.example.beakonpoc.models.BeaconDataModel


class MainActivityViewModel(application: Application): AndroidViewModel(application) {

    private val bleManager = BLEManager(application)
    
    fun updateRSSI(): LiveData<BeaconDataModel?>{
        return bleManager.updateBeacon()
    }

    fun startScan(){
        bleManager.stopScan()
        bleManager.startScan()
    }

    fun stopScan(){
        bleManager.stopScan()
    }

    override fun onCleared() {
        super.onCleared()
        bleManager.stopScan()
    }

}