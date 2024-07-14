package com.remote.ghibli.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FilmApiResponse(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val director: String? = null,
    val producer: String? = null,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("running_time")
    val runningTime: String? = null,
    @SerialName("rt_score")
    val rtScore: String? = null,
    @SerialName("people")
    val peopleUrls: List<String>? = null,
)