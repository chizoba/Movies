package com.remote.ghibli.usecases

import com.remote.ghibli.dependencies.SuspendWrapper
import com.remote.ghibli.repository.GhibliRepository
import com.remote.ghibli.usecases.models.Error
import com.remote.ghibli.usecases.models.ErrorTag
import com.remote.ghibli.usecases.models.FilmDetailUiModel
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetFilmDetailUseCase(
    val repository: GhibliRepository,
    private val context: CoroutineContext,
    private val getPeopleUseCase: GetPeopleUseCase,
) {
    // K/N helpers so we can use coroutines in Swift and keep the types
    fun invokeNative(
        slug: String,
        onSuccess: (FilmDetailUiModel) -> Unit,
        onError: (Throwable) -> Unit
    ) = SuspendWrapper { invoke(slug) }
        .subscribe({ it.fold(onSuccess, onError) }, onError)

    suspend operator fun invoke(id: String): Result<FilmDetailUiModel> = withContext(context) {
        val favoriteFilm = repository.getFavoriteFilm(id)
        val film = favoriteFilm.getOrNull() ?: repository.getFilm(id)
            .getOrElse { return@withContext Result.failure(Error(ErrorTag.Api)) }

        val filmDetailUiModel = FilmDetailUiModel(
            id = film.id,
            title = film.title,
            description = film.description,
            director = film.director,
            producer = film.producer,
            releaseDate = film.releaseDate,
            runningTime = film.runningTime,
            rtScore = film.rtScore,
            people = getPeopleUseCase(film.peopleUrls, favoriteFilm.isSuccess)
                .getOrDefault(emptyList()).map { it.name },
            isFavorite = favoriteFilm.isSuccess,
        )

        Result.success(filmDetailUiModel)
    }
}
