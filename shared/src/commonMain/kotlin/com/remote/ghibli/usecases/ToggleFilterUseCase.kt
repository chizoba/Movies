package com.remote.ghibli.usecases

import com.remote.ghibli.dependencies.SuspendWrapper
import com.remote.ghibli.repository.GhibliRepository
import com.remote.ghibli.repository.models.Filter
import com.remote.ghibli.usecases.models.FilterUiModel
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class ToggleFilterUseCase(
    val repository: GhibliRepository,
    private val context: CoroutineContext,
) {
    // K/N helpers so we can use coroutines in Swift and keep the types
    fun invokeNative(
        filterUiModel: FilterUiModel,
        isSelected: Boolean,
        onSuccess: (Boolean) -> Unit,
        onError: (Throwable) -> Unit
    ) = SuspendWrapper { invoke(filterUiModel, isSelected) }
        .subscribe({ it.fold(onSuccess, onError) }, onError)

    suspend operator fun invoke(
        filterUiModel: FilterUiModel,
        isSelected: Boolean,
    ): Result<Boolean> = withContext(context) {
        if (isSelected) {
            val filter = Filter(
                id = filterUiModel.id,
                name = filterUiModel.name,
                type = filterUiModel.type,
            )

            repository.addSelectedFilter(filter)
        } else {
            repository.removeSelectedFilter(filterUiModel.id)
        }
    }
}
