package com.example.weatherapp.model

interface IRepository {
    fun getWeatherFromServer() : Weather
    fun getWeatherFromLocalStorageRus(): List<Weather>
    fun getWeatherFromLocalStorageWorld(): List<Weather>
}