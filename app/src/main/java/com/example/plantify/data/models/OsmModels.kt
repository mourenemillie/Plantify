package com.example.plantify.data.models

import kotlinx.serialization.Serializable

@Serializable
data class OsmResponse(
    val version: Double? = null,
    val generator: String? = null,
    val copyright: String? = null,
    val attribution: String? = null,
    val license: String? = null,
    val elements: List<OsmElement>
)

@Serializable
data class OsmElement(
    val type: String,
    val id: Long,
    val lat: Double? = null,
    val lon: Double? = null,
    val nodes: List<Long>? = null,
    val members: List<OsmMember>? = null,
    val tags: Map<String, String>? = null,
    val user: String? = null,
    val uid: Long? = null,
    val timestamp: String? = null,
    val visible: Boolean? = null,
    val version: Int? = null,
    val changeset: Long? = null
)

@Serializable
data class OsmMember(
    val type: String,
    val ref: Long,
    val role: String
)
