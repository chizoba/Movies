package com.remote.ghibli.usecases

import com.remote.ghibli.dependencies.SuspendWrapper
import com.remote.ghibli.repository.GhibliRepository
import com.remote.ghibli.repository.models.FavoriteFilter
import com.remote.ghibli.repository.models.FilterType
import com.remote.ghibli.usecases.models.FilterUiModel
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetFiltersUseCase(
    val repository: GhibliRepository,
    private val context: CoroutineContext,
) {
    // K/N helpers so we can use coroutines in Swift and keep the types
    fun invokeNative(
        onSuccess: (List<FilterUiModel>) -> Unit,
        onError: (Throwable) -> Unit
    ) = SuspendWrapper { invoke() }
        .subscribe({ it.fold(onSuccess, onError) }, onError)

    suspend operator fun invoke(): Result<List<FilterUiModel>> = withContext(context) {
        val species = repository.getSpecies().getOrDefault(emptyList())
        val selectedFilterIds =
            repository.getSelectedFilters().getOrDefault(emptyList()).map { it.id }.toSet()

        val specieFilters = species.map { specie ->
            val isSelected = specie.id in selectedFilterIds
            FilterUiModel(specie.id, specie.name, isSelected, FilterType.Specie)
        }.sortedBy { it.name }

        val favoriteFilter = FilterUiModel(
            FavoriteFilter.id,
            FavoriteFilter.name,
            FavoriteFilter.id in selectedFilterIds,
            FilterType.Favorite,
        )

        val filters = specieFilters + favoriteFilter

        Result.success(filters)
    }
}
