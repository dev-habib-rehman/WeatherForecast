package com.example.weatherforecast.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.weatherforecast.utils.Constants

@Entity(
    tableName = Constants.Entities.WEATHER_FORECAST, foreignKeys = [ForeignKey(
        entity = WeatherEntity::class,
        parentColumns = ["id"],
        childColumns = ["weatherId"],
        onDelete = ForeignKey.CASCADE
    )], indices = [Index(value = ["weatherId"])]
)
data class WeatherForecastEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, var weatherId: Int, // Foreign key to Weather
    val date: String, val maxTemp: Double, val minTemp: Double, val condition: String
)

