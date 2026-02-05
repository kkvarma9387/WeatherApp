package com.example.weatherapp.presentation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherapp.R
import com.example.weatherapp.presentation.utils.LocationHelper
import com.example.weatherapp.presentation.composables.WeatherContent

/**
 * Main weather screen composable following Jetpack Compose best practices.
 * 
 * Handles the complete user flow:
 * 1. Request location permission on first launch
 * 2. If granted, fetch weather by location
 * 3. If denied, attempt to load last searched city (requirement #3)
 * 4. Display weather data or errors via Snackbar
 * 
 * @param modifier Modifier for customizing layout
 * @param viewModel WeatherViewModel injected via Hilt
 */
@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Track whether we've attempted to load last city
    var hasAttemptedLastCityLoad by remember { mutableStateOf(false) }

    val errorMessage = stringResource(R.string.error_loading_weather)
    val locationPermissionDenied = stringResource(R.string.error_location_permission_denied)

    /**
     * Location permission launcher with fallback to last searched city.
     * 
     * Flow:
     * - If permission granted: Fetch weather by device location
     * - If permission denied: Fall back to loading last searched city from preferences
     * 
     * This satisfies requirement #4 (location access) and #3 (auto-load last city).
     */
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                LocationHelper.fetchLastKnownLocation(context) { lat, lon ->
                    viewModel.loadWeatherByLocation(lat, lon)
                }
            } else {
                viewModel.onLocationPermissionDenied()
                // Fallback: Load last searched city if permission denied
                if (!hasAttemptedLastCityLoad) {
                    viewModel.loadLastSearchedCity()
                    hasAttemptedLastCityLoad = true
                }
            }
        }
    )

    /**
     * Request location permission on first composition.
     * This satisfies requirement #4: "Ask the User for location access"
     */
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    /**
     * Display error messages via Snackbar.
     * Distinguishes between location permission errors and API/network errors
     * to provide user-friendly, context-specific messages.
     */
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            val message = when (error) {
                is LocationPermissionDeniedException -> locationPermissionDenied
                else -> error.message ?: errorMessage
            }
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            WeatherContent(
                weather = uiState.weather,
                onSearchCity = viewModel::searchCity,
                modifier = Modifier.fillMaxSize()
            )

            // Show loading indicator during API calls
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}