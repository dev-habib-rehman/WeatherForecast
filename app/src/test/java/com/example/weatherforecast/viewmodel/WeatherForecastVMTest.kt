package com.example.weatherforecast.viewmodel

import com.example.weatherforecast.MainCoroutineRule
import com.example.weatherforecast.data.apiHelper.Result
import com.example.weatherforecast.data.local.entities.WeatherForecastEntity
import com.example.weatherforecast.data.remote.model.WeatherResponse
import com.example.weatherforecast.repositories.WeatherForecastRepository
import com.example.weatherforecast.ui.viewmodel.WeatherForecastVM
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
@HiltAndroidTest
class WeatherForecastVMTest {

    @get:Rule
    var coroutinesTestRule = MainCoroutineRule()

    @Mock
    private lateinit var mockRepository: WeatherForecastRepository

    private lateinit var viewModel: WeatherForecastVM

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = WeatherForecastVM(mockRepository)
    }

    @Test
    fun `initial state should be loading`() {
        assertTrue(viewModel.weatherState.value is Result.Loading)
        assertTrue(viewModel.weatherForecastState.value is Result.Loading)
    }

    @Test
    fun `getWeatherUpdate returns Success`() = runTest {
        // Arrange
        val mockResponse = Result.Success(
            WeatherResponse(location = null, current = null, forecast = null),
            responseCode = 200
        )

        `when`(mockRepository.getWeatherUpdate("Lahore")).thenReturn(flow {
            emit(mockResponse)
        })

        // Act
        viewModel.getWeatherUpdate("Lahore")

        val result = viewModel.weatherState.first()
        // Assert
        assertTrue(result is Result.Success)
    }

    @Test
    fun `getWeatherUpdate returns Failure`() = runTest {
        // Arrange
        val mockResponse = Result.Failure<WeatherResponse>(message = "Error", errorCode = 400)

        `when`(mockRepository.getWeatherUpdate("Lahore")).thenReturn(flow {
            emit(mockResponse)
        })

        // Act
        viewModel.getWeatherUpdate("Lahore")

        val result = viewModel.weatherState.first()
        // Assert
        assertTrue(result is Result.Failure)
        val failureResult = result as Result.Failure
        assertEquals("Error", failureResult.message)
    }

    @Test
    fun `getWeatherForecast returns Success`() = runTest {
        // Arrange
        val mockForecastResponse =
            Result.Success(listOf<WeatherForecastEntity>(), responseCode = 200)

        `when`(mockRepository.getWeatherForecast("Lahore")).thenReturn(flow {
            emit(mockForecastResponse)
        })

        // Act
        viewModel.getWeatherForecast("Lahore")

        val result = viewModel.weatherForecastState.first()

        // Assert
        assertTrue(result is Result.Success)
    }

    @Test
    fun `getWeatherForecast returns Failure`() = runTest {
        // Arrange
        val mockForecastResponse = Result.Failure<List<WeatherForecastEntity>>(
            message = "No forecasts found",
            errorCode = 400
        )

        `when`(mockRepository.getWeatherForecast("Lahore")).thenReturn(flow {
            emit(mockForecastResponse)
        })

        // Act
        viewModel.getWeatherForecast("Lahore")
        val result  = viewModel.weatherForecastState.first()

        // Assert
        assertTrue(result is Result.Failure)
        val failureResult = result as Result.Failure
        assertEquals("No forecasts found", failureResult.message)
    }

    @After
    fun tearDown() {
        // Reset any mutable state or resources here if needed
        Mockito.reset(mockRepository)
    }
}
