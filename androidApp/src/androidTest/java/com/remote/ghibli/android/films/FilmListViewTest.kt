package com.remote.ghibli.android.films

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasStateDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.remote.ghibli.repository.models.FilterType
import com.remote.ghibli.usecases.models.FilmUiModel
import com.remote.ghibli.usecases.models.FilterUiModel
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class FilmListViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dataLoadingState() {
        composeTestRule.setContent {
            FilmListView(
                showFilterList = false,
                onFilterClick = {},
                filters = emptyList(),
                onToggleFilter = {},
                isLoading = true,
                errorMessage = null,
                showReloadButton = false,
                reloadButtonText = "",
                viewFilmByFavorite = false,
                viewFilmButtonText = null,
                films = emptyList(),
                onToggleFavorite = { _, _ -> },
                onReloadClick = {},
                onViewFilmsClick = {},
                onNavigateToDetail = {},
            )
        }

        composeTestRule.onNode(hasTestTag("loading_indicator")).assertIsDisplayed()
    }

    @Test
    fun dataErrorState_noFilms() {
        val errorMessage = "You have no favorites."
        val showReloadButton = true
        val reloadButtonText = "Reload"
        val viewFilmByFavorite = true
        val viewFilmButtonText = "View favorite films"

        composeTestRule.setContent {
            FilmListView(
                showFilterList = false,
                onFilterClick = {},
                filters = emptyList(),
                onToggleFilter = {},
                isLoading = false,
                errorMessage = errorMessage,
                showReloadButton = showReloadButton,
                reloadButtonText = reloadButtonText,
                viewFilmByFavorite = viewFilmByFavorite,
                viewFilmButtonText = viewFilmButtonText,
                films = emptyList(),
                onToggleFavorite = { _, _ -> },
                onReloadClick = {},
                onViewFilmsClick = {},
                onNavigateToDetail = {},
            )
        }

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithText(reloadButtonText).assertIsDisplayed()
        composeTestRule.onNodeWithText(viewFilmButtonText).assertIsDisplayed()
    }

    @Test
    fun dataErrorState_noFavoriteFilms() {
        val errorMessage = "You have no favorites."
        val showReloadButton = false
        val reloadButtonText = ""
        val viewFilmByFavorite = false
        val viewFilmButtonText = "View films"

        composeTestRule.setContent {
            FilmListView(
                showFilterList = false,
                onFilterClick = {},
                filters = emptyList(),
                onToggleFilter = {},
                isLoading = false,
                errorMessage = errorMessage,
                showReloadButton = showReloadButton,
                reloadButtonText = reloadButtonText,
                viewFilmByFavorite = viewFilmByFavorite,
                viewFilmButtonText = viewFilmButtonText,
                films = emptyList(),
                onToggleFavorite = { _, _ -> },
                onReloadClick = {},
                onViewFilmsClick = {},
                onNavigateToDetail = {},
            )
        }

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun filmsDataLoadedState() {
        val filmUiModels = listOf(
            FilmUiModel(
                id = UUID.randomUUID().toString(),
                title = "My Neighbor Totoro",
                description = "-",
                releaseDate = "1988",
                isFavorite = false,
            ),
            FilmUiModel(
                id = UUID.randomUUID().toString(),
                title = "Princess Mononoke",
                description = "-",
                releaseDate = "1997",
                isFavorite = false,
            ),
        )

        composeTestRule.setContent {
            FilmListView(
                showFilterList = false,
                onFilterClick = {},
                filters = emptyList(),
                onToggleFilter = {},
                isLoading = false,
                errorMessage = null,
                showReloadButton = false,
                reloadButtonText = "",
                viewFilmByFavorite = false,
                viewFilmButtonText = null,
                films = filmUiModels,
                onToggleFavorite = { _, _ -> },
                onReloadClick = {},
                onViewFilmsClick = {},
                onNavigateToDetail = {},
            )
        }

        filmUiModels.forEach { item ->
            composeTestRule.onNodeWithText(item.title).assertIsDisplayed()
        }
    }

    @Test
    fun filtersDataLoadedState() {
        val specieName = "God"

        val filterUiModels = listOf(
            FilterUiModel(
                id = UUID.randomUUID().toString(),
                name = specieName,
                isSelected = false,
                type = FilterType.Specie,
            ),
        )

        var showFilterList by mutableStateOf(false)

        composeTestRule.setContent {
            FilmListView(
                showFilterList = showFilterList,
                onFilterClick = { showFilterList = it },
                filters = filterUiModels,
                onToggleFilter = {},
                isLoading = false,
                errorMessage = null,
                showReloadButton = false,
                reloadButtonText = "",
                viewFilmByFavorite = false,
                viewFilmButtonText = null,
                films = emptyList(),
                onToggleFavorite = { _, _ -> },
                onReloadClick = {},
                onViewFilmsClick = {},
                onNavigateToDetail = {},
            )
        }

        composeTestRule.onNodeWithTag("filter_menu_icon").performClick()
        composeTestRule.onNodeWithText(specieName).assertIsDisplayed()
    }

    @Test
    fun filtersDataLoadedState_performFilterCheck_showsFilteredFilms() {
        val filmUiModels = mutableStateListOf(
            FilmUiModel(
                id = UUID.randomUUID().toString(),
                title = "My Neighbor Totoro",
                description = "-",
                releaseDate = "1988",
                isFavorite = false,
            ),
            FilmUiModel(
                id = UUID.randomUUID().toString(),
                title = "Princess Mononoke",
                description = "-",
                releaseDate = "1997",
                isFavorite = false,
            ),
        )

        val specieName = "God"

        val filterUiModels = mutableStateListOf(
            FilterUiModel(
                id = UUID.randomUUID().toString(),
                name = specieName,
                isSelected = false,
                type = FilterType.Specie,
            )
        )

        var showFilterList by mutableStateOf(false)
        composeTestRule.setContent {

            FilmListView(
                showFilterList = showFilterList,
                onFilterClick = { showFilterList = it },
                filters = filterUiModels,
                onToggleFilter = {
                    val updatedFilter = it.copy(isSelected = true)
                    filterUiModels.apply {
                        remove(it)
                        add(updatedFilter)
                    }
                    filmUiModels.remove(filmUiModels.first())
                },
                isLoading = false,
                errorMessage = null,
                showReloadButton = false,
                reloadButtonText = "",
                viewFilmByFavorite = false,
                viewFilmButtonText = null,
                films = filmUiModels,
                onToggleFavorite = { _, _ -> },
                onReloadClick = {},
                onViewFilmsClick = {},
                onNavigateToDetail = {},
            )
        }

        composeTestRule.onNodeWithTag("filter_menu_icon").performClick()
        composeTestRule.onNodeWithTag(specieName).performClick()
        composeTestRule.onNodeWithTag(specieName).assertIsOn()
        composeTestRule.onNodeWithTag("filter_menu_icon").performClick()
        composeTestRule.onNodeWithText("My Neighbor Totoro").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("Princess Mononoke").assertIsDisplayed()
    }

    @Test
    fun dataLoadedState_performFavoriteClick_showsMarkedAsFavorite() {
        val filmUiModels = mutableStateListOf(
            FilmUiModel(
                id = UUID.randomUUID().toString(),
                title = "My Neighbor Totoro",
                description = "-",
                releaseDate = "1988",
                isFavorite = false,
            ),
        )

        composeTestRule.setContent {
            FilmListView(
                showFilterList = false,
                onFilterClick = {},
                filters = emptyList(),
                onToggleFilter = {},
                isLoading = false,
                errorMessage = null,
                showReloadButton = false,
                reloadButtonText = "",
                viewFilmByFavorite = false,
                viewFilmButtonText = null,
                films = filmUiModels,
                onToggleFavorite = { id, newFavoriteValue ->
                    val filterUiModel = filmUiModels.first { id == it.id }
                    val updatedFilterUiModel = filterUiModel.copy(isFavorite = newFavoriteValue)
                    filmUiModels.apply {
                        remove(filterUiModel)
                        add(updatedFilterUiModel)
                    }
                },
                onReloadClick = {},
                onViewFilmsClick = {},
                onNavigateToDetail = {},
            )
        }

        composeTestRule.onNodeWithContentDescription("Favorite").performClick()
        composeTestRule.onNode(hasStateDescription("true")).assertExists()
    }

    @Test
    fun dataLoadedState_performFavoriteClick_showsNotMarkedAsFavorite() {
        val filmUiModels = mutableStateListOf(
            FilmUiModel(
                id = UUID.randomUUID().toString(),
                title = "My Neighbor Totoro",
                description = "-",
                releaseDate = "1988",
                isFavorite = true,
            ),
        )

        composeTestRule.setContent {
            FilmListView(
                showFilterList = false,
                onFilterClick = {},
                filters = emptyList(),
                onToggleFilter = {},
                isLoading = false,
                errorMessage = null,
                showReloadButton = false,
                reloadButtonText = "",
                viewFilmByFavorite = false,
                viewFilmButtonText = null,
                films = filmUiModels,
                onToggleFavorite = { id, newFavoriteValue ->
                    val filterUiModel = filmUiModels.first { id == it.id }
                    val updatedFilterUiModel = filterUiModel.copy(isFavorite = newFavoriteValue)
                    filmUiModels.apply {
                        remove(filterUiModel)
                        add(updatedFilterUiModel)
                    }
                },
                onReloadClick = {},
                onViewFilmsClick = {},
                onNavigateToDetail = {},
            )
        }

        composeTestRule.onNodeWithContentDescription("Favorite").performClick()
        composeTestRule.onNode(hasStateDescription("false")).assertExists()
    }
}
