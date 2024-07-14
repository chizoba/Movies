package com.remote.ghibli.usecases

import com.remote.ghibli.api.GhibliApiMock
import com.remote.ghibli.api.models.SpecieApiResponse
import com.remote.ghibli.cache.GhibliCacheDefault
import com.remote.ghibli.database.GhibliDatabaseDefault
import com.remote.ghibli.database.getDatabaseDriverFactory
import com.remote.ghibli.repository.GhibliRepositoryDefault
import com.remote.ghibli.repository.models.FavoriteFilter
import com.remote.ghibli.repository.models.FilterType
import com.remote.ghibli.usecases.models.FilterUiModel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GetFiltersUseCaseTest {
    private val api = GhibliApiMock()
    private val database = GhibliDatabaseDefault(getDatabaseDriverFactory())
    private val testDispatcher = StandardTestDispatcher()
    private val cache = GhibliCacheDefault(testDispatcher)
    private val repository = GhibliRepositoryDefault(api, database, cache)

    private val getFiltersUseCase = GetFiltersUseCase(repository, testDispatcher)

    @Test
    fun `getFilters should return only favorite filter when api fails`() = runTest(testDispatcher) {
        api.speciesResponse = {
            Result.failure(Exception())
        }

        val filters = listOf(
            FilterUiModel(
                FavoriteFilter.id,
                FavoriteFilter.name,
                false,
                FilterType.Favorite,
            ),
        )

        val result = getFiltersUseCase()
        assertEquals(Result.success(filters), result)
    }

    @Test
    fun `getFilters should return all filters when api succeeds`() = runTest(testDispatcher) {
        val species = listOf(
            SpecieApiResponse("id1", "name1", listOf()),
            SpecieApiResponse("id2", "name2", listOf()),
        )
        api.speciesResponse = {
            Result.success(species)
        }

        val result = getFiltersUseCase()
        assertEquals(3, result.getOrThrow().size)
    }

    @Test
    fun `getFilters should return filters with their selected statuses`() =
        runTest(testDispatcher) {
            val species = listOf(
                SpecieApiResponse("id1", "name1", listOf()),
                SpecieApiResponse("id2", "name2", listOf()),
            )
            api.speciesResponse = {
                Result.success(species)
            }
            repository.addSelectedFilter(FavoriteFilter)

            val result = getFiltersUseCase()
            assertFalse(result.getOrThrow()[0].isSelected)
            assertFalse(result.getOrThrow()[1].isSelected)
            assertTrue(result.getOrThrow()[2].isSelected)
        }

    @Test
    fun `getFilters should be sorted by name with favorite filter at the end`() =
        runTest(testDispatcher) {
            val species = listOf(
                SpecieApiResponse("id1", "zebra", listOf()),
                SpecieApiResponse("id2", "cat", listOf()),
            )
            api.speciesResponse = {
                Result.success(species)
            }

            val result = getFiltersUseCase()
            assertEquals(result.getOrThrow()[0].name, "cat")
            assertEquals(result.getOrThrow()[1].name, "zebra")
            assertEquals(result.getOrThrow()[2].name, FavoriteFilter.name)
        }
}
