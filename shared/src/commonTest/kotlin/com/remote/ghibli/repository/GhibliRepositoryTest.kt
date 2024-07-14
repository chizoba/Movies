package com.remote.ghibli.repository

import app.cash.turbine.test
import com.remote.ghibli.api.GhibliApiMock
import com.remote.ghibli.api.models.FilmApiResponse
import com.remote.ghibli.api.models.PersonApiResponse
import com.remote.ghibli.api.models.SpecieApiResponse
import com.remote.ghibli.cache.GhibliCacheDefault
import com.remote.ghibli.database.GhibliDatabaseDefault
import com.remote.ghibli.database.getDatabaseDriverFactory
import com.remote.ghibli.repository.models.FavoriteFilter
import com.remote.ghibli.repository.models.Film
import com.remote.ghibli.repository.models.Filter
import com.remote.ghibli.repository.models.FilterType
import com.remote.ghibli.repository.models.Person
import com.remote.ghibli.repository.models.Specie
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GhibliRepositoryTest {
    private val api = GhibliApiMock()
    private val database = GhibliDatabaseDefault(getDatabaseDriverFactory())
    private val cache = GhibliCacheDefault()
    private val repository = GhibliRepositoryDefault(api, database, cache)

    @Test
    fun `getFilms should return empty list when api returns empty list`() = runTest {
        api.filmsResponse = { Result.success(listOf()) }

        assertEquals(Result.success(listOf()), repository.getFilms())
    }

    @Test
    fun `getFilms should return film list from api`() = runTest {
        val id = "id"
        val title = "title"
        val description = "description"
        val peopleUrls = listOf("firstPersonUrl", "secondPersonUrl")

        api.filmsResponse = {
            Result.success(
                listOf(
                    FilmApiResponse(
                        id = id,
                        title = title,
                        description = description,
                        peopleUrls = peopleUrls,
                    )
                )
            )
        }

        assertEquals(
            Result.success(
                listOf(
                    Film(
                        id,
                        title,
                        description,
                        "-",
                        "-",
                        "-",
                        "-",
                        "-",
                        peopleUrls
                    )
                )
            ),
            repository.getFilms()
        )
    }

    @Test
    fun `getFilms should return error when api returns error`() = runTest {
        val error = Exception()
        api.filmsResponse = { Result.failure(error) }

        assertEquals(
            Result.failure(error),
            repository.getFilms()
        )
    }

    @Test
    fun `getFilms should return film list from cache`() = runTest {
        val id = "id"
        val title = "title"
        val peopleUrls = listOf("firstPersonUrl", "secondPersonUrl")

        cache.setFilms(listOf(Film(id, title, "-", "-", "-", "-", "-", "-", peopleUrls)))

        assertEquals(
            Result.success(listOf(Film(id, title, "-", "-", "-", "-", "-", "-", peopleUrls))),
            repository.getFilms(),
        )
    }

    @Test
    fun `getFilm should return film with default values from api`() = runTest {
        api.filmResponse = {
            Result.success(
                FilmApiResponse(
                    id = "id",
                )
            )
        }

        val expected = Film("id", "-", "-", "-", "-", "-", "-", "-", emptyList())
        assertEquals(Result.success(expected), repository.getFilm("id"))
    }

    @Test
    fun `getFilm should return film from cache`() = runTest {
        val film = Film("id", "-", "-", "-", "-", "-", "-", "-", emptyList())
        cache.setFilms(listOf(film))
        assertEquals(Result.success(film), repository.getFilm("id"))
    }

    @Test
    fun `getSpecies should return species from api`() = runTest {
        api.speciesResponse = {
            Result.success(
                listOf(
                    SpecieApiResponse("id")
                )
            )
        }

        val expected = Specie("id", "", emptyList())
        assertEquals(Result.success(listOf(expected)), repository.getSpecies())
    }

    @Test
    fun `getSpecies should return error when api returns error`() = runTest {
        val error = Exception()
        api.speciesResponse = { Result.failure(error) }

        assertEquals(
            Result.failure(error),
            repository.getSpecies()
        )
    }

    @Test
    fun `getSpecies should return species from cache`() = runTest {
        val species = listOf(Specie("id", "", emptyList()))
        cache.setSpecies(species)
        assertEquals(Result.success(species), repository.getSpecies())
    }

    @Test
    fun `getSpecie should return specie from api`() = runTest {
        api.specieResponse = {
            Result.success(SpecieApiResponse("id"))
        }

        val expected = Specie("id", "", emptyList())
        assertEquals(Result.success(expected), repository.getSpecie("id"))
    }

    @Test
    fun `getSpecie should return error when api returns error`() = runTest {
        val error = Exception()
        api.specieResponse = { Result.failure(error) }

        assertEquals(
            Result.failure(error),
            repository.getSpecie("id")
        )
    }

    @Test
    fun `getSpecie should return specie from cache`() = runTest {
        val specie = Specie("id", "", emptyList())
        cache.setSpecies(listOf(specie))
        assertEquals(Result.success(specie), repository.getSpecie("id"))
    }

    @Test
    fun `getPeople should return list of persons`() = runTest {
        api.peopleResponse = {
            Result.success(listOf(PersonApiResponse("id", "name")))
        }

        val expected = listOf(Person("id", "name"))
        assertEquals(Result.success(expected), repository.getPeople())
    }

    @Test
    fun `getPeople should return error when api returns error`() = runTest {
        val error = Exception()
        api.peopleResponse = { Result.failure(error) }

        assertEquals(
            Result.failure(error),
            repository.getPeople()
        )
    }

    @Test
    fun `getPeople should return people from cache`() = runTest {
        val people = listOf(Person("id", "name"))
        cache.setPeople(people)
        assertEquals(Result.success(people), repository.getPeople())
    }

    @Test
    fun `getPerson should return person`() = runTest {
        api.personResponse = {
            Result.success(PersonApiResponse("id", "name"))
        }

        val expected = Person("id", "name")
        assertEquals(Result.success(expected), repository.getPerson("id"))
    }

    @Test
    fun `getPerson should return error when api returns error`() = runTest {
        val error = Exception()
        api.personResponse = { Result.failure(error) }

        assertEquals(
            Result.failure(error),
            repository.getPerson("id")
        )
    }

    @Test
    fun `getPerson should return person from cache`() = runTest {
        val person = Person("id", "name")
        cache.setPeople(listOf(person))
        assertEquals(Result.success(person), repository.getPerson("id"))
    }

    @Test
    fun `addFavoriteFilm should succeed when adding film to database`() = runTest {
        val film = Film(
            "id",
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

        repository.addFavoriteFilm(film, people)

        assertTrue(database.readFavoriteFilm(film.id).isSuccess)
        assertTrue(database.readFavoritePerson(person.id).isSuccess)
    }

    @Test
    fun `addFavoriteFilm should not create duplicates in database`() = runTest {
        val film = Film(
            "id",
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

        repository.addFavoriteFilm(film, people)
        repository.addFavoriteFilm(film, people)

        assertTrue(database.readFavoriteFilms().getOrThrow().size == 1)
        database.readFavoritePeople().getOrThrow().forEach {
            assertTrue(it.filmIds.size == 1)
        }
    }

    @Test
    fun `getFavoriteFilm should succeed when film exists in database`() = runTest {
        val film = Film(
            "id",
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

        repository.addFavoriteFilm(film, people)

        val result = repository.getFavoriteFilm(film.id)

        assertEquals(film, result.getOrThrow())
    }

    @Test
    fun `getFavoriteFilm should fail when film does not exist in database`() = runTest {
        val film = Film("id", "-", "-", "-", "-", "-", "-", "-", emptyList())
        val result = repository.getFavoriteFilm(film.id)
        assertTrue(result.isFailure)
    }

    @Test
    fun `getFavoriteFilms should succeed when films exist in database`() = runTest {
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

        val result = repository.getFavoriteFilms()
        val favoriteFilms = result.getOrThrow()

        assertEquals(film1, favoriteFilms[0])
        assertEquals(film2, favoriteFilms[1])
    }

    @Test
    fun `getFavoriteFilms should return empty list when database has no films`() = runTest {
        val result = repository.getFavoriteFilms()
        assertTrue(result.getOrThrow().isEmpty())
    }

    @Test
    fun `observeFavoriteFilms should trigger when favorite films are updated in the database`() =
        runTest {
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

            repository.observeFavoriteFilms().test {
                val list = awaitItem()
                assertTrue(list.size == 2)
                assertContains(list, film1)
                assertContains(list, film2)
                cancel()
                ensureAllEventsConsumed()
            }

            repository.removeFavoriteFilm(film1.id)

            repository.observeFavoriteFilms().test {
                val list = awaitItem()
                assertTrue(list.size == 1)
                assertContains(list, film2)
                assertFalse { film1 in list }
                cancel()
                ensureAllEventsConsumed()
            }

            repository.removeFavoriteFilm(film2.id)

            repository.observeFavoriteFilms().test {
                val list = awaitItem()
                assertTrue(list.isEmpty())
                cancel()
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `removeFavoriteFilm should succeed when film is found in database`() = runTest {
        val film = Film(
            "id",
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

        repository.addFavoriteFilm(film, people)

        assertTrue(repository.removeFavoriteFilm(film.id).isSuccess)
        assertTrue(database.readFavoritePerson(person.id).isFailure)
    }

    @Test
    fun `removeFavoriteFilm should fail when film is not found in database`() = runTest {
        val film = Film("id", "-", "-", "-", "-", "-", "-", "-", emptyList())

        val result = repository.removeFavoriteFilm(film.id)

        assertTrue(result.isFailure)
    }

    @Test
    fun `removeFavoriteFilm should not remove person if referenced by another film in database`() =
        runTest {
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

            repository.removeFavoriteFilm(film1.id)

            val dbPerson = database.readFavoritePerson(person.id).getOrThrow()

            assertTrue(film1.id !in dbPerson.filmIds)
            assertTrue(film2.id in dbPerson.filmIds)
            assertEquals(person, dbPerson.toPerson())
        }

    @Test
    fun `addSelectedFilter should succeed when adding filter to database`() = runTest {
        repository.addSelectedFilter(FavoriteFilter)

        val dbFilter = database.readSelectedFilters().getOrThrow()

        assertEquals(FavoriteFilter.id, dbFilter.first().id)
    }

    @Test
    fun `addSelectedFilter should not create duplicates in database`() = runTest {
        repository.addSelectedFilter(FavoriteFilter)
        repository.addSelectedFilter(FavoriteFilter)

        val dbFilter = database.readSelectedFilters().getOrThrow()

        assertEquals(1, dbFilter.size)
    }

    @Test
    fun `getSelectedFilters should return empty list when database has no selected filters`() =
        runTest {
            assertEquals(Result.success(emptyList()), repository.getSelectedFilters())
        }

    @Test
    fun `getSelectedFilters should succeed when selected filters exist in database`() = runTest {
        val filter1 = Filter("id", "name", FilterType.Specie)
        val filter2 = FavoriteFilter

        repository.addSelectedFilter(filter1)
        repository.addSelectedFilter(filter2)

        val selectedFilters = repository.getSelectedFilters().getOrThrow()

        assertEquals(filter1, selectedFilters[0])
        assertEquals(filter2, selectedFilters[1])
    }

    @Test
    fun `observeSelectedFilters should trigger when selected filters are updated in the database`() =
        runTest {
            val filter1 = Filter("id", "name", FilterType.Specie)
            val filter2 = FavoriteFilter

            repository.addSelectedFilter(filter1)
            repository.addSelectedFilter(filter2)

            repository.observeSelectedFilters().test {
                val list = awaitItem()
                assertTrue(list.size == 2)
                assertContains(list, filter1)
                assertContains(list, filter2)
                cancel()
                ensureAllEventsConsumed()
            }

            repository.removeSelectedFilter(filter1.id)

            repository.observeSelectedFilters().test {
                val list = awaitItem()
                assertTrue(list.size == 1)
                assertContains(list, filter2)
                assertFalse { filter1 in list }
                cancel()
                ensureAllEventsConsumed()
            }

            repository.removeSelectedFilter(filter2.id)

            repository.observeSelectedFilters().test {
                val list = awaitItem()
                assertTrue(list.isEmpty())
                cancel()
                ensureAllEventsConsumed()
            }
        }

    @Test
    fun `removeSelectedFilter should succeed when selected filter is found in database`() =
        runTest {
            val filter = FavoriteFilter

            repository.addSelectedFilter(filter)

            assertTrue(repository.removeSelectedFilter(filter.id).isSuccess)
        }

    @Test
    fun `removeSelectedFilters should remove all selected filters in database`() = runTest {
        val filter1 = Filter("id", "name", FilterType.Specie)
        val filter2 = FavoriteFilter

        repository.addSelectedFilter(filter1)
        repository.addSelectedFilter(filter2)

        val result = repository.removeSelectedFilters()

        assertTrue(result.isSuccess)
        assertTrue(repository.getSelectedFilters().getOrThrow().isEmpty())
    }
}
