package com.remote.ghibli.usecases

import app.cash.turbine.test
import com.remote.ghibli.api.GhibliApiMock
import com.remote.ghibli.cache.GhibliCacheDefault
import com.remote.ghibli.database.GhibliDatabaseDefault
import com.remote.ghibli.database.getDatabaseDriverFactory
import com.remote.ghibli.repository.GhibliRepositoryDefault
import com.remote.ghibli.repository.models.Film
import com.remote.ghibli.repository.models.Person
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ObserveFavoriteFilmsUseCaseTest {
    private val api = GhibliApiMock()
    private val database = GhibliDatabaseDefault(getDatabaseDriverFactory())
    private val testDispatcher = StandardTestDispatcher()
    private val cache = GhibliCacheDefault(testDispatcher)
    private val repository = GhibliRepositoryDefault(api, database, cache)

    private val observeFavoriteFilmsUseCase =
        ObserveFavoriteFilmsUseCase(repository, testDispatcher)

    @Test
    fun `observeFavoriteFilms should trigger when favorite films are updated in the database`() =
        runTest(testDispatcher) {
            val film1 = Film(
                "id1",
                "-",
                "-",
                "-",
                "-",
                "-",
                "-",
                "-",
                listOf("https://ghibliapi.vercel.app/people/id")
            )
            val film2 = Film(
                "id2",
                "-",
                "-",
                "-",
                "-",
                "-",
                "-",
                "-",
                listOf("https://ghibliapi.vercel.app/people/id")
            )
            val person = Person("id", "name")
            val people = listOf(person)

            repository.addFavoriteFilm(film1, people)
            repository.addFavoriteFilm(film2, people)

            observeFavoriteFilmsUseCase().test {
                val ids = awaitItem()
                assertTrue(ids.size == 2)
                assertContains(ids, film1.id)
                assertContains(ids, film2.id)
                cancel()
                ensureAllEventsConsumed()
            }

            repository.removeFavoriteFilm(film1.id)

            observeFavoriteFilmsUseCase().test {
                val ids = awaitItem()
                assertTrue(ids.size == 1)
                assertContains(ids, film2.id)
                assertFalse { film1.id in ids }
                cancel()
                ensureAllEventsConsumed()
            }

            repository.removeFavoriteFilm(film2.id)

            observeFavoriteFilmsUseCase().test {
                val ids = awaitItem()
                assertTrue(ids.isEmpty())
                cancel()
                ensureAllEventsConsumed()
            }
        }
}
