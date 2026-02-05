package com.example.weatherapp.domain.usecase
import com.example.weatherapp.domain.model.Weather
import com.example.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject


// Use case for fetching weather using device location.
// Triggered only when location permission is granted.
class GetWeatherByLocationUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): Result<Weather> {
        return try {
            // Fetch weather based on latitude and longitude
            val weather = repository.getWeatherByLocation(latitude, longitude)
            Result.success(weather)
        } catch (e: Exception) {
            // Gracefully handle location or network related failures
            Result.failure(e)
        }
    }
}