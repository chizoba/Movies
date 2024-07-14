package com.remote.ghibli.api.models

import kotlinx.serialization.Serializable

@Serializable
data class PersonApiResponse(
    val id: String? = null,
    val name: String? = null,
)
