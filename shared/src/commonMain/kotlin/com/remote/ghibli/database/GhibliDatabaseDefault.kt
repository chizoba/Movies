package com.remote.ghibli.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.remote.ghibli.database.FavoriteFilms
import com.remote.ghibli.database.FavoritePeople
import com.remote.ghibli.utils.urlPointsToAllEntities
import com.remote.ghibli.utils.getIdFromUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import com.remote.ghibli.database.FavoriteFilms as FavoriteFilmDbModel
import com.remote.ghibli.database.FavoritePeople as FavoritePersonDbModel
import com.remote.ghibli.database.SelectedFilters as SelectedFilterDbModel

class GhibliDatabaseDefault(
    databaseDriverFactory: DatabaseDriverFactory,
    private val context: CoroutineContext = Dispatchers.IO,
) : GhibliDatabase {
    private val database = AppDatabase(
        driver = databaseDriverFactory.createDriver(),
        favoriteFilmsAdapter = FavoriteFilms.Adapter(peopleUrlsAdapter = listOfStringAdapter),
        favoritePeopleAdapter = FavoritePeople.Adapter(filmIdsAdapter = setOfStringAdapter),
    )
    private val dbQuery = database.appDatabaseQueries

    override suspend fun createFavoriteFilm(
        favoriteFilm: FavoriteFilmDbModel,
        favoritePeople: List<FavoritePersonDbModel>,
    ): Result<Boolean> = run {
        dbQuery.transactionWithResult {
            dbQuery.createFavoriteFilm(
                id = favoriteFilm.id,
                title = favoriteFilm.title,
                description = favoriteFilm.description,
                director = favoriteFilm.director,
                producer = favoriteFilm.producer,
                releaseDate = favoriteFilm.releaseDate,
                runningTime = favoriteFilm.runningTime,
                rtScore = favoriteFilm.rtScore,
                peopleUrls = favoriteFilm.peopleUrls,
            )
            favoritePeople.forEach {
                val dbPerson = dbQuery.readFavoritePerson(it.id).executeAsOneOrNull()
                val filmIds = buildSet {
                    dbPerson?.let { person -> addAll(person.filmIds) }
                    addAll(it.filmIds)
                }
                dbQuery.createFavoritePerson(it.id, it.name, filmIds)
            }
            true
        }
    }

    override suspend fun readFavoriteFilm(id: String): Result<FavoriteFilmDbModel> = run {
        dbQuery.readFavoriteFilm(id).executeAsOne()
    }

    override suspend fun readFavoriteFilms(): Result<List<FavoriteFilms>> = run {
        dbQuery.readFavoriteFilms().executeAsList()
    }

    override suspend fun observeFavoriteFilms(): Flow<List<FavoriteFilmDbModel>> =
        withContext(context) { dbQuery.readFavoriteFilms().asFlow().mapToList(context) }

    override suspend fun deleteFavoriteFilm(id: String): Result<Boolean> = run {
        dbQuery.transactionWithResult {
            val film = dbQuery.readFavoriteFilm(id).executeAsOne()
            dbQuery.deleteFavoriteFilm(id)

            val peopleIds = if (urlPointsToAllEntities(film.peopleUrls)) {
                dbQuery.readFavoritePeople().executeAsList().map { it.id }
            } else {
                film.peopleUrls.map { getIdFromUrl(it) }
            }

            peopleIds.forEach { personId ->
                val person = dbQuery.readFavoritePerson(personId).executeAsOne()
                val newFilmIds = person.filmIds - id
                if (newFilmIds.isEmpty()) {
                    dbQuery.deleteFavoritePerson(personId)
                } else {
                    dbQuery.createFavoritePerson(person.id, person.name, newFilmIds)
                }
            }

            true
        }
    }

    override suspend fun readFavoritePerson(id: String): Result<FavoritePeople> = run {
        dbQuery.readFavoritePerson(id).executeAsOne()
    }

    override suspend fun readFavoritePeople(): Result<List<FavoritePeople>> = run {
        dbQuery.readFavoritePeople().executeAsList()
    }

    override suspend fun createSelectedFilter(filter: SelectedFilterDbModel): Result<Boolean> =
        run {
            dbQuery.createSelectedFilter(
                id = filter.id,
                name = filter.name,
                type = filter.type,
            )
            true
        }

    override suspend fun readSelectedFilters(): Result<List<SelectedFilterDbModel>> = run {
        dbQuery.readSelectedFilters().executeAsList()
    }

    override suspend fun observeSelectedFilters(): Flow<List<SelectedFilterDbModel>> =
        withContext(context) {
            dbQuery.readSelectedFilters().asFlow().mapToList(context)
        }

    override suspend fun deleteSelectedFilter(id: String): Result<Boolean> = run {
        dbQuery.deleteSelectedFilter(id)
        true
    }

    override suspend fun deleteSelectedFilters(): Result<Boolean> = run {
        dbQuery.deleteSelectedFilters()
        true
    }

    private suspend inline fun <reified T> run(crossinline action: () -> T): Result<T> =
        withContext(context) { runCatching { action() } }
}
