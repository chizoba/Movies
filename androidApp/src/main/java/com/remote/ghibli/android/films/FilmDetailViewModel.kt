package com.remote.ghibli.android.films

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remote.ghibli.android.components.SnackbarFeedback
import com.remote.ghibli.dependencies.Dependencies
import com.remote.ghibli.dependencies.current
import com.remote.ghibli.repository.GhibliRepository
import com.remote.ghibli.repository.models.Person
import com.remote.ghibli.usecases.GetFilmDetailUseCase
import com.remote.ghibli.usecases.GetPeopleUseCase
import com.remote.ghibli.usecases.ObserveFavoriteFilmsUseCase
import com.remote.ghibli.usecases.ToggleFavoriteUseCase
import com.remote.ghibli.usecases.models.ErrorTag
import com.remote.ghibli.usecases.models.FilmDetailUiModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FilmDetailViewModel(
    private val repository: GhibliRepository = Dependencies.current.repository,
    private val getFilmDetailUseCase: GetFilmDetailUseCase = GetFilmDetailUseCase(
        repository,
        Dependencies.current.backgroundContext,
        GetPeopleUseCase(repository, Dependencies.current.backgroundContext),
    ),
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = ToggleFavoriteUseCase(
        repository,
        Dependencies.current.backgroundContext,
        GetPeopleUseCase(repository, Dependencies.current.backgroundContext),
    ),
    private val observeFavoriteFilmsUseCase: ObserveFavoriteFilmsUseCase = ObserveFavoriteFilmsUseCase(
        repository,
        Dependencies.current.backgroundContext,
    ),
) : ViewModel() {

    data class State(
        val isLoading: Boolean = true,
        val errorMessage: String? = null,
        val title: String = "-",
        val film: FilmDetailUiModel? = null,
        val people: List<Person> = emptyList(),
    )

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()
    private val _feedback = MutableSharedFlow<SnackbarFeedback>()
    val feedback = _feedback.asSharedFlow()

    init {
        observeFavoriteFilm()
    }

    private fun observeFavoriteFilm() = viewModelScope.launch {
        observeFavoriteFilmsUseCase().collect { favoriteFilmIds ->
            val isFavorite = state.value.film?.id in favoriteFilmIds
            val updatedFilm = state.value.film?.copy(isFavorite = isFavorite)
            _state.update { it.copy(film = updatedFilm) }
        }
    }

    suspend fun loadData(slug: String) {
        _state.update { State() }
        val film = getFilmDetailUseCase.invoke(slug).getOrElse { _ ->
            _state.update {
                State(
                    isLoading = false,
                    errorMessage = "Failed to load film. Please try again.",
                )
            }
            return
        }
        _state.update {
            State(
                isLoading = false,
                title = film.title,
                film = film,
            )
        }
    }

    suspend fun toggleFavorite(filmId: String, isFavorite: Boolean) =
        toggleFavoriteUseCase(filmId, isFavorite).getOrElse {
            _feedback.emit(SnackbarFeedback(tag = ErrorTag.Favorite))
        }
}
