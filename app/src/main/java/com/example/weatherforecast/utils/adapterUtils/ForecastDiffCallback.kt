package com.example.weatherforecast.utils.adapterUtils

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherforecast.data.local.entities.WeatherForecastEntity

class ForecastDiffCallback : DiffUtil.ItemCallback<WeatherForecastEntity>() {
    override fun areItemsTheSame(
        oldItem: WeatherForecastEntity, newItem: WeatherForecastEntity
    ): Boolean {
        // Assuming URI paths are unique
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: WeatherForecastEntity, newItem: WeatherForecastEntity
    ): Boolean {
        return oldItem.id == newItem.id
    }
}