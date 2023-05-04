package com.example.beakonpoc.models

import androidx.lifecycle.MutableLiveData

interface BluetoothAdapterWrapper {

    fun isEnable(): Boolean
    fun updateBeacon(): MutableLiveData<MutableList<BeaconDataModel>?>
    fun stopScan()
    fun startScan()
}