package com.remote.ghibli.usecases

import com.remote.ghibli.dependencies.SuspendWrapper
import com.remote.ghibli.repository.GhibliRepository
import com.remote.ghibli.repository.models.Filter
import com.remote.ghibli.repository.models.FilterType
import com.remote.ghibli.usecases.models.Error
import com.remote.ghibli.usecases.models.ErrorTag
import com.remote.ghibli.usecases.models.FilmUiModel
import com.remote.ghibli.utils.getIdFromUrl
import com.remote.ghibli.utils.urlPointsToAllEntities
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetFilmListUseCase(
    val repository: GhibliRepository,
    private val context: CoroutineContext,
) {
    // K/N helpers so we can use coroutines in Swift and keep the types
    fun invokeNative(
        onSuccess: (List<FilmUiModel>) -> Unit,
        onError: (Throwable) -> Unit
    ) = SuspendWrapper { invoke() }
        .subscribe({ it.fold(onSuccess, onError) }, onError)

    suspend operator fun invoke(): Result<List<FilmUiModel>> = withContext(context) {
        val selectedFilters = repository.getSelectedFilters().getOrDefault(emptyList())
        val favoriteFilms = repository.getFavoriteFilms().getOrDefault(emptyList())
        val favoriteFilmIds = favoriteFilms.map { it.id }.toSet()

        val films = repository.getFilms().getOrDefault(emptyList())

        val filteredFilms = if (selectedFilters.isEmpty()) {
            films
        } else {
            val filterByFilmIds = getFilmIdsToFilterBy(selectedFilters, favoriteFilmIds)
            val filmsToFilter = films.takeIf { it.isNotEmpty() } ?: favoriteFilms
            filmsToFilter.filter { film -> film.id in filterByFilmIds }
        }

        val filmUiModels = filteredFilms.map {
            FilmUiModel(
                id = it.id,
                title = it.title,
                description = it.description,
                releaseDate = it.releaseDate,
                isFavorite = it.id in favoriteFilmIds,
            )
        }.sortedBy { it.releaseDate }

        if (filmUiModels.isEmpty()) {
            Result.failure(getError(selectedFilters))
        } else {
            Result.success(filmUiModels)
        }
    }

    private suspend fun getFilmIdsToFilterBy(
        selectedFilters: List<Filter>,
        favoriteFilmIds: Set<String>,
    ) = buildSet {
        selectedFilters.forEach { filter ->
            when (filter.type) {
                FilterType.Specie -> {
                    val specie = repository.getSpecie(filter.id).getOrNull()
                    // It does not seem like the film property of any specie points to the entire
                    // film collection (i.e. '/films/') but I added the 'urlPointsToAllEntities'
                    // check below for safety as it could be possible.
                    val filmUrls = if (specie != null && urlPointsToAllEntities(specie.filmUrls)) {
                        repository.getSpecies().getOrDefault(emptyList()).flatMap { it.filmUrls }
                    } else {
                        specie?.filmUrls
                    }
                    filmUrls?.forEach { filmUrl -> add(getIdFromUrl(filmUrl)) }
                }

                FilterType.Favorite -> {
                    addAll(favoriteFilmIds)
                }
            }
        }
    }

    private fun getError(selectedFilters: List<Filter>): Error {
        return if (selectedFilters.hasOnlyFavoriteFilter()) {
            Error(ErrorTag.Favorite)
        } else {
            Error(ErrorTag.Api)
        }
    }

    private fun List<Filter>.hasOnlyFavoriteFilter(): Boolean =
        this.size == 1 && this.first().type == FilterType.Favorite
}
