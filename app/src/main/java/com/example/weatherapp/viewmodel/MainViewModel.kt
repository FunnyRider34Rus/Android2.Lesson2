package com.example.weatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.IRepository
import com.example.weatherapp.model.Repository

class MainViewModel(private val liveDataToObserve: MutableLiveData<AppState> = MutableLiveData()) :
    ViewModel() {

    private val repository : IRepository = Repository()

    fun getLiveData(): LiveData<AppState> = liveDataToObserve

    fun getWeather() {
        liveDataToObserve.postValue(AppState.Loading)
        Thread {
            liveDataToObserve.postValue(AppState.Success(repository.getWeatherFromServer()))
        }.start()
    }
}