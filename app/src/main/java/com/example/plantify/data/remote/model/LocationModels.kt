package com.example.plantify.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class Wilayah(
    val code: String,
    val name: String
)

@Serializable
data class WilayahResponse(
    val data: List<Wilayah>
)

// Nominatim OpenStreetMap Reverse Geocoding
@Serializable
data class NominatimResponse(
    val address: NominatimAddress? = null
)

@Serializable
data class NominatimAddress(
    val village: String? = null,
    val suburb: String? = null,
    val town: String? = null,
    val city_district: String? = null,
    val city: String? = null,
    val county: String? = null,
    val state: String? = null
)

// BMKG API Response
@Serializable
data class BmkgWeatherResponse(
    val data: List<BmkgLocationData>? = null
)

@Serializable
data class BmkgLocationData(
    val cuaca: List<List<BmkgCuaca>>? = null
)

@Serializable
data class BmkgCuaca(
    val datetime: String,
    val t: Int, // Temperature
    val hu: Int, // Humidity
    val weather_desc: String
)
