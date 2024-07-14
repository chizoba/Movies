package com.remote.ghibli.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpecieApiResponse(
    val id: String? = null,
    val name: String? = null,
    @SerialName("films")
    val filmUrls: List<String>? = null,
)
