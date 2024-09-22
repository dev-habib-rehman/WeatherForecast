package com.example.weatherforecast.data.remote.model

import com.example.weatherforecast.data.local.entities.LocationEntity
import com.example.weatherforecast.data.local.entities.WeatherEntity
import com.example.weatherforecast.data.local.entities.WeatherForecastEntity
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WeatherResponse(
    @SerializedName("location") val location: Location? = null,
    @SerializedName("current") val current: CurrentWeather? = null,
    @SerializedName("forecast") val forecast: Forecast? = null
) : Serializable

data class Location(
    @SerializedName("name") val name: String? = null,
    @SerializedName("region") val region: String? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("lat") val lat: Double? = null,
    @SerializedName("lon") val lon: Double? = null,
    @SerializedName("tz_id") val tzId: String? = null,
    @SerializedName("localtime_epoch") val localtimeEpoch: Long? = null,
    @SerializedName("localtime") val localtime: String? = null
) : Serializable


fun Location.toDbModel() = LocationEntity(
    name = name ?: "",
    region = region ?: "",
    country = country ?: "",
    lat = lat ?: 0.0,
    lon = lon ?: 0.0
)

data class CurrentWeather(
    @SerializedName("last_updated_epoch") val lastUpdatedEpoch: Long? = null,
    @SerializedName("last_updated") val lastUpdated: String? = null,
    @SerializedName("temp_c") val tempC: Double? = null,
    @SerializedName("temp_f") val tempF: Double? = null,
    @SerializedName("is_day") val isDay: Int? = null,
    @SerializedName("condition") val condition: Condition? = null,
    @SerializedName("wind_mph") val windMph: Double? = null,
    @SerializedName("wind_kph") val windKph: Double? = null,
    @SerializedName("wind_degree") val windDegree: Int? = null,
    @SerializedName("wind_dir") val windDir: String? = null,
    @SerializedName("pressure_mb") val pressureMb: Double? = null,
    @SerializedName("pressure_in") val pressureIn: Double? = null,
    @SerializedName("precip_mm") val precipMm: Double? = null,
    @SerializedName("precip_in") val precipIn: Double? = null,
    @SerializedName("humidity") val humidity: Int? = null,
    @SerializedName("cloud") val cloud: Int? = null,
    @SerializedName("feelslike_c") val feelslikeC: Double? = null,
    @SerializedName("feelslike_f") val feelslikeF: Double? = null,
    @SerializedName("windchill_c") val windchillC: Double? = null,
    @SerializedName("windchill_f") val windchillF: Double? = null,
    @SerializedName("heatindex_c") val heatindexC: Double? = null,
    @SerializedName("heatindex_f") val heatindexF: Double? = null,
    @SerializedName("dewpoint_c") val dewpointC: Double? = null,
    @SerializedName("dewpoint_f") val dewpointF: Double? = null,
    @SerializedName("vis_km") val visKm: Double? = null,
    @SerializedName("vis_miles") val visMiles: Double? = null,
    @SerializedName("uv") val uv: Double? = null,
    @SerializedName("gust_mph") val gustMph: Double? = null,
    @SerializedName("gust_kph") val gustKph: Double? = null
) : Serializable


fun CurrentWeather.toDbModel(locationId: Int): WeatherEntity {
    return WeatherEntity(
        locationId = locationId, // Use the provided location ID
        lastUpdatedEpoch = this.lastUpdatedEpoch,
        lastUpdated = this.lastUpdated,
        temperature = this.tempC, // You can choose to store tempC or tempF
        humidity = this.humidity,
        windSpeed = this.windMph, // Choose windMph or windKph as needed
        conditionText = this.condition?.text,
        conditionIcon = this.condition?.icon
    )
}


data class Forecast(
    @SerializedName("forecastday") val forecastDay: List<ForecastDay>? = null
) : Serializable

fun Forecast.toEntities(weatherId: Int): List<WeatherForecastEntity> {
    return forecastDay?.map { forecastDay ->
        WeatherForecastEntity(
            weatherId = weatherId, // Use the weatherId passed to the function
            date = forecastDay.date ?: "",
            maxTemp = forecastDay.day?.maxTempC ?: 0.0,
            minTemp = forecastDay.day?.minTempC ?: 0.0,
            condition = forecastDay.day?.condition?.text ?: ""
        )
    } ?: emptyList()
}

data class ForecastDay(
    @SerializedName("date") val date: String? = null,
    @SerializedName("date_epoch") val dateEpoch: Long? = null,
    @SerializedName("day") val day: Day? = null,
    @SerializedName("astro") val astro: Astro? = null,
    @SerializedName("hour") val hour: List<Hour>? = null
) : Serializable

