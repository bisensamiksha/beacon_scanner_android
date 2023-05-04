package com.example.beakonpoc.di

import android.app.Application
import com.example.beakonpoc.models.BLEManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideBLEManager(application: Application): BLEManager {
        return BLEManager(application)
    }
}