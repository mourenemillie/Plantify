package com.example.plantify.data.remote

import com.example.plantify.data.models.OsmElement
import com.example.plantify.data.models.OsmResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.URL

class OsmService {
    private val json = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }
    private val baseUrl = "https://api.openstreetmap.org/api/0.6"

    /**
     * Fetch a node by its ID.
     */
    suspend fun getNode(id: Long): OsmElement? = getElement("node", id)

    /**
     * Fetch a way by its ID.
     */
    suspend fun getWay(id: Long): OsmElement? = getElement("way", id)

    /**
     * Fetch a relation by its ID.
     */
    suspend fun getRelation(id: Long): OsmElement? = getElement("relation", id)

    private suspend fun getElement(type: String, id: Long): OsmElement? = withContext(Dispatchers.IO) {
        try {
            val response = URL("$baseUrl/$type/$id.json").readText()
            val osmResponse = json.decodeFromString<OsmResponse>(response)
            osmResponse.elements.firstOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Fetch multiple elements of the same type by their IDs.
     */
    suspend fun getElements(type: String, ids: List<Long>): List<OsmElement> = withContext(Dispatchers.IO) {
        if (ids.isEmpty()) return@withContext emptyList()
        try {
            val idsString = ids.joinToString(",")
            val response = URL("$baseUrl/${type}s?${type}s=$idsString.json").readText()
            val osmResponse = json.decodeFromString<OsmResponse>(response)
            osmResponse.elements
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Fetch history of an element.
     */
    suspend fun getHistory(type: String, id: Long): List<OsmElement> = withContext(Dispatchers.IO) {
        try {
            val response = URL("$baseUrl/$type/$id/history.json").readText()
            val osmResponse = json.decodeFromString<OsmResponse>(response)
            osmResponse.elements
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Fetch relations an element is member of.
     */
    suspend fun getRelationsForElement(type: String, id: Long): List<OsmElement> = withContext(Dispatchers.IO) {
        try {
            val response = URL("$baseUrl/$type/$id/relations.json").readText()
            val osmResponse = json.decodeFromString<OsmResponse>(response)
            osmResponse.elements
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Fetch map data (nodes, ways, relations) for a bounding box.
     * Note: This returns XML as JSON is not supported for the 'map' call in API v0.6.
     */
    suspend fun getMapData(left: Double, bottom: Double, right: Double, top: Double): String? = withContext(Dispatchers.IO) {
        try {
            URL("$baseUrl/map?bbox=$left,$bottom,$right,$top").readText()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
