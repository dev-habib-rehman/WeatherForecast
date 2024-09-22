package com.example.weatherforecast.ui.viewHolder

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.data.local.entities.WeatherForecastEntity
import com.example.weatherforecast.databinding.ItemForecastBinding

class ForecastViewHolder(private val binding: ItemForecastBinding) :
    RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(item: WeatherForecastEntity, position: Int) {
        binding.apply {
            tvDay.text = (position + 1).toString()
            tvDate.text = item.date
            tvTempHigh.text = "${item.maxTemp} °C"
            tvTempLow.text = "${item.minTemp} °C"
            tvCondition.text = item.condition
        }
    }
}