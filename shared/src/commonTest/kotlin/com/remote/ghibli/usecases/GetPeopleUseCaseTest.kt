package com.remote.ghibli.usecases

import com.remote.ghibli.api.GhibliApiMock
import com.remote.ghibli.api.models.PersonApiResponse
import com.remote.ghibli.cache.GhibliCacheDefault
import com.remote.ghibli.database.GhibliDatabaseDefault
import com.remote.ghibli.database.getDatabaseDriverFactory
import com.remote.ghibli.repository.GhibliRepositoryDefault
import com.remote.ghibli.repository.models.Film
import com.remote.ghibli.repository.models.Person
import com.remote.ghibli.usecases.models.Error
import com.remote.ghibli.usecases.models.ErrorTag
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPeopleUseCaseTest {
    private val api = GhibliApiMock()
    private val database = GhibliDatabaseDefault(getDatabaseDriverFactory())
    private val testDispatcher = StandardTestDispatcher()
    private val cache = GhibliCacheDefault(testDispatcher)
    private val repository = GhibliRepositoryDefault(api, database, cache)

    private val getPeopleUseCase = GetPeopleUseCase(repository, testDispatcher)

    @Test
    fun `getPeople should return people from favorites`() = runTest(testDispatcher) {
        val film = Film(
            "id",
            "-",
            "-",
            "-",
            "-",
            "-",
            "-",
            "-",
            listOf("https://ghibliapi.vercel.app/people/")
        )
        val person1 = Person("id1", "name1")
        val person2 = Person("id2", "name2")
        val people = listOf(person1, person2)
        repository.addFavoriteFilm(film, people)

        val peopleUrls = listOf("https://ghibliapi.vercel.app/people/")
        val isCurrentlyFavorite = true

        val result = getPeopleUseCase(peopleUrls, isCurrentlyFavorite)
        assertEquals(Result.success(people), result)
    }

    @Test
    fun `getPeople should return empty when retrieving people from favorites`() =
        runTest(testDispatcher) {
            val peopleUrls = listOf("https://ghibliapi.vercel.app/people/")
            val isCurrentlyFavorite = true

            val result = getPeopleUseCase(peopleUrls, isCurrentlyFavorite)
            assertEquals(Result.success(emptyList()), result)
        }

    @Test
    fun `getPeople should return people from api`() = runTest(testDispatcher) {
        api.peopleResponse = {
            Result.success(listOf(PersonApiResponse("id", "name")))
        }

        val peopleUrls = listOf("https://ghibliapi.vercel.app/people/")
        val isCurrentlyFavorite = false

        val result = getPeopleUseCase(peopleUrls, isCurrentlyFavorite)
        assertEquals(Result.success(listOf(Person("id", "name"))), result)
    }

    @Test
    fun `getPeople should fail when retrieving people from api`() = runTest(testDispatcher) {
        val error = Exception()
        api.peopleResponse = {
            Result.failure(error)
        }

        val peopleUrls = listOf("https://ghibliapi.vercel.app/people/")
        val isCurrentlyFavorite = false

        val result = getPeopleUseCase(peopleUrls, isCurrentlyFavorite)
        assertEquals(Result.failure(error), result)
    }

    @Test
    fun `getPeople should return specified persons from favorites`() = runTest(testDispatcher) {
        val film = Film(
            "id",
            "-",
            "-",
            "-",
            "-",
            "-",
            "-",
            "-",
            listOf(
                "https://ghibliapi.vercel.app/people/id1",
                "https://ghibliapi.vercel.app/people/id2"
            )
        )
        val person1 = Person("id1", "name1")
        val person2 = Person("id2", "name2")
        val people = listOf(person1, person2)
        repository.addFavoriteFilm(film, people)

        val peopleUrls = listOf("https://ghibliapi.vercel.app/people/id1")
        val isCurrentlyFavorite = true

        val result = getPeopleUseCase(peopleUrls, isCurrentlyFavorite)
        assertEquals(Result.success(listOf(person1)), result)
    }

    @Test
    fun `getPeople should fail when retrieving specified persons from favorites`() =
        runTest(testDispatcher) {
            val peopleUrls = listOf("https://ghibliapi.vercel.app/people/id")
            val isCurrentlyFavorite = true

            val result = getPeopleUseCase(peopleUrls, isCurrentlyFavorite)
            assertEquals(Result.failure(Error(ErrorTag.Favorite)), result)
        }

    @Test
    fun `getPeople should return specified persons from api`() = runTest(testDispatcher) {
        api.personResponse = {
            Result.success(PersonApiResponse("id", "name"))
        }

        val peopleUrls = listOf("https://ghibliapi.vercel.app/people/id")
        val isCurrentlyFavorite = false

        val result = getPeopleUseCase(peopleUrls, isCurrentlyFavorite)
        assertEquals(Result.success(listOf(Person("id", "name"))), result)
    }

    @Test
    fun `getPeople should fail when retrieving specified persons from api`() =
        runTest(testDispatcher) {
            api.personResponse = {
                Result.failure(Exception())
            }

            val peopleUrls = listOf("https://ghibliapi.vercel.app/people/id")
            val isCurrentlyFavorite = false

            val result = getPeopleUseCase(peopleUrls, isCurrentlyFavorite)
            assertEquals(Result.failure(Error(ErrorTag.Api)), result)
        }
}
