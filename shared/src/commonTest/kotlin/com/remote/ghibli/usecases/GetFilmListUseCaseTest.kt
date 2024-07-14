package com.remote.ghibli.usecases

import com.remote.ghibli.api.GhibliApiMock
import com.remote.ghibli.api.models.FilmApiResponse
import com.remote.ghibli.api.models.SpecieApiResponse
import com.remote.ghibli.cache.GhibliCacheDefault
import com.remote.ghibli.database.GhibliDatabaseDefault
import com.remote.ghibli.database.getDatabaseDriverFactory
import com.remote.ghibli.repository.GhibliRepositoryDefault
import com.remote.ghibli.repository.models.FavoriteFilter
import com.remote.ghibli.repository.models.Film
import com.remote.ghibli.repository.models.Filter
import com.remote.ghibli.repository.models.FilterType
import com.remote.ghibli.repository.models.Person
import com.remote.ghibli.usecases.models.Error
import com.remote.ghibli.usecases.models.ErrorTag
import com.remote.ghibli.usecases.models.FilmUiModel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetFilmListUseCaseTest {
    private val api = GhibliApiMock()
    private val database = GhibliDatabaseDefault(getDatabaseDriverFactory())
    private val testDispatcher = StandardTestDispatcher()
    private val cache = GhibliCacheDefault()
    private val repository = GhibliRepositoryDefault(api, database, cache)

    private val getFilmListUseCase = GetFilmListUseCase(repository, testDispatcher)

    @Test
    fun `getFilmList should return all films sorted by date`() = runTest(testDispatcher) {
        val id1 = "id1"
        val id2 = "id2"
        val title = "title"
        val description = "description"
        val releaseDate1 = "2000"
        val releaseDate2 = "2024"
        val peopleUrls = listOf("firstPersonUrl", "secondPersonUrl")

        api.filmsResponse = {
            Result.success(
                listOf(
                    FilmApiResponse(
                        id = id2,
                        title = title,
                        description = description,
                        releaseDate = releaseDate2,
                        peopleUrls = peopleUrls,
                    ),
                    FilmApiResponse(
                        id = id1,
                        title = title,
                        description = description,
                        releaseDate = releaseDate1,
                        peopleUrls = peopleUrls,
                    ),
                ),
            )
        }

        val expected = listOf(
            FilmUiModel(
                id = id1,
                title = title,
                description = description,
                releaseDate = releaseDate1,
                isFavorite = false,
            ),
            FilmUiModel(
                id = id2,
                title = title,
                description = description,
                releaseDate = releaseDate2,
                isFavorite = false,
            ),
        )

        assertEquals(Result.success(expected), getFilmListUseCase())
    }

    @Test
    fun `getFilmList should return films filtered by specie`() = runTest(testDispatcher) {
        val id1 = "id1"
        val id2 = "id2"
        val title = "title"
        val description = "description"
        val releaseDate1 = "2000"
        val releaseDate2 = "2024"
        val peopleUrls = listOf("firstPersonUrl", "secondPersonUrl")

        api.filmsResponse = {
            Result.success(
                listOf(
                    FilmApiResponse(
                        id = id2,
                        title = title,
                        description = description,
                        releaseDate = releaseDate2,
                        peopleUrls = peopleUrls,
                    ),
                    FilmApiResponse(
                        id = id1,
                        title = title,
                        description = description,
                        releaseDate = releaseDate1,
                        peopleUrls = peopleUrls,
                    ),
                ),
            )
        }

        api.specieResponse = {
            Result.success(
                SpecieApiResponse(
                    "id",
                    "specie_name",
                    listOf("https://ghibliapi.vercel.app/films/id2"),
                ),
            )
        }

        repository.addSelectedFilter(Filter("id", "specie_name", FilterType.Specie))

        val expected = listOf(
            FilmUiModel(
                id = id2,
                title = title,
                description = description,
                releaseDate = releaseDate2,
                isFavorite = false,
            ),
        )

        assertEquals(Result.success(expected), getFilmListUseCase())
    }

    @Test
    fun `getFilmList with no favorite filter should return error when api returns error`() =
        runTest(testDispatcher) {
            api.filmsResponse = {
                Result.failure(Exception())
            }

            assertEquals(Result.failure(Error(ErrorTag.Api)), getFilmListUseCase())
        }

    @Test
    fun `getFilmList with favorite filter should return error when no favorite film exists`() =
        runTest(testDispatcher) {
            api.filmsResponse = {
                Result.failure(Exception())
            }

            repository.addSelectedFilter(FavoriteFilter)

            assertEquals(Result.failure(Error(ErrorTag.Favorite)), getFilmListUseCase())
        }

    @Test
    fun `getFilmList with favorite filter should return favorite films when api returns error`() =
        runTest(testDispatcher) {
            api.filmsResponse = {
                Result.failure(Exception())
            }

            repository.addSelectedFilter(FavoriteFilter)
            val peopleUrls = listOf("firstPersonUrl", "secondPersonUrl")
            val film = Film("id2", "-", "-", "-", "-", "-", "-", "-", peopleUrls)
            repository.addFavoriteFilm(film, listOf(Person("id", "name")))


            val expected = listOf(
                FilmUiModel(
                    id = "id2",
                    title = "-",
                    description = "-",
                    releaseDate = "-",
                    isFavorite = true,
                ),
            )

            assertEquals(Result.success(expected), getFilmListUseCase())
        }
}
