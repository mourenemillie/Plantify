package com.example.plantify.data.remote

import com.example.plantify.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiService {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun generateCareSchedule(plantName: String, condition: String): String? = withContext(Dispatchers.IO) {
        val prompt = """
            Create a care schedule for a plant named '$plantName' in this condition: '$condition'.
            Provide the output in JSON format with the following structure:
            {
              "tasks": [
                {
                  "type": "Watering",
                  "time": "08:00",
                  "description": "Short description"
                },
                {
                  "type": "Fertilizing",
                  "time": "09:00",
                  "description": "Short description"
                }
              ]
            }
            Valid types are: "Watering", "Fertilizing", "Pruning".
            Only return the raw JSON string. No markdown tags.
        """.trimIndent()

        try {
            val response = generativeModel.generateContent(prompt)
            return@withContext response.text
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
