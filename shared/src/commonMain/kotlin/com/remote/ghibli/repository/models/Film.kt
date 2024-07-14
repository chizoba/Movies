package com.remote.ghibli.repository.models

data class Film(
    val id: String,
    val title: String,
    val description: String,
    val director: String,
    val producer: String,
    val releaseDate: String,
    val runningTime: String,
    val rtScore: String,
    val peopleUrls: List<String>,
)
