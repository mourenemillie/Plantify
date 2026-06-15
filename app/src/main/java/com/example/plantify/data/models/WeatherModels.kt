package com.example.plantify.data.models

import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val data: List<WeatherData>? = null
)

@Serializable
data class WeatherData(
    val cuaca: List<List<WeatherForecast>>? = null
)

@Serializable
data class WeatherForecast(
    val datetime: String,
    val t: String? = null,
    val hu: String? = null,
    val weather_desc: String? = null,
    val ws: String? = null
)
