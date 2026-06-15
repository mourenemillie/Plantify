package com.example.plantify.data.remote

import com.example.plantify.data.models.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.URL

class WeatherService {
    private val json = Json { 
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val baseUrl = "https://api.bmkg.go.id/publik/prakiraan-cuaca"

    suspend fun getCurrentWeather(adm4: String): String? = withContext(Dispatchers.IO) {
        try {
            val response = URL("$baseUrl?adm4=$adm4").readText()
            val weatherData = json.decodeFromString<WeatherResponse>(response)
            val latest = weatherData.data?.firstOrNull()?.cuaca?.firstOrNull()?.firstOrNull()
            if (latest != null) {
                "${latest.weather_desc}, Temp: ${latest.t}°C, Humidity: ${latest.hu}%"
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
