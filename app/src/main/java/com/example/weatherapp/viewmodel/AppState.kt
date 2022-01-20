package com.example.weatherapp.viewmodel

import com.example.weatherapp.model.City

sealed class AppState {
    data class Success(val weatherData : City) : AppState()
    data class Error(val error : Throwable) : AppState()
    object Loading : AppState()
}
