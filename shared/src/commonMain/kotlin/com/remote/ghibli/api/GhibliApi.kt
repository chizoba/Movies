package com.remote.ghibli.api

import com.remote.ghibli.api.models.FilmApiResponse
import com.remote.ghibli.api.models.PersonApiResponse
import com.remote.ghibli.api.models.SpecieApiResponse

interface GhibliApi {
    suspend fun getFilms(): Result<List<FilmApiResponse>>
    suspend fun getFilm(slug: String): Result<FilmApiResponse>
    suspend fun getSpecies(): Result<List<SpecieApiResponse>>
    suspend fun getSpecie(slug: String): Result<SpecieApiResponse>
    suspend fun getPeople(): Result<List<PersonApiResponse>>
    suspend fun getPerson(slug: String): Result<PersonApiResponse>
}
