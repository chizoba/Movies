package com.remote.ghibli.repository.models

data class Filter(
    val id: String,
    val name: String,
    val type: FilterType,
)

enum class FilterType { Specie, Favorite }

val FavoriteFilter = Filter(id = "favorite_id", name = "Favorite", type = FilterType.Favorite)

