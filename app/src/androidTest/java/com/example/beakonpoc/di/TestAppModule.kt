package com.example.beakonpoc.di

import android.app.Application
import com.example.beakonpoc.models.BLEManager
import com.example.beakonpoc.models.BeaconEmitter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class TestBLEManager

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class TestBeaconEmitter
@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides //provides BLEManager
    @TestBLEManager //qualifier BLEManager dependency injection for testing
    fun provideBLEManager(application: Application): BLEManager {
        return BLEManager(application)
    }

    @Provides //provides BeaconEmitter
    @TestBeaconEmitter //qualifier BeaconEmitter dependency injection for testing
    fun provideBeaconEmitter(application: Application): BeaconEmitter{
        return BeaconEmitter(application)
    }

}