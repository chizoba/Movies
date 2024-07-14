package com.remote.ghibli.repository

import com.remote.ghibli.api.GhibliApi
import com.remote.ghibli.cache.GhibliCache
import com.remote.ghibli.database.GhibliDatabase
import com.remote.ghibli.dependencies.SuspendWrapper
import com.remote.ghibli.repository.models.Film
import com.remote.ghibli.repository.models.Filter
import com.remote.ghibli.repository.models.Person
import com.remote.ghibli.repository.models.Specie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.remote.ghibli.database.FavoriteFilms as FavoriteFilmDbModel
import com.remote.ghibli.database.FavoritePeople as FavoritePersonDbModel
import com.remote.ghibli.database.SelectedFilters as SelectedFilterDbModel

interface GhibliRepository {
    suspend fun getFilms(): Result<List<Film>>
    suspend fun getFilm(slug: String): Result<Film>
    suspend fun getSpecies(): Result<List<Specie>>
    suspend fun getSpecie(slug: String): Result<Specie>
    suspend fun getPeople(): Result<List<Person>>
    suspend fun getPerson(slug: String): Result<Person>
    suspend fun addFavoriteFilm(film: Film, people: List<Person>): Result<Boolean>
    suspend fun getFavoriteFilm(id: String): Result<Film>
    suspend fun getFavoriteFilms(): Result<List<Film>>
    suspend fun observeFavoriteFilms(): Flow<List<Film>>
    suspend fun removeFavoriteFilm(id: String): Result<Boolean>
    suspend fun readFavoritePerson(id: String): Result<Person>
    suspend fun readFavoritePeople(): Result<List<Person>>
    suspend fun addSelectedFilter(filter: Filter): Result<Boolean>
    suspend fun getSelectedFilters(): Result<List<Filter>>
    suspend fun observeSelectedFilters(): Flow<List<Filter>>
    suspend fun removeSelectedFilter(id: String): Result<Boolean>
    suspend fun removeSelectedFilters(): Result<Boolean>

    // K/N helpers so we can use coroutines in Swift and keep the types
    fun getFilmsNative(
        onSuccess: (List<Film>) -> Unit,
        onError: (Throwable) -> Unit
    ) = SuspendWrapper { getFilms() }
        .subscribe(
            onSuccess = { it.fold(onSuccess, onError) },
            onError = onError
        )

    fun getSpeciesNative(
        onSuccess: (List<Specie>) -> Unit,
        onError: (Throwable) -> Unit
    ) = SuspendWrapper { getSpecies() }
        .subscribe({ it.fold(onSuccess, onError) }, onError)

    fun filterFilmsNative(
        species: List<String>,
        onSuccess: (List<Film>) -> Unit,
        onError: (Throwable) -> Unit
    ) = SuspendWrapper { }
        .subscribe({ }, onError)
}

