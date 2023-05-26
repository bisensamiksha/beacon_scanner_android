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

    @Provides
    @TestBLEManager
    fun provideBLEManager(application: Application): BLEManager {
        return BLEManager(application)
    }

    @Provides
    @TestBeaconEmitter
    fun provideBeaconEmitter(application: Application): BeaconEmitter{
        return BeaconEmitter(application)
    }

}