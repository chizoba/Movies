package com.remote.ghibli.usecases

import com.remote.ghibli.api.GhibliApiMock
import com.remote.ghibli.cache.GhibliCacheDefault
import com.remote.ghibli.database.GhibliDatabaseDefault
import com.remote.ghibli.database.getDatabaseDriverFactory
import com.remote.ghibli.repository.GhibliRepositoryDefault
import com.remote.ghibli.repository.models.FavoriteFilter
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ViewFilmsUseCaseTest {
    private val api = GhibliApiMock()
    private val database = GhibliDatabaseDefault(getDatabaseDriverFactory())
    private val testDispatcher = StandardTestDispatcher()
    private val cache = GhibliCacheDefault(testDispatcher)
    private val repository = GhibliRepositoryDefault(api, database, cache)

    private val viewFilmsUseCase = ViewFilmsUseCase(repository, testDispatcher)

    @Test
    fun `viewFilms should remove all selected filters and only select favorite filter`() =
        runTest(testDispatcher) {
            viewFilmsUseCase(byFavorite = true)
            assertEquals(Result.success(listOf(FavoriteFilter)), repository.getSelectedFilters())
        }

    @Test
    fun `viewFilms should remove all selected filters`() = runTest(testDispatcher) {
        viewFilmsUseCase(byFavorite = false)
        assertEquals(Result.success(emptyList()), repository.getSelectedFilters())
    }
}