class GhibliRepositoryDefault(
    private val api: GhibliApi,
    private val database: GhibliDatabase,
    private val cache: GhibliCache,
) : GhibliRepository {
    override suspend fun getFilms(): Result<List<Film>> =
        cache.getFilms().takeIf { it.isSuccess } ?: api.getFilms().map { films ->
            films.map { film ->
                Film(
                    id = film.id.orEmpty(),
                    title = film.title ?: "-",
                    description = film.description ?: "-",
                    director = film.director ?: "-",
                    producer = film.producer ?: "-",
                    releaseDate = film.releaseDate ?: "-",
                    runningTime = film.runningTime ?: "-",
                    rtScore = film.rtScore ?: "-",
                    peopleUrls = film.peopleUrls.orEmpty(),
                )
            }
        }.also { it.map { films -> cache.setFilms(films) } }

    override suspend fun getFilm(slug: String): Result<Film> =
        cache.getFilm(slug).takeIf { it.isSuccess } ?: api.getFilm(slug).map { film ->
            Film(
                id = film.id.orEmpty(),
                title = film.title ?: "-",
                description = film.description ?: "-",
                director = film.director ?: "-",
                producer = film.producer ?: "-",
                releaseDate = film.releaseDate ?: "-",
                runningTime = film.runningTime ?: "-",
                rtScore = film.rtScore ?: "-",
                peopleUrls = film.peopleUrls.orEmpty(),
            )
        }

    override suspend fun getSpecies(): Result<List<Specie>> =
        cache.getSpecies().takeIf { it.isSuccess } ?: api.getSpecies().map {
            it.map { specie ->
                Specie(
                    id = specie.id.orEmpty(),
                    name = specie.name.orEmpty(),
                    filmUrls = specie.filmUrls.orEmpty(),
                )
            }
        }.also { it.map { species -> cache.setSpecies(species) } }

    override suspend fun getSpecie(slug: String): Result<Specie> =
        cache.getSpecie(slug).takeIf { it.isSuccess } ?: api.getSpecie(slug).map { specie ->
            Specie(
                id = specie.id.orEmpty(),
                name = specie.name.orEmpty(),
                filmUrls = specie.filmUrls.orEmpty(),
            )
        }

    override suspend fun getPeople(): Result<List<Person>> =
        cache.getPeople().takeIf { it.isSuccess } ?: api.getPeople().map {
            it.map { person ->
                Person(
                    id = person.id.orEmpty(),
                    name = person.name.orEmpty(),
                )
            }
        }.also { it.map { people -> cache.setPeople(people) } }

    override suspend fun getPerson(slug: String): Result<Person> =
        cache.getPerson(slug).takeIf { it.isSuccess } ?: api.getPerson(slug).map { person ->
            Person(
                id = person.id.orEmpty(),
                name = person.name.orEmpty(),
            )
        }

    override suspend fun addFavoriteFilm(film: Film, people: List<Person>): Result<Boolean> {
        val favoriteFilm = FavoriteFilmDbModel(
            id = film.id,
            title = film.title,
            description = film.description,
            director = film.director,
            producer = film.producer,
            releaseDate = film.releaseDate,
            runningTime = film.runningTime,
            rtScore = film.rtScore,
            peopleUrls = film.peopleUrls,
        )
        val favoritePeople = people.map {
            FavoritePersonDbModel(it.id, it.name, setOf(favoriteFilm.id))
        }
        return database.createFavoriteFilm(favoriteFilm, favoritePeople)
    }

    override suspend fun getFavoriteFilm(id: String): Result<Film> =
        database.readFavoriteFilm(id).map { it.toFilm() }

    override suspend fun getFavoriteFilms(): Result<List<Film>> =
        database.readFavoriteFilms().map {
            it.map { favoriteFilmDbModel ->
                favoriteFilmDbModel.toFilm()
            }
        }

    override suspend fun observeFavoriteFilms(): Flow<List<Film>> =
        database.observeFavoriteFilms().map {
            it.map { favoriteFilmDbModel ->
                favoriteFilmDbModel.toFilm()
            }
        }

    override suspend fun removeFavoriteFilm(id: String): Result<Boolean> =
        database.deleteFavoriteFilm(id)

    override suspend fun readFavoritePerson(id: String): Result<Person> =
        database.readFavoritePerson(id).map { it.toPerson() }

    override suspend fun readFavoritePeople(): Result<List<Person>> =
        database.readFavoritePeople().map {
            it.map { favoritePersonDbModel ->
                favoritePersonDbModel.toPerson()
            }
        }

    override suspend fun addSelectedFilter(filter: Filter): Result<Boolean> {
        val selectedFilterDbModel = SelectedFilterDbModel(
            id = filter.id,
            name = filter.name,
            type = filter.type.name,
        )
        return database.createSelectedFilter(selectedFilterDbModel)
    }

    override suspend fun getSelectedFilters(): Result<List<Filter>> =
        database.readSelectedFilters().map { selectedFilterDbModel ->
            selectedFilterDbModel.map {
                it.toFilter()
            }
        }

    override suspend fun observeSelectedFilters(): Flow<List<Filter>> =
        database.observeSelectedFilters().map { selectedFilterDbModel ->
            selectedFilterDbModel.map {
                it.toFilter()
            }
        }

    override suspend fun removeSelectedFilter(id: String): Result<Boolean> =
        database.deleteSelectedFilter(id)

    override suspend fun removeSelectedFilters(): Result<Boolean> = database.deleteSelectedFilters()
}
