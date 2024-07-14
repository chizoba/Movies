package com.remote.ghibli.database

import kotlinx.coroutines.flow.Flow
import com.remote.ghibli.database.FavoriteFilms as FavoriteFilmDbModel
import com.remote.ghibli.database.FavoritePeople as FavoritePersonDbModel
import com.remote.ghibli.database.SelectedFilters as SelectedFilterDbModel

interface GhibliDatabase {
    suspend fun createFavoriteFilm(
        favoriteFilm: FavoriteFilmDbModel,
        favoritePeople: List<FavoritePersonDbModel>,
    ): Result<Boolean>
    suspend fun readFavoriteFilm(id: String): Result<FavoriteFilmDbModel>
    suspend fun readFavoriteFilms(): Result<List<FavoriteFilmDbModel>>
    suspend fun observeFavoriteFilms(): Flow<List<FavoriteFilmDbModel>>
    suspend fun deleteFavoriteFilm(id: String): Result<Boolean>
    suspend fun readFavoritePerson(id: String): Result<FavoritePersonDbModel>
    suspend fun readFavoritePeople(): Result<List<FavoritePersonDbModel>>
    suspend fun createSelectedFilter(filter: SelectedFilterDbModel): Result<Boolean>
    suspend fun readSelectedFilters(): Result<List<SelectedFilterDbModel>>
    suspend fun observeSelectedFilters(): Flow<List<SelectedFilterDbModel>>
    suspend fun deleteSelectedFilter(id: String): Result<Boolean>
    suspend fun deleteSelectedFilters(): Result<Boolean>
}
