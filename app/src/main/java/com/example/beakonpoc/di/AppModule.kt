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
annotation class AppBLEManager

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @AppBLEManager
    fun provideBLEManager(application: Application): BLEManager {
        return BLEManager(application)
    }

    @Provides
    fun provideBeaconEmitter(application: Application):BeaconEmitter{
        return BeaconEmitter(application)
    }
}