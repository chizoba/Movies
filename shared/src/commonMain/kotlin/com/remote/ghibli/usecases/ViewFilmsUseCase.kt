package com.remote.ghibli.usecases

import com.remote.ghibli.dependencies.SuspendWrapper
import com.remote.ghibli.repository.GhibliRepository
import com.remote.ghibli.repository.models.FavoriteFilter
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class ViewFilmsUseCase(
    val repository: GhibliRepository,
    private val context: CoroutineContext,
) {
    // K/N helpers so we can use coroutines in Swift and keep the types
    fun invokeNative(
        byFavorite: Boolean,
        onSuccess: (Boolean) -> Unit,
        onError: (Throwable) -> Unit
    ) = SuspendWrapper { invoke(byFavorite) }
        .subscribe({ it.fold(onSuccess, onError) }, onError)

    suspend operator fun invoke(
        byFavorite: Boolean,
    ): Result<Boolean> = withContext(context) {
        if (byFavorite) {
            repository.removeSelectedFilters()
            repository.addSelectedFilter(FavoriteFilter)
        } else {
            repository.removeSelectedFilters()
        }
    }
}
