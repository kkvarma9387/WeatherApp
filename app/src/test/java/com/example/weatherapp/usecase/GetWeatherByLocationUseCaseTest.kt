package com.example.weatherapp.usecase

import com.example.weatherapp.data.local.PreferencesHelper
import com.example.weatherapp.domain.model.Weather
import com.example.weatherapp.domain.usecase.GetWeatherByCityUseCase
import com.example.weatherapp.domain.usecase.GetWeatherByLocationUseCase
import com.example.weatherapp.presentation.LocationPermissionDeniedException
import com.example.weatherapp.presentation.WeatherViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val getWeatherByCityUseCase: GetWeatherByCityUseCase = mockk()
    private val getWeatherByLocationUseCase: GetWeatherByLocationUseCase = mockk()
    private val preferencesHelper: PreferencesHelper = mockk(relaxed = true)

    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel = WeatherViewModel(
            getWeatherByCityUseCase,
            getWeatherByLocationUseCase,
            preferencesHelper
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchCity success updates uiState with weather`() = runTest {
        val weather = Weather("Krugerville", 8.7, "clear sky", "icon")
        coEvery { getWeatherByCityUseCase(any()) } returns Result.success(weather)

        viewModel.searchCity("Krugerville")

        advanceUntilIdle()  // wait for coroutine to complete

        val state = viewModel.uiState.value
        assertEquals(weather, state.weather)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `searchCity failure updates uiState with error`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { getWeatherByCityUseCase(any()) } returns Result.failure(exception)

        viewModel.searchCity("Krugerville")

        advanceUntilIdle()  // wait for coroutine to complete

        val state = viewModel.uiState.value
        assertNull(state.weather)
        assertFalse(state.isLoading)
        assertEquals(exception, state.error)
    }

    @Test
    fun `searchCity blank does nothing`() = runTest {
        val initial = viewModel.uiState.value

        viewModel.searchCity("")

        val state = viewModel.uiState.value
        assertEquals(initial, state)
    }

    @Test
    fun `loadWeatherByLocation success updates state`() = runTest {
        val weather = Weather("Krugerville", 8.7, "clear sky", "icon")
        coEvery { getWeatherByLocationUseCase(any(), any()) } returns Result.success(weather)

        viewModel.loadWeatherByLocation(33.28, -96.99)

        advanceUntilIdle()  // wait for coroutine to complete

        val state = viewModel.uiState.value
        assertEquals(weather, state.weather)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `loadWeatherByLocation failure updates error state`() = runTest {
        val exception = RuntimeException("Location error")
        coEvery { getWeatherByLocationUseCase(any(), any()) } returns Result.failure(exception)

        viewModel.loadWeatherByLocation(0.0, 0.0)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.weather)
        assertFalse(state.isLoading)
        assertEquals(exception, state.error)
    }

    @Test
    fun `onLocationPermissionDenied sets permission error`() = runTest {
        viewModel.onLocationPermissionDenied()

        val state = viewModel.uiState.value
        assertTrue(state.error is LocationPermissionDeniedException)
        assertFalse(state.isLoading)
    }

    @Test
    fun `clearError clears error`() = runTest {
        viewModel.onLocationPermissionDenied()

        viewModel.clearError()

        val state = viewModel.uiState.value
        assertNull(state.error)
    }
}
