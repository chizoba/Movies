package com.remote.ghibli.usecases

import com.remote.ghibli.api.GhibliApiMock
import com.remote.ghibli.api.models.FilmApiResponse
import com.remote.ghibli.cache.GhibliCacheDefault
import com.remote.ghibli.database.GhibliDatabaseDefault
import com.remote.ghibli.database.getDatabaseDriverFactory
import com.remote.ghibli.repository.GhibliRepositoryDefault
import com.remote.ghibli.repository.models.Film
import com.remote.ghibli.usecases.models.Error
import com.remote.ghibli.usecases.models.ErrorTag
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetFilmDetailUseCaseTest {
    private val api = GhibliApiMock()
    private val database = GhibliDatabaseDefault(getDatabaseDriverFactory())
    private val testDispatcher = StandardTestDispatcher()
    private val cache = GhibliCacheDefault(testDispatcher)
    private val repository = GhibliRepositoryDefault(api, database, cache)

    private val getPeopleUseCase = GetPeopleUseCase(repository, testDispatcher)
    private val getFilmDetailUseCase =
        GetFilmDetailUseCase(repository, testDispatcher, getPeopleUseCase)

    @Test
    fun `getFilmDetail should return error when favorites and api returns error`() =
        runTest(testDispatcher) {
            val error = Error(ErrorTag.Api)
            api.filmResponse = { Result.failure(error) }

            assertEquals(
                Result.failure(error),
                getFilmDetailUseCase.invoke("")
            )
        }

    @Test
    fun `getFilmDetail should return expected film details from favorites`() =
        runTest(testDispatcher) {
            val film = Film("id", "-", "-", "-", "-", "-", "-", "-", listOf())
            repository.addFavoriteFilm(film, listOf())

            val result = getFilmDetailUseCase.invoke("id")
            assertEquals("id", result.getOrThrow().id)
        }

    @Test
    fun `getFilmDetail should return expected film details from api`() =
        runTest(testDispatcher) {
            api.filmResponse = {
                Result.success(FilmApiResponse(id = "id"))
            }

            val result = getFilmDetailUseCase.invoke("")
            assertEquals("id", result.getOrThrow().id)
        }
}
