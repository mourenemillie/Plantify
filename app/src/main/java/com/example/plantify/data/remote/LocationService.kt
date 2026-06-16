package com.example.plantify.data.remote

import com.example.plantify.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.URL

class LocationService {
    private val json = Json { ignoreUnknownKeys = true }
    private val baseUrl = "https://wilayah.id/api"

    suspend fun getProvinces(): List<Province> = withContext(Dispatchers.IO) {
        try {
            val response = URL("$baseUrl/provinces.json").readText()
            json.decodeFromString<LocationData<Province>>(response).data
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRegencies(provinceCode: String): List<Regency> = withContext(Dispatchers.IO) {
        try {
            val response = URL("$baseUrl/regencies/$provinceCode.json").readText()
            json.decodeFromString<LocationData<Regency>>(response).data
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getDistricts(regencyCode: String): List<District> = withContext(Dispatchers.IO) {
        try {
            val response = URL("$baseUrl/districts/$regencyCode.json").readText()
            json.decodeFromString<LocationData<District>>(response).data
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getVillages(districtCode: String): List<Village> = withContext(Dispatchers.IO) {
        try {
            val response = URL("$baseUrl/villages/$districtCode.json").readText()
            json.decodeFromString<LocationData<Village>>(response).data
        } catch (e: Exception) {
            emptyList()
        }
    }
}
