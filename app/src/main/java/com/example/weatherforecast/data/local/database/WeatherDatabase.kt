package com.example.weatherforecast.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherforecast.data.local.dao.WeatherDao
import com.example.weatherforecast.data.local.entities.LocationEntity
import com.example.weatherforecast.data.local.entities.WeatherEntity
import com.example.weatherforecast.data.local.entities.WeatherForecastEntity

@Database(
    entities = [LocationEntity::class, WeatherEntity::class, WeatherForecastEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherForecastDao(): WeatherDao
}