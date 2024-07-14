package com.remote.ghibli.usecases

import com.remote.ghibli.dependencies.SuspendWrapper
import com.remote.ghibli.repository.GhibliRepository
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class ToggleFavoriteUseCase(
    val repository: GhibliRepository,
    private val context: CoroutineContext,
    private val getPeopleUseCase: GetPeopleUseCase,
) {
    // K/N helpers so we can use coroutines in Swift and keep the types
    fun invokeNative(
        filmId: String,
        isFavorite: Boolean,
        onSuccess: (Boolean) -> Unit,
        onError: (Throwable) -> Unit
    ) = SuspendWrapper { invoke(filmId, isFavorite) }
        .subscribe({ it.fold(onSuccess, onError) }, onError)

    suspend operator fun invoke(filmId: String, isFavorite: Boolean): Result<Boolean> =
        withContext(context) {
            if (isFavorite) {
                val film = repository.getFilm(filmId).getOrElse {
                    return@withContext Result.failure(it)
                }
                val people = getPeopleUseCase(film.peopleUrls, !isFavorite).getOrElse {
                    return@withContext Result.failure(it)
                }
                repository.addFavoriteFilm(film, people)
            } else {
                repository.removeFavoriteFilm(filmId)
            }
        }
}
