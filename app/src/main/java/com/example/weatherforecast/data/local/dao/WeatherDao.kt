package com.example.weatherforecast.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecast.data.local.entities.LocationEntity
import com.example.weatherforecast.data.local.entities.WeatherEntity
import com.example.weatherforecast.data.local.entities.WeatherForecastEntity
import com.example.weatherforecast.utils.Constants

@Dao
interface WeatherDao {
    // Insert or update location
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)

    // Fetch location by city name
    @Query("SELECT * FROM ${Constants.Entities.LOCATION} WHERE name = :cityName LIMIT 1")
    suspend fun getLocationByCityName(cityName: String): LocationEntity?

    // Insert or update weather
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    // Fetch weather using city name by joining with Location table
    @Query(
        """
        SELECT ${Constants.Entities.WEATHER}.* FROM ${Constants.Entities.WEATHER}
        INNER JOIN location ON ${Constants.Entities.WEATHER}.locationId = location.id
        WHERE location.name = :cityName
        LIMIT 1
    """
    )
    suspend fun getWeatherByCityName(cityName: String): WeatherEntity?

    // Insert forecast
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: WeatherForecastEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllForecasts(forecasts: List<WeatherForecastEntity>)

    // Fetch forecasts using city name by joining with Location and Weather
    @Query("""
        SELECT ${Constants.Entities.WEATHER_FORECAST}.* FROM ${Constants.Entities.WEATHER_FORECAST}
        INNER JOIN ${Constants.Entities.WEATHER} ON ${Constants.Entities.WEATHER_FORECAST}.weatherId = ${Constants.Entities.WEATHER}.id
        INNER JOIN location ON ${Constants.Entities.WEATHER}.locationId = location.id
        WHERE location.name = :cityName
    """)
    suspend fun getForecastByCityName(cityName: String): List<WeatherForecastEntity>

}
