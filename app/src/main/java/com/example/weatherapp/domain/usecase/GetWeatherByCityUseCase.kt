package com.example.weatherapp.domain.usecase
import com.example.weatherapp.domain.model.Weather
import com.example.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

// Use case for fetching weather by city.
// Keeps business logic out of the ViewModel and UI.
class GetWeatherByCityUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(city: String): Result<Weather> {
        return try {
            // Delegate data fetching to repository
            val weather = repository.getWeatherByCity(city)
            Result.success(weather)
        } catch (e: Exception) {
            // Defensive handling to avoid crashing the UI on API/network errors
            Result.failure(e)
        }
    }
}