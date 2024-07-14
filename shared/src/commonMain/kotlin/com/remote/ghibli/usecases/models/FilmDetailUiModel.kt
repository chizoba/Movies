package com.remote.ghibli.usecases.models

data class FilmDetailUiModel(
    val id: String,
    val title: String,
    val description: String,
    val director: String,
    val producer: String,
    val releaseDate: String,
    val runningTime: String,
    val rtScore: String,
    val people: List<String>,
    val isFavorite: Boolean,
)
