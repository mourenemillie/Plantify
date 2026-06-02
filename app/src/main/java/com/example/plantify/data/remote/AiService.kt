package com.example.plantify.data.remote

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AiService {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "AIzaSyAAaP8h_W3NkT-JDeXPPHotSIxmOY1v5DA"
    )

    suspend fun generateCareSchedule(plantName: String, condition: String): String? = withContext(Dispatchers.IO) {
        val prompt = """
            Create a care schedule for a plant named '$plantName' in this condition: '$condition'.
            Provide the output in JSON format with the following structure:
            {
              "tasks": [
                {
                  "type": "Watering" or "Fertilizing" or "Pruning",
                  "time": "HH:mm",
                  "description": "Short description"
                }
              ]
            }
            Only return the JSON.
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
