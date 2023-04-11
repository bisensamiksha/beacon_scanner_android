package com.example.beakonpoc

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData


class MainActivityViewMode(application: Application): AndroidViewModel(application) {
    private val distance = MutableLiveData<Int>()

    fun updateDistance(): MutableLiveData<Int>{
        return distance
    }

}