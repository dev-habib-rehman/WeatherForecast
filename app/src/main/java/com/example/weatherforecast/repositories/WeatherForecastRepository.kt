package com.example.weatherforecast.repositories

import com.example.weatherforecast.data.apiHelper.Result
import com.example.weatherforecast.data.local.entities.WeatherForecastEntity
import com.example.weatherforecast.data.remote.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherForecastRepository {
    suspend fun getWeatherUpdate(searchQuery: String): Flow<Result<WeatherResponse>>

    suspend fun getLocalWeatherUpdate(searchQuery: String): Result<WeatherResponse>?

    suspend fun fetchWeatherUpdate(searchQuery: String): Result<WeatherResponse>

    suspend fun insertWeatherData(
        remoteResult: Result<WeatherResponse>, searchQuery: String
    ): Result<WeatherResponse>

    suspend fun getWeatherForecast(cityName: String): Flow<Result<List<WeatherForecastEntity>>>
}