package com.remote.ghibli.android.films

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remote.ghibli.android.components.SnackbarFeedback
import com.remote.ghibli.dependencies.Dependencies
import com.remote.ghibli.dependencies.current
import com.remote.ghibli.repository.GhibliRepository
import com.remote.ghibli.usecases.ViewFilmsUseCase
import com.remote.ghibli.usecases.GetFilmListUseCase
import com.remote.ghibli.usecases.GetFiltersUseCase
import com.remote.ghibli.usecases.GetPeopleUseCase
import com.remote.ghibli.usecases.ObserveFavoriteFilmsUseCase
import com.remote.ghibli.usecases.ObserveFiltersUseCase
import com.remote.ghibli.usecases.ToggleFavoriteUseCase
import com.remote.ghibli.usecases.ToggleFilterUseCase
import com.remote.ghibli.usecases.models.Error
import com.remote.ghibli.usecases.models.ErrorTag
import com.remote.ghibli.usecases.models.FilmUiModel
import com.remote.ghibli.usecases.models.FilterUiModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FilmListViewModel(
    private val repository: GhibliRepository = Dependencies.current.repository,
    private val getFilmListUseCase: GetFilmListUseCase = GetFilmListUseCase(
        repository,
        Dependencies.current.backgroundContext,
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
    private val getFiltersUseCase: GetFiltersUseCase = GetFiltersUseCase(
        repository,
        Dependencies.current.backgroundContext,
    ),
    private val toggleFilterUseCase: ToggleFilterUseCase = ToggleFilterUseCase(
        repository,
        Dependencies.current.backgroundContext,
    ),
    private val observeFiltersUseCase: ObserveFiltersUseCase = ObserveFiltersUseCase(
        repository,
        Dependencies.current.backgroundContext,
    ),
    private val viewFilmsUseCase: ViewFilmsUseCase = ViewFilmsUseCase(
        repository,
        Dependencies.current.backgroundContext,
    ),
) : ViewModel() {

    data class State(
        val isLoading: Boolean = true,
        val error: Error? = null,
        val films: List<FilmUiModel> = emptyList(),
        val filters: List<FilterUiModel> = emptyList(),
    )

    private var _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()
    private val _feedback = MutableSharedFlow<SnackbarFeedback>()
    val feedback = _feedback.asSharedFlow()

    init {
        observeFilters()
        observeFavoriteFilms()
    }

    private fun observeFilters() = viewModelScope.launch {
        observeFiltersUseCase().collect { reloadData() }
    }

    private fun observeFavoriteFilms() = viewModelScope.launch {
        observeFavoriteFilmsUseCase().collect { reloadData() }
    }

    suspend fun loadData() {
        _state.update { State() }
        reloadData()
    }

    private suspend fun reloadData() {
        getFilms()
        getFilters()
    }

    private suspend fun getFilms() {
        val films = getFilmListUseCase().getOrElse { throwable ->
            _state.update { it.copy(isLoading = false, error = throwable as? Error) }
            return
        }
        _state.update { it.copy(isLoading = false, films = films, error = null) }
    }

    private suspend fun getFilters() {
        val filters = getFiltersUseCase().getOrElse {
            return
        }
        _state.update { it.copy(filters = filters) }
    }

    suspend fun toggleFilter(filterUiModel: FilterUiModel) =
        toggleFilterUseCase(filterUiModel, !filterUiModel.isSelected)

    suspend fun toggleFavorite(filmId: String, isFavorite: Boolean) =
        toggleFavoriteUseCase(filmId, isFavorite).getOrElse {
            _feedback.emit(SnackbarFeedback(tag = ErrorTag.Favorite))
        }

    suspend fun viewFilms(byFavorite: Boolean) = viewFilmsUseCase(byFavorite)
}
