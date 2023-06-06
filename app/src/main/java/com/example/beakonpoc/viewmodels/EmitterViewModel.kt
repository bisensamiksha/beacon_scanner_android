package com.example.beakonpoc.viewmodels

import androidx.lifecycle.ViewModel
import com.example.beakonpoc.models.BeaconDataModel
import com.example.beakonpoc.models.BeaconEmitter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EmitterViewModel @Inject constructor(
    private val beaconEmitter: BeaconEmitter
) : ViewModel() {

    fun startIBeacon(beacon: BeaconDataModel) {
        if(beacon.uuid != null && beacon.major != null && beacon.minor != null){
            beaconEmitter.startIBeacon(beacon.uuid, beacon.major.toInt(), beacon.minor.toInt())
        }
    }

    fun startEddyStone(beacon: BeaconDataModel) {
        if(beacon.namespace != null && beacon.instance != null){
            beaconEmitter.startEddystone(beacon.namespace, beacon.instance)
        }
    }

    fun stopEmitter(uuid: String) {
        beaconEmitter.stopEmitting(uuid)
    }

}