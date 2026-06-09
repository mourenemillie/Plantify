package com.example.plantify.data.remote.model

import com.google.gson.annotations.SerializedName

data class NominatimSearchResponse(
    @SerializedName("place_id")
    val placeId: Long,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("lon")
    val lon: String,
    @SerializedName("display_name")
    val displayName: String
)