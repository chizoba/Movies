package com.remote.ghibli.usecases

import com.remote.ghibli.api.GhibliApiMock
import com.remote.ghibli.api.models.FilmApiResponse
import com.remote.ghibli.api.models.PersonApiResponse
import com.remote.ghibli.cache.GhibliCacheDefault
import com.remote.ghibli.database.GhibliDatabaseDefault
import com.remote.ghibli.database.getDatabaseDriverFactory
import com.remote.ghibli.repository.GhibliRepositoryDefault
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class ToggleFavoriteUseCaseTest {
    private val api = GhibliApiMock()
    private val database = GhibliDatabaseDefault(getDatabaseDriverFactory())
    private val testDispatcher = StandardTestDispatcher()
    private val cache = GhibliCacheDefault(testDispatcher)
    private val repository = GhibliRepositoryDefault(api, database, cache)

    private val getPeopleUseCase = GetPeopleUseCase(repository, testDispatcher)
    private val toggleFavoriteUseCase =
        ToggleFavoriteUseCase(repository, testDispatcher, getPeopleUseCase)

    @Test
    fun `toggleFavorite should add a film as favorite`() = runTest(testDispatcher) {
        api.filmResponse = {
            Result.success(
                FilmApiResponse(
                    id = "id",
                    peopleUrls = listOf("https://ghibliapi.vercel.app/people/")
                )
            )
        }

        api.peopleResponse = {
            Result.success(listOf(PersonApiResponse(id = "person_id", name = "name")))
        }

        val result = toggleFavoriteUseCase("id", true)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `toggleFavorite should fail when film is not found in data layer`() =
        runTest(testDispatcher) {
            api.filmResponse = {
                Result.failure(Exception())
            }

            val result = toggleFavoriteUseCase("id", true)

            assertTrue(result.isFailure)
        }

    @Test
    fun `toggleFavorite should fail when film's people are not found in data layer`() =
        runTest(testDispatcher) {
            api.filmResponse = {
                Result.success(
                    FilmApiResponse(
                        id = "id",
                        peopleUrls = listOf("https://ghibliapi.vercel.app/people/")
                    )
                )
            }

            api.peopleResponse = {
                Result.failure(Exception())
            }

            val result = toggleFavoriteUseCase("id", true)

            assertTrue(result.isFailure)
        }

    @Test
    fun `toggleFavorite should remove a film as favorite`() = runTest(testDispatcher) {
        api.filmResponse = {
            Result.success(
                FilmApiResponse(
                    id = "id",
                    peopleUrls = listOf("https://ghibliapi.vercel.app/people/")
                )
            )
        }

        api.peopleResponse = {
            Result.success(listOf(PersonApiResponse(id = "person_id", name = "name")))
        }

        toggleFavoriteUseCase("id", true)

        val result = toggleFavoriteUseCase("id", false)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `toggleFavorite should fail to remove a film as favorite if film was never a favorite`() =
        runTest(testDispatcher) {
            val result = toggleFavoriteUseCase("id", false)

            assertTrue(result.isFailure)
        }
}
