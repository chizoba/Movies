package com.remote.ghibli.android.films

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.remote.ghibli.android.R
import com.remote.ghibli.android.components.Snackbar
import kotlinx.coroutines.launch

@Composable
fun FilmDetailView(
    slug: String,
    onBack: () -> Unit,
    viewModel: FilmDetailViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val feedback by viewModel.feedback.collectAsStateWithLifecycle(null)

    BackHandler {
        onBack()
    }

    LaunchedEffect(Unit) {
        viewModel.loadData(slug)
    }

    FilmDetailView(
        title = state.title,
        isLoading = state.isLoading,
        error = state.errorMessage,
        description = state.film?.description.orEmpty(),
        director = state.film?.director.orEmpty(),
        producer = state.film?.producer.orEmpty(),
        releaseDate = state.film?.releaseDate.orEmpty(),
        runningTime = state.film?.runningTime.orEmpty(),
        rtScore = state.film?.rtScore.orEmpty(),
        people = state.film?.people.orEmpty(),
        isFavorite = state.film?.isFavorite ?: false,
        onFavoriteClick = {
            scope.launch {
                state.film?.let {
                    viewModel.toggleFavorite(it.id, !it.isFavorite)
                }
            }
        },
        onBack = onBack
    )

    feedback?.let { Snackbar(it) }
}

@Composable
fun FilmDetailView(
    title: String,
    isLoading: Boolean,
    error: String?,
    description: String,
    director: String,
    producer: String,
    releaseDate: String,
    runningTime: String,
    rtScore: String,
    people: List<String>,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = stringResource(R.string.content_description_favorite),
                            tint = if (isFavorite) Color.Red else Color.White,
                            modifier = Modifier.semantics { stateDescription = isFavorite.toString() }
                        )
                    }
                },
            )
        },
    ) { insets ->
        Crossfade(targetState = isLoading, label = "detail") { isLoading ->
            when {
                isLoading ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        CircularProgressIndicator(Modifier.testTag("loading_indicator"))
                    }

                error != null ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(16.dp),
                        )
                    }

                else ->
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .padding(insets)
                            .padding(16.dp)
                            .testTag("list")
                    ) {
                        item(key = "Title") {
                            DetailSection(
                                title = "Title",
                                description = title
                            )
                        }
                        item(key = "Description") {
                            DetailSection(
                                title = "Description",
                                description = description
                            )
                        }
                        item(key = "Director") {
                            DetailSection(
                                title = "Director",
                                description = director
                            )
                        }
                        item(key = "Producer") {
                            DetailSection(
                                title = "Producer",
                                description = producer
                            )
                        }
                        item(key = "Release Date") {
                            DetailSection(
                                title = "Release Date",
                                description = releaseDate
                            )
                        }
                        item(key = "Running Time") {
                            DetailSection(
                                title = "Running Time",
                                description = runningTime
                            )
                        }
                        item(key = "RT Score") {
                            DetailSection(
                                title = "RT Score",
                                description = rtScore
                            )
                        }
                        item(key = "People") {
                            Header("People")
                        }
                        items(items = people, key = { it }) {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.body1,
                            )
                        }
                        item(key = "Divider") {
                            Divider()
                        }
                    }
            }
        }
    }
}

@Composable
fun DetailSection(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Header(title)
        Spacer(Modifier.size(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
fun Header(
    title: String,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.body1,
        fontWeight = FontWeight.SemiBold,
    )
}

@Preview
@Composable
fun FilmDetailView_Preview() {
    FilmDetailView(
        title = "Castle in the Sky",
        isLoading = false,
        error = null,
        description = "The orphan Sheeta inherited a mysterious crystal that links her to the " +
                "mythical sky-kingdom of Laputa. With the help of resourceful Pazu and a" +
                " rollicking band of sky pirates, she makes her way to the ruins of the" +
                " once-great civilization. Sheeta and Pazu must outwit the evil Muska, who" +
                " plans to use Laputa's science to make himself ruler of the world.",
        director = "Hayao Miyazaki",
        producer = "Isao Takahata",
        releaseDate = "1986",
        runningTime = "124",
        rtScore = "95",
        people = listOf("Bruno Oliveira", "Luis Ramos", "Chizoba Ogbonna"),
        isFavorite = true,
        onFavoriteClick = {},
        onBack = {},
    )
}