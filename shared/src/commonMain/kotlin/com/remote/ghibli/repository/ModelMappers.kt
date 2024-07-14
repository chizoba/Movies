package com.remote.ghibli.repository

import com.remote.ghibli.repository.models.Film
import com.remote.ghibli.repository.models.Filter
import com.remote.ghibli.repository.models.FilterType
import com.remote.ghibli.repository.models.Person
import com.remote.ghibli.database.FavoriteFilms as FavoriteFilmDbModel
import com.remote.ghibli.database.FavoritePeople as FavoritePersonDbModel
import com.remote.ghibli.database.SelectedFilters as SelectedFilterDbModel

fun FavoriteFilmDbModel.toFilm() = Film(
    id = this.id,
    title = this.title,
    description = this.description,
    director = this.director,
    producer = this.producer,
    releaseDate = this.releaseDate,
    runningTime = this.runningTime,
    rtScore = this.rtScore,
    peopleUrls = this.peopleUrls,
)

fun FavoritePersonDbModel.toPerson() = Person(
    id = this.id,
    name = this.name,
)

fun SelectedFilterDbModel.toFilter() = Filter(
    id = this.id,
    name = this.name,
    type = FilterType.valueOf(this.type),
)
