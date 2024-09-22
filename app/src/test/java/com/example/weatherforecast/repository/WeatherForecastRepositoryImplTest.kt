package com.example.weatherforecast.repository

import com.example.weatherforecast.data.apiHelper.Result
import com.example.weatherforecast.data.local.dao.WeatherDao
import com.example.weatherforecast.data.local.entities.LocationEntity
import com.example.weatherforecast.data.local.entities.WeatherEntity
import com.example.weatherforecast.data.local.entities.WeatherForecastEntity
import com.example.weatherforecast.data.local.entities.toDomainModel
import com.example.weatherforecast.data.remote.model.CurrentWeather
import com.example.weatherforecast.data.remote.model.Location
import com.example.weatherforecast.data.remote.model.WeatherResponse
import com.example.weatherforecast.data.remote.service.WeatherApiService
import com.example.weatherforecast.repositories.WeatherForecastRepositoryImpl
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response

@ExperimentalCoroutinesApi
class WeatherForecastRepositoryImplTest {

    // Mock dependencies
    @Mock
    private lateinit var mockWeatherApiService: WeatherApiService

    @Mock
    private lateinit var mockWeatherDao: WeatherDao

    // System under test
    private lateinit var repository: WeatherForecastRepositoryImpl

    @Before
    fun setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this)
        mockWeatherApiService = mock(WeatherApiService::class.java)
        mockWeatherDao = mock(WeatherDao::class.java)

        // Initialize repository with mocked dependencies
        repository = WeatherForecastRepositoryImpl(mockWeatherApiService, mockWeatherDao)
    }

    @Test
    fun `getLocalWeatherUpdate returns Success when local weather data exists`(): Unit =
        runBlocking {
            // Arrange
            val cityName = "Lahore"
            val localWeatherEntity = mock(WeatherEntity::class.java)
            val localLocationEntity = mock(LocationEntity::class.java)

            // Mocking DAO behavior
            `when`(mockWeatherDao.getWeatherByCityName(cityName)).thenReturn(localWeatherEntity)
            `when`(mockWeatherDao.getLocationByCityName(cityName)).thenReturn(localLocationEntity)

            // Act
            val result = repository.getLocalWeatherUpdate(cityName)

            // Assert
            assertEquals(
                Result.Success(
                    data = WeatherResponse(
                        location = localLocationEntity.toDomainModel(),
                        current = localWeatherEntity.toDomainModel()
                    ), responseCode = 200
                ), result
            )

            verify(mockWeatherDao).getWeatherByCityName(cityName)
            verify(mockWeatherDao).getLocationByCityName(cityName)
        }

    @Test
    fun `getLocalWeatherUpdate returns null when local weather data does not exist`(): Unit =
        runBlocking {
            // Arrange
            val cityName = "Lahore"

            // Mocking DAO behavior
            `when`(mockWeatherDao.getWeatherByCityName(cityName)).thenReturn(null)
            `when`(mockWeatherDao.getLocationByCityName(cityName)).thenReturn(null)

            // Act
            val result = repository.getLocalWeatherUpdate(cityName)

            // Assert
            assertNull(result)

            verify(mockWeatherDao).getWeatherByCityName(cityName)
            verify(mockWeatherDao).getLocationByCityName(cityName)
        }

    @Test
    fun `fetchWeatherUpdate returns Success when API fetch is successful`(): Unit = runBlocking {
        // Arrange
        val cityName = "Lahore"
        val remoteWeatherResponse = WeatherResponse(
            location = Location(name = cityName),
            current = CurrentWeather(tempC = 30.0, humidity = 65, windMph = 15.0),
            forecast = null
        )

        // Mock the service call to return a Response object
        val apiResponse = Response.success(remoteWeatherResponse)
        `when`(mockWeatherApiService.getWeatherForecast(cityName)).thenReturn(apiResponse)

        // Act
        val result = repository.fetchWeatherUpdate(cityName)

        // Assert
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(30.0, successResult.data?.current?.tempC)
        assertEquals("Lahore", successResult.data?.location?.name)

        verify(mockWeatherApiService).getWeatherForecast(cityName)
    }

    @Test
    fun `fetchWeatherUpdate returns Failure when API fetch fails`(): Unit = runBlocking {
        // Arrange
        val cityName = "Lahore"
        val errorMessage = "Server error"
        val errorResponse = Response.error<WeatherResponse>(
            500,
            ResponseBody.create(MediaType.parse("application/json"), errorMessage)
        )

        // Mock the service call to simulate an API failure
        `when`(mockWeatherApiService.getWeatherForecast(cityName)).thenReturn(errorResponse)

        // Act
        val result = repository.fetchWeatherUpdate(cityName)

        // Assert
        assertTrue(result is Result.Failure)
        val failureResult = result as Result.Failure
        assertEquals(
            errorMessage,
            failureResult.errorBody?.string()
        ) // Adjust this to match your actual message

        verify(mockWeatherApiService).getWeatherForecast(cityName)
    }

    @Test
    fun `fetchWeatherUpdate returns Failure when API returns null data`(): Unit = runBlocking {
        // Arrange
        val cityName = "Lahore"

        // Mock the service call to simulate an API response with null data
        val apiResponse = Response.success<WeatherResponse>(null)
        `when`(mockWeatherApiService.getWeatherForecast(cityName)).thenReturn(apiResponse)

        // Act
        val result = repository.fetchWeatherUpdate(cityName)

        // Assert
        assertTrue(result is Result.Failure)
        val failureResult = result as Result.Failure
        assertEquals("No data received from remote", failureResult.message)

        verify(mockWeatherApiService).getWeatherForecast(cityName)
    }

    @Test
    fun `insertWeatherData returns Success when weather data is valid`() = runTest {
        // Arrange
        val localWeatherEntity = mock(WeatherEntity::class.java)
        val localLocationEntity = mock(LocationEntity::class.java)
        val weatherResponse = WeatherResponse(
            location = Location(name = "London"),
            current = CurrentWeather(tempC = 30.0, humidity = 65, windMph = 15.0),
            forecast = null
        )
        val remoteResult = Result.Success(weatherResponse, 200)
        val searchQuery = "London"

        // Mock the localDataSource interactions using Mockito
        `when`(mockWeatherDao.insertLocation(localLocationEntity)).thenReturn(Unit)
        `when`(mockWeatherDao.getLocationByCityName(searchQuery)).thenReturn(
            LocationEntity(id = 1, name = "London", region = "", country = "", lat = 0.0, lon = 0.0)
        )
        `when`(mockWeatherDao.insertWeather(localWeatherEntity)).thenReturn(Unit)
        `when`(mockWeatherDao.getWeatherByCityName(searchQuery)).thenReturn(
            WeatherEntity(id = 1, locationId = 1, temperature = 15.0)
        )
        // Act
        val result = repository.insertWeatherData(remoteResult, searchQuery)
        // Assert
        assertTrue(result is Result.Success)
        assertEquals(200, (result as Result.Success).responseCode)
        assertEquals("London", result.data?.location?.name)
        assertEquals(15.0, result.data?.current?.tempC)
    }

    @Test
    fun `insertWeatherData returns Failure when weather data is null`(): Unit = runBlocking {
        // Arrange
        val cityName = "Lahore"
        val remoteResult = Result.Success(null, 200)

        // Act
        val result = repository.insertWeatherData(remoteResult, cityName)

        // Assert
        assertTrue(result is Result.Failure)
        assertEquals("No data received from remote", (result as Result.Failure).message)
    }

    @Test
    fun `insertWeatherData returns Failure when remoteResult indicates failure`(): Unit =
        runBlocking {
            // Arrange
            val cityName = "Lahore"
            val errorMessage = "Error fetching data"
            val remoteResult =
                Result.Failure<WeatherResponse>(message = errorMessage, errorCode = 500)

            // Act
            val result = repository.insertWeatherData(remoteResult, cityName)

            // Assert
            assertTrue(result is Result.Failure)
            assertEquals(errorMessage, (result as Result.Failure).message)
        }

    @Test
    fun `insertWeatherData returns Failure when weather data is empty`(): Unit = runBlocking {
        // Arrange
        val cityName = "Lahore"
        val remoteResult = Result.Success(mock(WeatherResponse::class.java), 200)

        // Mock to simulate no data
        `when`(remoteResult.data?.location).thenReturn(null)

        // Act
        val result = repository.insertWeatherData(remoteResult, cityName)

        // Assert
        assertTrue(result is Result.Failure)
        assertEquals("Weather data is null", (result as Result.Failure).message)
    }

    @Test
    fun `getWeatherUpdate emits local data when available`(): Unit = runBlocking {
        // Arrange
        val cityName = "Lahore"

        `when`(mockWeatherDao.getWeatherByCityName(cityName)).thenReturn(mock(WeatherEntity::class.java))
        `when`(mockWeatherDao.getLocationByCityName(cityName)).thenReturn(mock(LocationEntity::class.java))

        // Act
        val result = repository.getWeatherUpdate(cityName).first()

        // Assert
        assertTrue(result is Result.Success)
        verify(mockWeatherApiService, never()).getWeatherForecast(cityName)
    }

    @Test
    fun `getWeatherUpdate emits Failure when remote fetch fails`() = runBlocking {
        // Arrange
        val cityName = "Lahore"
        val errorMessage = "Network error"

        // Mocking DAO to return null for local data
        `when`(mockWeatherDao.getWeatherByCityName(cityName)).thenReturn(null)

        // Mocking remote API to return an error
        `when`(mockWeatherApiService.getWeatherForecast(cityName))
            .thenReturn(
                Response.error(
                    500,
                    ResponseBody.create(MediaType.parse("application/json"), errorMessage)
                )
            )

        // Act
        val result = repository.getWeatherUpdate(cityName).first()

        // Assert
        assertTrue(result is Result.Failure)
        val failureResult = result as Result.Failure
        assertEquals(errorMessage, failureResult.errorBody?.string())
    }

    @Test
    fun `getWeatherForecast returns Success when local forecasts exist`(): Unit = runBlocking {
        // Arrange
        val cityName = "Lahore"
        val mockForecasts = listOf(mock(WeatherForecastEntity::class.java), mock(WeatherForecastEntity::class.java))

        // Mocking DAO to return a list of forecasts
        `when`(mockWeatherDao.getForecastByCityName(cityName)).thenReturn(mockForecasts)

        // Act
        val result = repository.getWeatherForecast(cityName).first()

        // Assert
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(mockForecasts, successResult.data)
        assertEquals(200, successResult.responseCode)

        verify(mockWeatherDao).getForecastByCityName(cityName)
    }

    @Test
    fun `getWeatherForecast returns Failure when no local forecasts exist`(): Unit = runBlocking {
        // Arrange
        val cityName = "Lahore"

        // Mocking DAO to return an empty list
        `when`(mockWeatherDao.getForecastByCityName(cityName)).thenReturn(emptyList())

        // Act
        val result = repository.getWeatherForecast(cityName).first()

        // Assert
        assertTrue(result is Result.Failure)
        val failureResult = result as Result.Failure
        assertEquals("No forecasts found locally.", failureResult.message)
        assertNotNull(failureResult.exception)

        verify(mockWeatherDao).getForecastByCityName(cityName)
    }
}