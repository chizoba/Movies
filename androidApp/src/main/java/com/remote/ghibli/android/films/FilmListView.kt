package com.remote.ghibli.android.films

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.remote.ghibli.android.R
import com.remote.ghibli.android.components.FilmListItem
import com.remote.ghibli.android.components.Snackbar
import com.remote.ghibli.usecases.models.ErrorTag
import com.remote.ghibli.usecases.models.FilmUiModel
import com.remote.ghibli.usecases.models.FilterUiModel
import kotlinx.coroutines.launch

@Composable
fun FilmListView(
    viewModel: FilmListViewModel = viewModel(),
    navigateToDetail: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showFilterList by remember { mutableStateOf(false) }
    val feedback by viewModel.feedback.collectAsStateWithLifecycle(null)

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    val errorMessage = when (state.error?.tag) {
        ErrorTag.Favorite -> stringResource(R.string.no_favorites)
        ErrorTag.Api -> stringResource(R.string.no_films)
        null -> null
    }

    val showReloadButton = state.error?.tag == ErrorTag.Api

    val byFavorite = when (state.error?.tag) {
        ErrorTag.Favorite -> false
        ErrorTag.Api -> true
        null -> false
    }

    val viewFilmButtonText = when (state.error?.tag) {
        ErrorTag.Favorite -> stringResource(R.string.view_films)
        ErrorTag.Api -> stringResource(R.string.view_favorite_films)
        null -> null
    }

    FilmListView(
        showFilterList = showFilterList,
        onFilterClick = { boolean -> showFilterList = boolean },
        filters = state.filters,
        onToggleFilter = { filter -> scope.launch { viewModel.toggleFilter(filter) } },
        isLoading = state.isLoading,
        errorMessage = errorMessage,
        showReloadButton = showReloadButton,
        reloadButtonText = stringResource(R.string.reload),
        viewFilmByFavorite = byFavorite,
        viewFilmButtonText = viewFilmButtonText,
        films = state.films,
        onToggleFavorite = { id, newFavoriteValue ->
            scope.launch { viewModel.toggleFavorite(id, newFavoriteValue) }
        },
        onReloadClick = { scope.launch { viewModel.loadData() } },
        onViewFilmsClick = { boolean -> scope.launch { viewModel.viewFilms(boolean) } },
        onNavigateToDetail = { id -> navigateToDetail(id) },
    )

    feedback?.let { Snackbar(it) }
}

@Composable
fun FilmListView(
    showFilterList: Boolean,
    onFilterClick: (Boolean) -> Unit,
    filters: List<FilterUiModel>,
    onToggleFilter: (FilterUiModel) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    showReloadButton: Boolean,
    reloadButtonText: String,
    viewFilmByFavorite: Boolean,
    viewFilmButtonText: String?,
    films: List<FilmUiModel>,
    onToggleFavorite: (String, Boolean) -> Unit,
    onReloadClick: () -> Unit,
    onViewFilmsClick: (Boolean) -> Unit,
    onNavigateToDetail: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gihbli Films") },
                actions = {
                    Box {
                        IconButton(
                            onClick = { onFilterClick(true) },
                            modifier = Modifier.testTag("filter_menu_icon"),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_filter_alt_24),
                                contentDescription = stringResource(R.string.content_description_filter),
                                tint = Color.White,
                            )
                        }

                        DropdownMenu(
                            expanded = showFilterList,
                            onDismissRequest = { onFilterClick(false) },
                        ) {
                            filters.forEach { filter ->
                                DropdownMenuItem(onClick = { onToggleFilter(filter) }) {
                                    Checkbox(
                                        checked = filter.isSelected,
                                        onCheckedChange = { onToggleFilter(filter) },
                                        modifier = Modifier.testTag(filter.name)
                                    )
                                    Text(filter.name)
                                }
                            }
                        }
                    }
                }
            )
        },
        backgroundColor = Color.Black.copy(alpha = 0.03f)
    ) { insets ->
        Crossfade(
            targetState = isLoading,
            label = "list",
            modifier = Modifier.padding(insets)
        ) { isLoading ->
            when {
                isLoading ->
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                    ) {
                        CircularProgressIndicator(
                            Modifier
                                .align(Alignment.Center)
                                .testTag("loading_indicator"),
                        )
                    }


                errorMessage != null ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(errorMessage, textAlign = TextAlign.Center)

                        if (showReloadButton) {
                            OutlinedButton(
                                onClick = { onReloadClick() },
                                modifier = Modifier.padding(8.dp),
                            ) {
                                Text(reloadButtonText)
                            }
                        }

                        if (viewFilmButtonText != null) {
                            Button(
                                onClick = { onViewFilmsClick(viewFilmByFavorite) },
                                modifier = Modifier.padding(8.dp),
                            ) {
                                Text(viewFilmButtonText)
                            }
                        }
                    }

                else ->
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        contentPadding = PaddingValues(16.dp),
                    ) {
                        items(films, key = FilmUiModel::id) {
                            FilmListItem(
                                title = it.title,
                                description = it.description,
                                favoriteIcon = if (it.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                favoriteTintColor = if (it.isFavorite) Color.Red else Color.Unspecified,
                                onFavoriteClick = { onToggleFavorite(it.id, !it.isFavorite) },
                                modifier = Modifier
                                    .clickable { onNavigateToDetail(it.id) }
                                    .semantics { stateDescription = it.isFavorite.toString() }
                            )
                        }
                    }
            }
        }
    }
}