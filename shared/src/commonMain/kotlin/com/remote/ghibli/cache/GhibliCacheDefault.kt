package com.remote.ghibli.cache

import com.remote.ghibli.repository.models.Film
import com.remote.ghibli.repository.models.Person
import com.remote.ghibli.repository.models.Specie
import io.ktor.util.collections.ConcurrentMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GhibliCacheDefault(private val context: CoroutineContext = Dispatchers.IO) : GhibliCache {
    private val filmsCache = ConcurrentMap<String, Film>()
    private val speciesCache = ConcurrentMap<String, Specie>()
    private val peopleCache = ConcurrentMap<String, Person>()

    override suspend fun getFilms(): Result<List<Film>> = run {
        check(filmsCache.values.isNotEmpty())
        filmsCache.values.toList()
    }

    override suspend fun getFilm(slug: String): Result<Film> = run { filmsCache[slug]!!.copy() }

    override suspend fun getSpecies(): Result<List<Specie>> = run {
        check(speciesCache.values.isNotEmpty())
        speciesCache.values.toList()
    }

    override suspend fun getSpecie(slug: String): Result<Specie> =
        run { speciesCache[slug]!!.copy() }

    override suspend fun getPeople(): Result<List<Person>> = run {
        check(peopleCache.values.isNotEmpty())
        peopleCache.values.toList()
    }

    override suspend fun getPerson(slug: String): Result<Person> =
        run { peopleCache[slug]!!.copy() }

    override suspend fun setFilms(films: List<Film>): Result<Boolean> = run {
        val map = films.associateBy { it.id }
        filmsCache.putAll(map)
        true
    }

    override suspend fun setSpecies(species: List<Specie>): Result<Boolean> = run {
        val map = species.associateBy { it.id }
        speciesCache.putAll(map)
        true
    }

    override suspend fun setPeople(people: List<Person>): Result<Boolean> = run {
        val map = people.associateBy { it.id }
        peopleCache.putAll(map)
        true
    }

    private suspend inline fun <reified T> run(crossinline action: () -> T): Result<T> =
        withContext(context) { runCatching { action() } }
}
