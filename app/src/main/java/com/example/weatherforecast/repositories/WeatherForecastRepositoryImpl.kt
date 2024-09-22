package com.example.weatherforecast.repositories

import com.example.weatherforecast.data.apiHelper.ApiHelper
import com.example.weatherforecast.data.apiHelper.Result
import com.example.weatherforecast.data.local.dao.WeatherDao
import com.example.weatherforecast.data.local.entities.WeatherForecastEntity
import com.example.weatherforecast.data.local.entities.toDomainModel
import com.example.weatherforecast.data.remote.model.WeatherResponse
import com.example.weatherforecast.data.remote.model.toDbModel
import com.example.weatherforecast.data.remote.model.toEntities
import com.example.weatherforecast.data.remote.service.WeatherApiService
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class WeatherForecastRepositoryImpl @Inject constructor(
    private val remoteDataSource: WeatherApiService, private val localDataSource: WeatherDao
) : WeatherForecastRepository, ApiHelper() {

    override suspend fun getWeatherUpdate(searchQuery: String): Flow<Result<WeatherResponse>> =
        flow {
            // Try to get data from the local database first
            val localWeatherResult = getLocalWeatherUpdate(searchQuery)
            if (localWeatherResult != null) {
                emit(localWeatherResult)
                return@flow
            }
            // If no local data, fetch from remote
            val remoteResult = fetchWeatherUpdate(searchQuery)
            // Handle API response and insert into DB
            val finalResult = insertWeatherData(remoteResult, searchQuery)
            emit(finalResult)
        }


    override suspend fun getLocalWeatherUpdate(searchQuery: String): Result<WeatherResponse>? {
        val localCurrentWeather = localDataSource.getWeatherByCityName(searchQuery)
        val localLocation = localDataSource.getLocationByCityName(searchQuery)

        return if (localCurrentWeather != null) {
            Result.Success(
                data = WeatherResponse(
                    location = localLocation?.toDomainModel(),
                    current = localCurrentWeather.toDomainModel()
                ), responseCode = 200
            )
        } else {
            null // No local data found
        }
    }

    override suspend fun fetchWeatherUpdate(searchQuery: String): Result<WeatherResponse> {
        val response = serviceCall { remoteDataSource.getWeatherForecast(searchQuery) }.first()
        return when {
            response is Result.Failure && response.errorCode == 200 -> {
                Result.Failure(
                    message = "No data received from remote",
                    errorCode = 400
                )
            }
            else -> {
                response
            }
        }
    }

    override suspend fun insertWeatherData(
        remoteResult: Result<WeatherResponse>, searchQuery: String
    ): Result<WeatherResponse> {
        return when (remoteResult) {
            is Result.Success -> {
                remoteResult.data?.let { data ->

                    val locationEntity = data.location?.toDbModel()
                    locationEntity?.let { location ->
                        localDataSource.insertLocation(
                            location
                        )
                    }
                    val weatherEntity = data.current?.toDbModel(
                        localDataSource.getLocationByCityName(searchQuery)?.id ?: 0
                    )

                    // Check if weatherEntity is not null before proceeding
                    weatherEntity?.let { weather ->
                        // Insert into the database
                        localDataSource.insertWeather(weather)
                        val forecastEntities = data.forecast?.toEntities(
                            localDataSource.getWeatherByCityName(searchQuery)?.id ?: 0
                        )
                        forecastEntities?.let { forecast ->
                            localDataSource.insertAllForecasts(
                                forecast
                            )
                        }
                        val location = localDataSource.getLocationByCityName(
                            searchQuery
                        )?.toDomainModel()
                        val currentWeather = localDataSource.getWeatherByCityName(
                            searchQuery
                        )?.toDomainModel()
                        // Emit the inserted data
                        Result.Success(
                            WeatherResponse(
                                location = location,
                                current = currentWeather,
                                forecast = data.forecast
                            ), 200
                        )

                    } ?: Result.Failure(message = "Weather data is null", errorCode = 400)
                } ?: Result.Failure(
                    message = "No data received from remote", errorCode = 400
                )
            }

            is Result.Failure -> {
                Result.Failure(
                    remoteResult.message,
                    exception = remoteResult.exception,
                    errorCode = remoteResult.errorCode,
                    errorBody = remoteResult.errorBody
                )
            }

            else -> Result.Failure(
                message = "No data received from remote", errorCode = 400
            )
        }
    }

    override suspend fun getWeatherForecast(cityName: String): Flow<Result<List<WeatherForecastEntity>>> =
        flow {
            val localForecasts = localDataSource.getForecastByCityName(cityName)

            if (localForecasts.isNotEmpty()) {
                emit(Result.Success(localForecasts, responseCode = 200))
            } else {
                emit(
                    Result.Failure(
                        message = "No forecasts found locally.",
                        exception = Exception("No forecasts found locally."),
                        errorCode = 400
                    )
                )
            }
        }
}


