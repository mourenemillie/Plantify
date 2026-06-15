package com.example.plantify.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Province(val code: String, val name: String)

@Serializable
data class Regency(val code: String, val name: String)

@Serializable
data class District(val code: String, val name: String)

@Serializable
data class Village(val code: String, val name: String)

@Serializable
data class LocationData<T>(val data: List<T>)
