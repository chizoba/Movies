package com.remote.ghibli.usecases

import com.remote.ghibli.repository.GhibliRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class ObserveFiltersUseCase(
    val repository: GhibliRepository,
    private val context: CoroutineContext,
) {
    // K/N helpers so we can use coroutines in Swift and keep the types
//    fun invokeNative(
//        onEach: (item: List<String>) -> Unit,
//        onCompletion: (cause: Throwable?) -> Unit,
//    ) = SuspendWrapper { invoke() }
//        .subscribe({ it.wrap(onEach, onCompletion) }, onCompletion)

    suspend operator fun invoke(): Flow<List<String>> = withContext(context) {
        repository.observeSelectedFilters().map { filters -> filters.map { it.id } }
    }
}
