package com.remote.ghibli.usecases

import app.cash.turbine.test
import com.remote.ghibli.api.GhibliApiMock
import com.remote.ghibli.cache.GhibliCacheDefault
import com.remote.ghibli.database.GhibliDatabaseDefault
import com.remote.ghibli.database.getDatabaseDriverFactory
import com.remote.ghibli.repository.GhibliRepositoryDefault
import com.remote.ghibli.repository.models.FavoriteFilter
import com.remote.ghibli.repository.models.Filter
import com.remote.ghibli.repository.models.FilterType
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ObserveFiltersUseCaseTest {
    private val api = GhibliApiMock()
    private val database = GhibliDatabaseDefault(getDatabaseDriverFactory())
    private val testDispatcher = StandardTestDispatcher()
    private val cache = GhibliCacheDefault(testDispatcher)
    private val repository = GhibliRepositoryDefault(api, database, cache)

    private val observeFiltersUseCase = ObserveFiltersUseCase(repository, testDispatcher)

    @Test
    fun `observeFilters should trigger when selected filters are updated in the database`() =
        runTest(testDispatcher) {
            val filter1 = Filter("id", "name", FilterType.Specie)
            val filter2 = FavoriteFilter

            repository.addSelectedFilter(filter1)
            repository.addSelectedFilter(filter2)

            observeFiltersUseCase().test {
                val ids = awaitItem()
                assertTrue(ids.size == 2)
                assertContains(ids, filter1.id)
                assertContains(ids, filter2.id)
                cancel()
                ensureAllEventsConsumed()
            }

            repository.removeSelectedFilter(filter1.id)

            observeFiltersUseCase().test {
                val ids = awaitItem()
                assertTrue(ids.size == 1)
                assertContains(ids, filter2.id)
                assertFalse { filter1.id in ids }
                cancel()
                ensureAllEventsConsumed()
            }

            repository.removeSelectedFilter(filter2.id)

            observeFiltersUseCase().test {
                val ids = awaitItem()
                assertTrue(ids.isEmpty())
                cancel()
                ensureAllEventsConsumed()
            }
        }
}
