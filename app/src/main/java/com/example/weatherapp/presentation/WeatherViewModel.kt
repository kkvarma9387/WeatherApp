package com.example.weatherapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.local.PreferencesHelper
import com.example.weatherapp.domain.usecase.GetWeatherByLocationUseCase
import com.example.weatherapp.domain.usecase.GetWeatherByCityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Custom exception for location permission denial.
 * Allows the UI to distinguish between network errors and permission issues.
 */
class LocationPermissionDeniedException : Exception("Location permission is required to fetch weather")

/**
 * ViewModel for managing weather screen state and business logic.
 * 
 * Follows MVVM architecture pattern with clear separation of concerns:
 * - UI observes uiState for reactive updates
 * - Business logic delegated to use cases
 * - Data persistence handled via PreferencesHelper
 * 
 * @param getWeatherByCityUseCase Use case for fetching weather by city name
 * @param getWeatherByLocationUseCase Use case for fetching weather by coordinates
 * @param preferencesHelper Helper for persisting last searched city (Java class)
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherByCityUseCase: GetWeatherByCityUseCase,
    private val getWeatherByLocationUseCase: GetWeatherByLocationUseCase,
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    /**
     * Searches for weather data by city name.
     * 
     * Defensive programming: Validates input before making network call to avoid
     * unnecessary API requests and potential errors. After successful search,
     * saves city to preferences for auto-loading on next app launch.
     * 
     * @param city City name to search for (can include state/country codes)
     */
    fun searchCity(city: String) {
        // Defensive check: Don't proceed with blank input
        if (city.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getWeatherByCityUseCase(city)
                .onSuccess { weather ->
                    // Save successful search to preferences for auto-load on next launch
                    preferencesHelper.saveLastCity(city)
                    
                    _uiState.update {
                        it.copy(
                            weather = weather,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { exception ->
                    // Gracefully handle errors without crashing the app
                    _uiState.update {
                        it.copy(
                            weather = null,
                            isLoading = false,
                            error = exception
                        )
                    }
                }
        }
    }

    /**
     * Loads weather data for a specific geographic location.
     * 
     * Called when user grants location permission. Uses coordinates
     * from device GPS to fetch local weather automatically.
     * 
     * @param latitude Latitude coordinate
     * @param longitude Longitude coordinate
     */
    fun loadWeatherByLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getWeatherByLocationUseCase(latitude, longitude)
                .onSuccess { weather ->
                    _uiState.update {
                        it.copy(
                            weather = weather,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            weather = null,
                            isLoading = false,
                            error = exception
                        )
                    }
                }
        }
    }

    /**
     * Loads the last searched city from preferences and fetches its weather.
     * 
     * Called on app launch to satisfy requirement #3: "Auto-load the last city
     * searched upon app launch." Only proceeds if a city was previously saved.
     */
    fun loadLastSearchedCity() {
        val lastCity = preferencesHelper.getLastCity()
        if (lastCity != null) {
            searchCity(lastCity)
        }
    }

    /**
     * Handles the case when user denies location permission.
     * 
     * Sets a specific error type so the UI can display an appropriate
     * message explaining why location-based weather isn't available.
     */
    fun onLocationPermissionDenied() {
        _uiState.update {
            it.copy(
                isLoading = false,
                error = LocationPermissionDeniedException()
            )
        }
    }

    /**
     * Clears the current error state.
     * 
     * Called after displaying error message to user via Snackbar,
     * preventing the same error from showing repeatedly.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}