data class Day(
    @SerializedName("maxtemp_c") val maxTempC: Double? = null,
    @SerializedName("maxtemp_f") val maxTempF: Double? = null,
    @SerializedName("mintemp_c") val minTempC: Double? = null,
    @SerializedName("mintemp_f") val minTempF: Double? = null,
    @SerializedName("avgtemp_c") val avgTempC: Double? = null,
    @SerializedName("avgtemp_f") val avgTempF: Double? = null,
    @SerializedName("maxwind_mph") val maxWindMph: Double? = null,
    @SerializedName("maxwind_kph") val maxWindKph: Double? = null,
    @SerializedName("totalprecip_mm") val totalPrecipMm: Double? = null,
    @SerializedName("totalprecip_in") val totalPrecipIn: Double? = null,
    @SerializedName("totalsnow_cm") val totalSnowCm: Double? = null,
    @SerializedName("avgvis_km") val avgVisKm: Double? = null,
    @SerializedName("avgvis_miles") val avgVisMiles: Double? = null,
    @SerializedName("avghumidity") val avgHumidity: Int? = null,
    @SerializedName("daily_will_it_rain") val dailyWillItRain: Int? = null,
    @SerializedName("daily_chance_of_rain") val dailyChanceOfRain: Int? = null,
    @SerializedName("daily_will_it_snow") val dailyWillItSnow: Int? = null,
    @SerializedName("daily_chance_of_snow") val dailyChanceOfSnow: Int? = null,
    @SerializedName("condition") val condition: Condition? = null,
    @SerializedName("uv") val uv: Double? = null
) : Serializable

data class Astro(
    @SerializedName("sunrise") val sunrise: String? = null,
    @SerializedName("sunset") val sunset: String? = null,
    @SerializedName("moonrise") val moonrise: String? = null,
    @SerializedName("moonset") val moonset: String? = null,
    @SerializedName("moon_phase") val moonPhase: String? = null,
    @SerializedName("moon_illumination") val moonIllumination: Int? = null,
    @SerializedName("is_moon_up") val isMoonUp: Int? = null,
    @SerializedName("is_sun_up") val isSunUp: Int? = null
) : Serializable

data class Hour(
    @SerializedName("time_epoch") val timeEpoch: Long? = null,
    @SerializedName("time") val time: String? = null,
    @SerializedName("temp_c") val tempC: Double? = null,
    @SerializedName("temp_f") val tempF: Double? = null,
    @SerializedName("is_day") val isDay: Int? = null,
    @SerializedName("condition") val condition: Condition? = null,
    @SerializedName("wind_mph") val windMph: Double? = null,
    @SerializedName("wind_kph") val windKph: Double? = null,
    @SerializedName("wind_degree") val windDegree: Int? = null,
    @SerializedName("wind_dir") val windDir: String? = null,
    @SerializedName("pressure_mb") val pressureMb: Double? = null,
    @SerializedName("pressure_in") val pressureIn: Double? = null,
    @SerializedName("precip_mm") val precipMm: Double? = null,
    @SerializedName("precip_in") val precipIn: Double? = null,
    @SerializedName("snow_cm") val snowCm: Double? = null,
    @SerializedName("humidity") val humidity: Int? = null,
    @SerializedName("cloud") val cloud: Int? = null,
    @SerializedName("feelslike_c") val feelslikeC: Double? = null,
    @SerializedName("feelslike_f") val feelslikeF: Double? = null,
    @SerializedName("windchill_c") val windchillC: Double? = null,
    @SerializedName("windchill_f") val windchillF: Double? = null,
    @SerializedName("heatindex_c") val heatindexC: Double? = null,
    @SerializedName("heatindex_f") val heatindexF: Double? = null,
    @SerializedName("dewpoint_c") val dewpointC: Double? = null,
    @SerializedName("dewpoint_f") val dewpointF: Double? = null,
    @SerializedName("will_it_rain") val willItRain: Int? = null,
    @SerializedName("chance_of_rain") val chanceOfRain: Int? = null,
    @SerializedName("will_it_snow") val willItSnow: Int? = null,
    @SerializedName("chance_of_snow") val chanceOfSnow: Int? = null,
    @SerializedName("vis_km") val visKm: Double? = null,
    @SerializedName("vis_miles") val visMiles: Double? = null,
    @SerializedName("gust_mph") val gustMph: Double? = null,
    @SerializedName("gust_kph") val gustKph: Double? = null,
    @SerializedName("uv") val uv: Double? = null
) : Serializable

data class Condition(
    @SerializedName("text") val text: String? = null,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("code") val code: Int? = null
) : Serializable

