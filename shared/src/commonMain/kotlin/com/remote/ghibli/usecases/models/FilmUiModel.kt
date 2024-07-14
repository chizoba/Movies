package com.remote.ghibli.usecases.models

data class FilmUiModel(
    val id: String,
    val title: String,
    val description: String,
    val releaseDate: String,
    val isFavorite: Boolean,
)
