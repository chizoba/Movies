package com.remote.ghibli.api

import com.remote.ghibli.api.models.FilmApiResponse
import com.remote.ghibli.api.models.PersonApiResponse
import com.remote.ghibli.api.models.SpecieApiResponse

class GhibliApiMock(
    var filmsResponse: () -> Result<List<FilmApiResponse>> = { throw NotImplementedError() },
    var filmResponse: () -> Result<FilmApiResponse> = { throw NotImplementedError() },
    var speciesResponse: () -> Result<List<SpecieApiResponse>> = { throw NotImplementedError() },
    var specieResponse: () -> Result<SpecieApiResponse> = { throw NotImplementedError() },
    var peopleResponse: () -> Result<List<PersonApiResponse>> = { throw NotImplementedError() },
    var personResponse: () -> Result<PersonApiResponse> = { throw NotImplementedError() },
) : GhibliApi {
    override suspend fun getFilms(): Result<List<FilmApiResponse>> = filmsResponse()

    override suspend fun getFilm(slug: String): Result<FilmApiResponse> = filmResponse()

    override suspend fun getSpecies(): Result<List<SpecieApiResponse>> = speciesResponse()

    override suspend fun getSpecie(slug: String): Result<SpecieApiResponse> = specieResponse()

    override suspend fun getPeople(): Result<List<PersonApiResponse>> = peopleResponse()

    override suspend fun getPerson(slug: String): Result<PersonApiResponse> = personResponse()
}
