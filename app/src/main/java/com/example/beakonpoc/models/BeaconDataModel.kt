package com.example.beakonpoc.models

data class BeaconDataModel(
    val type: BeaconType,
    val uuid: String?,
    val major: String? = null,
    val minor: String? = null,
    val rssi: String? =  null,
    val namespace: String? = null,
    val instance: String? = null
)

enum class BeaconType{
    iBeacon, eddyStone
}
