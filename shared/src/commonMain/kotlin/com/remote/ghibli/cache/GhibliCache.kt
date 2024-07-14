package com.remote.ghibli.cache

import com.remote.ghibli.repository.models.Film
import com.remote.ghibli.repository.models.Person
import com.remote.ghibli.repository.models.Specie

interface GhibliCache {
    suspend fun getFilms(): Result<List<Film>>
    suspend fun getFilm(slug: String): Result<Film>
    suspend fun getSpecies(): Result<List<Specie>>
    suspend fun getSpecie(slug: String): Result<Specie>
    suspend fun getPeople(): Result<List<Person>>
    suspend fun getPerson(slug: String): Result<Person>
    suspend fun setFilms(films: List<Film>): Result<Boolean>
    suspend fun setSpecies(species: List<Specie>): Result<Boolean>
    suspend fun setPeople(people: List<Person>): Result<Boolean>
}
