package com.remote.ghibli.dependencies

import co.touchlab.kermit.Logger
import com.remote.ghibli.api.GhibliApi
import com.remote.ghibli.api.GhibliApiDefault
import com.remote.ghibli.cache.GhibliCache
import com.remote.ghibli.cache.GhibliCacheDefault
import com.remote.ghibli.database.DatabaseDriverFactory
import com.remote.ghibli.database.GhibliDatabase
import com.remote.ghibli.database.GhibliDatabaseDefault
import com.remote.ghibli.repository.GhibliRepository
import com.remote.ghibli.repository.GhibliRepositoryDefault
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.coroutines.CoroutineContext

private lateinit var currentDependencies: Dependencies
val Dependencies.Companion.current: Dependencies
    get() {
        check(::currentDependencies.isInitialized) {
            "Dependencies are not initialized. " +
                    "Make sure to call initDependencies() before using Dependencies.current."
        }
        return currentDependencies
    }

fun initWorld(dependencies: Dependencies) {
    currentDependencies = dependencies
}

interface Dependencies {
    val repository: GhibliRepository
    val backgroundContext: CoroutineContext

    companion object
}

class DependenciesLive(
    httpClient: HttpClient,
    databaseDriverFactory: DatabaseDriverFactory,
) : Dependencies {

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            Logger.e("Exception in $coroutineContext", throwable = throwable)
        }
    private val mainContext = Dispatchers.Main + coroutineExceptionHandler
    override val backgroundContext = Dispatchers.Default + coroutineExceptionHandler
    private val ioContext = Dispatchers.IO + coroutineExceptionHandler

    private val api: GhibliApi = GhibliApiDefault(httpClient, ioContext)
    private val database: GhibliDatabase = GhibliDatabaseDefault(databaseDriverFactory, ioContext)
    private val cache: GhibliCache = GhibliCacheDefault(ioContext)

    override val repository: GhibliRepository = GhibliRepositoryDefault(api, database, cache)
}

class DependenciesFactory {
    fun create(databaseDriverFactory: DatabaseDriverFactory): Dependencies =
        DependenciesLive(createHttpClient(), databaseDriverFactory)
}