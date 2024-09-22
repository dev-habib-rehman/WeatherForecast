package com.example.weatherforecast.di

import com.example.weatherforecast.data.local.dao.WeatherDao
import com.example.weatherforecast.data.remote.service.WeatherApiService
import com.example.weatherforecast.repositories.WeatherForecastRepository
import com.example.weatherforecast.repositories.WeatherForecastRepositoryImpl
import com.example.weatherforecast.ui.viewmodel.WeatherForecastVM
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {

    @Singleton
    @Provides
    fun provideExchangeRateRepository(
        apiService: WeatherApiService,
        exchangeRatesDao: WeatherDao
    ): WeatherForecastRepository =
        WeatherForecastRepositoryImpl(apiService, exchangeRatesDao)


    @Singleton
    @Provides
    fun provideMainViewModel(repository: WeatherForecastRepository): WeatherForecastVM =
        WeatherForecastVM(repository)

}