package com.example.plantify.data.remote

import com.example.plantify.BuildConfig
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiService {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    // Return String? (Null on unknown error, but here we will return "ERROR: msg" if it fails)
    suspend fun generateCareSchedule(plantName: String, condition: String, weatherContext: String? = null): String? = withContext(Dispatchers.IO) {
        val weatherInfo = if (weatherContext != null) "The current weather in the user's location is: $weatherContext." else ""
        val prompt = """
            $weatherInfo
            Create a detailed care schedule for a plant named '$plantName' in this condition: '$condition'.
            Consider the current weather if provided to adjust watering frequency or special care.
            
            Provide the output in JSON format with the following structure:
            {
              "recommendation_text": "A brief summary of why these tasks were chosen based on the plant and weather",
              "tasks": [
                {
                  "type": "Watering" or "Fertilizing" or "Pruning",
                  "time": "HH:mm",
                  "description": "Short instruction"
                }
              ]
            }
            Valid types are: "Watering", "Fertilizing", "Pruning".
            Only return the raw JSON string. No markdown tags.
        """.trimIndent()

        try {
            val response = generativeModel.generateContent(prompt)
            var cleanText = response.text ?: return@withContext null
            
            // Handle markdown code blocks if the AI includes them
            if (cleanText.contains("```json")) {
                cleanText = cleanText.substringAfter("```json").substringBefore("```").trim()
            } else if (cleanText.contains("```")) {
                cleanText = cleanText.substringAfter("```").substringBeforeLast("```").trim()
            }
            
            return@withContext cleanText
        } catch (e: Exception) {
            Log.e("AiService", "Error generating care schedule", e)
            return@withContext "ERROR: ${e.javaClass.simpleName} - ${e.message}"
        }
    }
}
