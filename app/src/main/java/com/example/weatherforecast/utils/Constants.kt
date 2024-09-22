package com.example.weatherforecast.utils

object Constants {

    object ApiEndpoints {
        const val WEATHER_FORECAST = "v1/forecast.json"
    }

    object ApiParams {
        const val SEARCH_QUERY = "q"
        const val NO_OF_DAYS = "days"
    }

    object Database {
        const val DB_NAME = "weather_database"
    }

    object Entities {
        const val LOCATION = "location"
        const val WEATHER = "currentWeather"
        const val WEATHER_FORECAST = "weatherForecast"
    }
}