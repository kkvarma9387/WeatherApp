package com.example.weatherapp.usecase


import com.example.weatherapp.domain.model.Weather
import com.example.weatherapp.domain.repository.WeatherRepository
import com.example.weatherapp.domain.usecase.GetWeatherByCityUseCase
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetWeatherByCityUseCaseTest {

    private val repository: WeatherRepository = mockk()
    private lateinit var useCase: GetWeatherByCityUseCase

    @Before
    fun setup() {
        useCase = GetWeatherByCityUseCase(repository)
    }

    @Test
    fun `returns success when repository returns weather`() = runTest {
        val weather = Weather("Krugerville", 8.7, "clear sky", "icon")
        coEvery { repository.getWeatherByCity("Krugerville") } returns weather

        val result = useCase("Krugerville")

        assertTrue(result.isSuccess)
        assertEquals(weather, result.getOrNull())
    }

    @Test
    fun `returns failure when repository throws exception`() = runTest {
        val exception = RuntimeException("API error")
        coEvery { repository.getWeatherByCity("Krugerville") } throws exception

        val result = useCase("Krugerville")

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
