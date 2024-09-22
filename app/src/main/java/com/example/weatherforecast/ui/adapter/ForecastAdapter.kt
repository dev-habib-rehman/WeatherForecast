package com.example.weatherforecast.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.weatherforecast.R
import com.example.weatherforecast.data.local.entities.WeatherForecastEntity
import com.example.weatherforecast.databinding.ItemForecastBinding
import com.example.weatherforecast.ui.viewHolder.ForecastViewHolder
import com.example.weatherforecast.utils.adapterUtils.ForecastDiffCallback

class ForecastAdapter : ListAdapter<WeatherForecastEntity, ForecastViewHolder>(
    ForecastDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val binding: ItemForecastBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_forecast, parent, false
        )
        return ForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }
}

