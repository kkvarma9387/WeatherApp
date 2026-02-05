package com.example.weatherapp.data.remote
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherApi {

    /**
     * Fetches weather data for a specific city by name.
     * Supports city name alone or with state/country codes for more precise results.
     * 
     * @param city City name, optionally with state and country (e.g., "London,UK" or "Austin,TX,US")
     * @param apiKey OpenWeatherMap API key for authentication
     * @param units Unit system for temperature (default: metric for Celsius)
     * @return WeatherDto containing current weather information
     */
    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherDto

    /**
     * Fetches weather data for a specific geographic location using coordinates.
     * Used when the user grants location permission for automatic weather retrieval.
     * 
     * @param lat Latitude coordinate
     * @param lon Longitude coordinate
     * @param apiKey OpenWeatherMap API key for authentication
     * @param units Unit system for temperature (default: metric for Celsius)
     * @return WeatherDto containing current weather information
     */
    @GET("weather")
    suspend fun getWeatherByLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherDto
}
