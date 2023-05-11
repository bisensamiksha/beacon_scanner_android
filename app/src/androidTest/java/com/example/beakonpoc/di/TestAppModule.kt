package com.example.beakonpoc.di

import android.app.Application
import com.example.beakonpoc.models.BLEManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class TestBLEManager

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @TestBLEManager
    fun provideBLEManager(application: Application): BLEManager {
        return BLEManager(application)
    }

}