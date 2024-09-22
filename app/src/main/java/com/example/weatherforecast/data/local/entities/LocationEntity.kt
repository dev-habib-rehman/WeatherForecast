package com.example.weatherforecast.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherforecast.data.remote.model.Location
import com.example.weatherforecast.utils.Constants

@Entity(tableName = Constants.Entities.LOCATION)
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null, // Unique ID for the location
    val name: String, val region: String, val country: String, val lat: Double, val lon: Double
)

fun LocationEntity.toDomainModel() = Location(
    name = name, region = region, country = country, lat = lat, lon = lon
)
