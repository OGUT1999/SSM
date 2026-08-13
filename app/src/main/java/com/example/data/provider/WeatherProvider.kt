package com.example.data.provider

import com.example.data.local.WeatherEntity

interface WeatherProvider {
    val providerName: String
    suspend fun getWeatherInfo(locationName: String): WeatherEntity
}

class LocalCacheWeatherProvider(
    private val costControlEngine: CostControlEngine
) : WeatherProvider {

    override val providerName: String = "Local Weather Cache ($0)"

    override suspend fun getWeatherInfo(locationName: String): WeatherEntity {
        costControlEngine.updateWeatherProvider(providerName)
        return WeatherEntity(
            locationName = locationName,
            temperatureC = 30,
            condition = "Cerah Berawan",
            rainProbabilityPercent = 35,
            humidityPercent = 75,
            windSpeedKmh = 14,
            advisoryText = "Pemberitahuan Cuaca Lokal ($0 Cache): Kondisi sangat baik untuk pemupukan pagi & pemberian pakan. Sore berpotensi hujan ringan."
        )
    }
}
