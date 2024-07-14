package com.remote.ghibli.api

import com.remote.ghibli.api.models.FilmApiResponse
import com.remote.ghibli.api.models.PersonApiResponse
import com.remote.ghibli.api.models.SpecieApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

// Check out API documentation at https://ghibliapi.vercel.app/

class GhibliApiDefault(
    private val client: HttpClient,
    private val context: CoroutineContext = Dispatchers.IO
) : GhibliApi {

    override suspend fun getFilms(): Result<List<FilmApiResponse>> =
        get("https://ghibliapi.vercel.app/films?limit=250")

    override suspend fun getFilm(slug: String): Result<FilmApiResponse> =
        get("https://ghibliapi.vercel.app/films/$slug")

    override suspend fun getSpecies(): Result<List<SpecieApiResponse>> =
        get("https://ghibliapi.vercel.app/species?limit=250")

    override suspend fun getSpecie(slug: String): Result<SpecieApiResponse> =
        get("https://ghibliapi.vercel.app/species/$slug")

    override suspend fun getPeople(): Result<List<PersonApiResponse>> =
        get("https://ghibliapi.vercel.app/people?limit=250")

    override suspend fun getPerson(slug: String): Result<PersonApiResponse> =
        get("https://ghibliapi.vercel.app/people/$slug")

    private suspend inline fun <reified T> get(path: String): Result<T> =
        withContext(context) {
            runCatching { client.get(path) }
                .map { it.body() }
        }
}