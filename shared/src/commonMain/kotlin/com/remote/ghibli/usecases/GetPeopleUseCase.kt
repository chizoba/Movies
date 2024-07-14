package com.remote.ghibli.usecases

import com.remote.ghibli.dependencies.SuspendWrapper
import com.remote.ghibli.repository.GhibliRepository
import com.remote.ghibli.repository.models.Person
import com.remote.ghibli.usecases.models.Error
import com.remote.ghibli.usecases.models.ErrorTag
import com.remote.ghibli.utils.getIdFromUrl
import com.remote.ghibli.utils.urlPointsToAllEntities
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetPeopleUseCase(
    val repository: GhibliRepository,
    private val context: CoroutineContext,
) {
    // K/N helpers so we can use coroutines in Swift and keep the types
    fun invokeNative(
        peopleUrls: List<String>,
        isFavorite: Boolean,
        onSuccess: (List<Person>) -> Unit,
        onError: (Throwable) -> Unit
    ) = SuspendWrapper { invoke(peopleUrls, isFavorite) }
        .subscribe({ it.fold(onSuccess, onError) }, onError)

    suspend operator fun invoke(
        peopleUrls: List<String>,
        isCurrentlyFavorite: Boolean,
    ): Result<List<Person>> =
        withContext(context) {
            val allEntitiesUrl = urlPointsToAllEntities(peopleUrls)
            when {
                allEntitiesUrl && isCurrentlyFavorite -> {
                    repository.readFavoritePeople()
                }
                allEntitiesUrl && !isCurrentlyFavorite -> {
                    repository.getPeople()
                }
                !allEntitiesUrl && isCurrentlyFavorite -> {
                    getPersons(peopleUrls, ErrorTag.Favorite) {
                        personId -> repository.readFavoritePerson(personId)
                    }
                }
                !allEntitiesUrl && !isCurrentlyFavorite -> {
                    getPersons(peopleUrls, ErrorTag.Api) {
                        personId -> repository.getPerson(personId)
                    }
                }
                else -> {
                    Result.failure(Error(ErrorTag.Api))
                }
            }
        }

    private suspend fun getPersons(
        peopleUrls: List<String>,
        errorTag: ErrorTag,
        action: suspend (String) -> Result<Person>,
    ) = withContext(context) {
        runCatching {
            val people = peopleUrls.map { url ->
                async {
                    val personId = getIdFromUrl(url)
                    action(personId).getOrNull()
                }
            }.awaitAll()

            if (people.any { it == null }) {
                throw Error(errorTag)
            } else {
                people.filterNotNull()
            }
        }
    }
}
