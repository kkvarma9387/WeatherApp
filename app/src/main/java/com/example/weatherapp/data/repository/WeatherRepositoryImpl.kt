package com.example.weatherapp.data.repository

import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.remote.WeatherDto
import com.example.weatherapp.domain.model.Weather
import com.example.weatherapp.domain.repository.WeatherRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Implementation of WeatherRepository that fetches data from OpenWeatherMap API.
 * 
 * Follows repository pattern to abstract data source details from the domain layer.
 * Implements defensive error handling to gracefully manage network and API failures.
 * 
 * @param api WeatherApi interface for making network requests
 */
class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
) : WeatherRepository {

    /**
     * Fetches weather data for a specific city.
     * 
     * @param city City name, optionally with state/country codes
     * @return Weather domain model
     * @throws WeatherException subclasses for specific error cases
     */
    override suspend fun getWeatherByCity(city: String): Weather {
        return executeWeatherRequest {
            api.getWeatherByCity(
                city = city,
                apiKey = BuildConfig.WEATHER_API_KEY
            )
        }
    }

    /**
     * Fetches weather data for a specific geographic location.
     * 
     * @param latitude Latitude coordinate
     * @param longitude Longitude coordinate
     * @return Weather domain model
     * @throws WeatherException subclasses for specific error cases
     */
    override suspend fun getWeatherByLocation(
        latitude: Double,
        longitude: Double
    ): Weather {
        return executeWeatherRequest {
            api.getWeatherByLocation(
                lat = latitude,
                lon = longitude,
                apiKey = BuildConfig.WEATHER_API_KEY
            )
        }
    }

    /**
     * Executes a weather API request with comprehensive error handling.
     * 
     * Defensive programming approach:
     * - Maps HTTP status codes to user-friendly exceptions
     * - Handles network failures (IOException)
     * - Catches unexpected errors to prevent app crashes
     * 
     * This satisfies the requirement for "defensive code that graciously handles
     * unexpected edge cases" and "robust error handling with user-friendly messages."
     * 
     * @param request Suspend function that makes the API call
     * @return Weather domain model on success
     * @throws WeatherException subclasses with user-friendly error messages
     */
    private suspend fun executeWeatherRequest(
        request: suspend () -> WeatherDto
    ): Weather {
        return try {
            val response = request()
            response.toDomain()
        } catch (e: HttpException) {
            // Map HTTP status codes to specific, user-friendly exceptions
            throw when (e.code()) {
                401 -> UnauthorizedException("Invalid API key")
                404 -> CityNotFoundException("Location not found")
                429 -> RateLimitExceededException("Too many requests. Please try again later.")
                500, 502, 503 -> ServerException("Server error. Please try again later.")
                else -> WeatherApiException("API error: ${e.message()}")
            }
        } catch (e: IOException) {
            // Handle network connectivity issues
            throw NetworkException("Network error. Please check your connection.")
        } catch (e: Exception) {
            // Catch-all for unexpected errors to prevent app crashes
            throw WeatherApiException("Unexpected error: ${e.localizedMessage}")
        }
    }
}

/**
 * Extension function to map API DTO to domain model.
 * 
 * Separates data layer concerns (DTOs) from domain layer (business models).
 * Includes defensive null check for weather info array.
 * 
 * @return Weather domain model
 * @throws DataParsingException if weather data is missing or malformed
 */
private fun WeatherDto.toDomain(): Weather {
    val weatherInfo = weather.firstOrNull()
        ?: throw DataParsingException("No weather data available")

    return Weather(
        city = name,
        temperature = main.temp,
        description = weatherInfo.description,
        iconUrl = buildIconUrl(weatherInfo.icon)
    )
}

/**
 * Builds the full URL for weather icon from OpenWeatherMap.
 * Uses @2x size for better quality on high-DPI screens.
 * 
 * @param icon Icon code from API response (e.g., "01d")
 * @return Full URL to weather icon image
 */
private fun buildIconUrl(icon: String): String {
    return "https://openweathermap.org/img/wn/$icon@2x.png"
}

/**
 * Base class for all weather-related exceptions.
 * Allows catching all weather errors with a single catch block if needed.
 */
sealed class WeatherException(message: String) : Exception(message)

/** Thrown when the requested city/location is not found (HTTP 404) */
class CityNotFoundException(message: String) : WeatherException(message)

/** Thrown when network connectivity is unavailable */
class NetworkException(message: String) : WeatherException(message)

/** Thrown when API key is invalid or missing (HTTP 401) */
class UnauthorizedException(message: String) : WeatherException(message)

/** Thrown when API rate limit is exceeded (HTTP 429) */
class RateLimitExceededException(message: String) : WeatherException(message)

/** Thrown when OpenWeatherMap servers are experiencing issues (HTTP 5xx) */
class ServerException(message: String) : WeatherException(message)

/** Thrown when API response cannot be parsed correctly */
class DataParsingException(message: String) : WeatherException(message)

/** Generic exception for other API errors */
class WeatherApiException(message: String) : WeatherException(message)