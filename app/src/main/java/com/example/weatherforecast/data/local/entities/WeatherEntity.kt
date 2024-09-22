package com.example.weatherforecast.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.weatherforecast.data.remote.model.Condition
import com.example.weatherforecast.data.remote.model.CurrentWeather
import com.example.weatherforecast.utils.Constants

@Entity(
    tableName = Constants.Entities.WEATHER, foreignKeys = [ForeignKey(
        entity = LocationEntity::class,
        parentColumns = ["id"],
        childColumns = ["locationId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["locationId"])]
)
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var locationId: Int? = 0, // Foreign key to Location
    val lastUpdatedEpoch: Long? = 0L,
    val temperature: Double? = 0.0,
    val humidity: Int? = 0,
    val windSpeed: Double? = 0.0,
    val conditionText: String? = "",
    val conditionIcon: String? = "",
    val lastUpdated: String? = "" // Timestamp for last update
)

fun WeatherEntity.toDomainModel() = CurrentWeather(
    lastUpdatedEpoch = lastUpdatedEpoch,
    tempC = temperature,
    humidity = humidity,
    windMph = windSpeed,
    condition = Condition(text = conditionText, icon = conditionIcon),
    lastUpdated = lastUpdated
)