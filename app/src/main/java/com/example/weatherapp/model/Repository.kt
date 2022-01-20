package com.example.weatherapp.model

class Repository : IRepository {
    override fun getWeatherFromServer(): City = City("Moscow", +3)
}