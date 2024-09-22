package com.example.weatherforecast.data.remote.service

import com.example.weatherforecast.BuildConfig
import com.example.weatherforecast.data.remote.model.WeatherResponse
import com.example.weatherforecast.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET(Constants.ApiEndpoints.WEATHER_FORECAST)
    suspend fun getWeatherForecast(
        @Query(Constants.ApiParams.SEARCH_QUERY) cityName: String,
        @Query(Constants.ApiParams.NO_OF_DAYS) days: Int = 5,
        @Query("key") key: String = BuildConfig.API_KEY
    ): Response<WeatherResponse>
}