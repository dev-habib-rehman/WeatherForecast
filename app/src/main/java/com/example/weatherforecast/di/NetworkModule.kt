package com.example.weatherforecast.di

import android.content.Context
import androidx.room.Room
import com.example.weatherforecast.BuildConfig
import com.example.weatherforecast.data.local.dao.WeatherDao
import com.example.weatherforecast.data.local.database.WeatherDatabase
import com.example.weatherforecast.data.remote.service.WeatherApiService
import com.example.weatherforecast.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit = Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext application: Context): WeatherDatabase =
        Room.databaseBuilder(
            application, WeatherDatabase::class.java, Constants.Database.DB_NAME
        ).build()


    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): WeatherApiService =
        retrofit.create(WeatherApiService::class.java)


    @Singleton
    @Provides
    fun provideWeatherForecastDao(appDatabase: WeatherDatabase): WeatherDao =
        appDatabase.weatherForecastDao()
}
