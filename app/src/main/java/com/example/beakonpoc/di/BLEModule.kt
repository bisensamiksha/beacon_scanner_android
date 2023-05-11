package com.example.beakonpoc.di

import com.example.beakonpoc.models.BLEManager
import com.example.beakonpoc.models.BluetoothAdapterWrapper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class BLEModule {

    @Binds
    abstract fun bindBLEManager(@AppBLEManager impl: BLEManager): BluetoothAdapterWrapper
}