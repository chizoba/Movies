package com.remote.ghibli.usecases

import com.remote.ghibli.api.GhibliApiMock
import com.remote.ghibli.cache.GhibliCacheDefault
import com.remote.ghibli.database.GhibliDatabaseDefault
import com.remote.ghibli.database.getDatabaseDriverFactory
import com.remote.ghibli.repository.GhibliRepositoryDefault
import com.remote.ghibli.repository.models.FavoriteFilter
import com.remote.ghibli.usecases.models.FilterUiModel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class ToggleFilterUseCaseTest {
    private val api = GhibliApiMock()
    private val database = GhibliDatabaseDefault(getDatabaseDriverFactory())
    private val testDispatcher = StandardTestDispatcher()
    private val cache = GhibliCacheDefault(testDispatcher)
    private val repository = GhibliRepositoryDefault(api, database, cache)

    private val toggleFilterUseCase = ToggleFilterUseCase(repository, testDispatcher)

    @Test
    fun `toggleFilter should add a filter as selected`() = runTest(testDispatcher) {
        val filterUiModel = FilterUiModel(
            id = FavoriteFilter.id,
            name = FavoriteFilter.name,
            isSelected = false,
            type = FavoriteFilter.type,
        )

        val result = toggleFilterUseCase(filterUiModel = filterUiModel, isSelected = true)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `toggleFilter should remove a filter as selected`() = runTest(testDispatcher) {
        val filterUiModel = FilterUiModel(
            id = FavoriteFilter.id,
            name = FavoriteFilter.name,
            isSelected = true,
            type = FavoriteFilter.type,
        )

        val result = toggleFilterUseCase(filterUiModel = filterUiModel, isSelected = false)
        assertTrue(result.isSuccess)
    }
}
