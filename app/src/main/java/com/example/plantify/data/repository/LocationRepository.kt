package com.example.plantify.data.repository

import com.example.plantify.data.remote.BmkgApiService
import com.example.plantify.data.remote.LocationApiService
import com.example.plantify.data.remote.NominatimApiService
import com.example.plantify.data.remote.model.NominatimSearchResponse // Tambahan import baru
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationRepository(
    private val apiService: LocationApiService,
    private val nominatimService: NominatimApiService,
    private val bmkgService: BmkgApiService
) {
    suspend fun getProvinces() = apiService.getProvinces().data
    suspend fun getRegencies(provinceId: String) = apiService.getRegencies(provinceId).data
    suspend fun getDistricts(regencyId: String) = apiService.getDistricts(regencyId).data
    suspend fun getVillages(districtId: String) = apiService.getVillages(districtId).data

    suspend fun reverseGeocode(lat: Double, lon: Double) = withContext(Dispatchers.IO) {
        nominatimService.reverseGeocode(lat = lat, lon = lon).address
    }

    suspend fun getCoordinatesFromAddress(
        kelurahan: String,
        kecamatan: String,
        kabupaten: String,
        provinsi: String
    ): NominatimSearchResponse? = withContext(Dispatchers.IO) {
        return@withContext try {
            val fullAddress = "$kelurahan, $kecamatan, $kabupaten, $provinsi, Indonesia"

            val results = nominatimService.searchGeocode(address = fullAddress)

            if (results.isNotEmpty()) {
                results[0]
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getBmkgWeather(adm4: String) = withContext(Dispatchers.IO) {
        try {
            val response = bmkgService.getBmkgWeather(adm4)
            val cuacaList = response.data?.firstOrNull()?.cuaca?.firstOrNull()
            if (!cuacaList.isNullOrEmpty()) {
                val current = cuacaList[0]
                "${current.t}°C — ${current.weather_desc}"
            } else {
                "Unknown"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Normal/Unknown"
        }
    }
}