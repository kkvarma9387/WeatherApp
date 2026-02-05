package com.example.weatherapp.presentation
import com.example.weatherapp.domain.model.Weather

data class WeatherUiState(
    val weather: Weather? = null,
    val isLoading: Boolean = false,
    val error: Throwable? = null
)