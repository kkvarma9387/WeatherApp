package com.example.weatherapp.domain.repository

import com.example.weatherapp.domain.model.Weather


/*
    Repository contract for weather data.
   Keeps the domain layer independent of the data source (API, cache, etc.).
 */
interface WeatherRepository {
    suspend fun getWeatherByCity(city: String): Weather
    suspend fun getWeatherByLocation(
        lat: Double,
        lon: Double
    ): Weather
}
