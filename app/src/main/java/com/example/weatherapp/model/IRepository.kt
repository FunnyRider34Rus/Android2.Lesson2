package com.example.weatherapp.model

interface IRepository {
    fun getWeatherFromServer() : City
}