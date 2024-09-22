package com.example.weatherforecast.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.data.apiHelper.Result
import com.example.weatherforecast.data.local.entities.WeatherForecastEntity
import com.example.weatherforecast.data.remote.model.WeatherResponse
import com.example.weatherforecast.repositories.WeatherForecastRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherForecastVM @Inject constructor(
    private val weatherForecastRepository: WeatherForecastRepository
) : ViewModel() {

    private val _weatherState = MutableStateFlow<Result<WeatherResponse>>(Result.Loading())
    val weatherState: StateFlow<Result<WeatherResponse>> = _weatherState.asStateFlow()

    private val _weatherForecastState = MutableStateFlow<Result<List<WeatherForecastEntity>>>(Result.Loading())
    val weatherForecastState: StateFlow<Result<List<WeatherForecastEntity>>> = _weatherForecastState.asStateFlow()

    fun getWeatherUpdate(searchQuery: String = "Lahore") {
        _weatherState.value = Result.Loading()
        viewModelScope.launch {
            weatherForecastRepository.getWeatherUpdate(searchQuery).collect { result ->
                _weatherState.value = result
            }
        }
    }

    fun getWeatherForecast(cityName:String){
        viewModelScope.launch {
            weatherForecastRepository.getWeatherForecast(cityName).collect{
                _weatherForecastState.value = it
            }
        }
    }
}