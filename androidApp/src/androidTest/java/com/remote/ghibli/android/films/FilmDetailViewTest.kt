package com.remote.ghibli.android.films

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasStateDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import org.junit.Rule
import org.junit.Test

class FilmDetailViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dataLoadingState() {
        composeTestRule.setContent {
            FilmDetailView(
                title = "",
                isLoading = true,
                error = null,
                description = "",
                director = "",
                producer = "",
                releaseDate = "",
                runningTime = "",
                rtScore = "",
                people = emptyList(),
                isFavorite = false,
                onFavoriteClick = {},
                onBack = {},
            )
        }

        composeTestRule.onNode(hasTestTag("loading_indicator")).assertIsDisplayed()
    }

    @Test
    fun dataErrorState() {
        val errorMessage = "Something went wrong. Please try again."
        val isFavorite = false

        composeTestRule.setContent {
            FilmDetailView(
                title = "",
                isLoading = false,
                error = errorMessage,
                description = "",
                director = "",
                producer = "",
                releaseDate = "",
                runningTime = "",
                rtScore = "",
                people = emptyList(),
                isFavorite = isFavorite,
                onFavoriteClick = {},
                onBack = {},
            )
        }

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNode(hasStateDescription("$isFavorite")).assertExists()
    }

    @Test
    fun dataLoadedState() {
        composeTestRule.setContent {
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

        composeTestRule.onNodeWithText("Hayao Miyazaki").assertIsDisplayed()
        composeTestRule.onNodeWithTag("list").performTouchInput { swipeUp() }
        composeTestRule.onNodeWithText("Luis Ramos").assertIsDisplayed()
    }

    @Test
    fun dataLoadedState_showsMarkedAsFavorite() {
        val isFavorite = true

        composeTestRule.setContent {
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
                isFavorite = isFavorite,
                onFavoriteClick = {},
                onBack = {},
            )
        }

        composeTestRule.onNode(hasStateDescription("$isFavorite")).assertExists()
    }

    @Test
    fun dataLoadedState_performFavoriteClick_showsMarkedAsFavorite() {
        var isFavorite by mutableStateOf(false)

        composeTestRule.setContent {
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
                isFavorite = isFavorite,
                onFavoriteClick = { isFavorite = !isFavorite },
                onBack = {},
            )
        }

        composeTestRule.onNodeWithContentDescription("Favorite").performClick()
        composeTestRule.onNode(hasStateDescription("$isFavorite")).assertExists()
    }

    @Test
    fun dataLoadedState_showsNotMarkedAsFavorite() {
        val isFavorite = false

        composeTestRule.setContent {
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
                isFavorite = isFavorite,
                onFavoriteClick = {},
                onBack = {},
            )
        }

        composeTestRule.onNode(hasStateDescription("$isFavorite")).assertExists()
    }

    @Test
    fun dataLoadedState_performFavoriteClick_showsNotMarkedAsFavorite() {
        var isFavorite by mutableStateOf(true)

        composeTestRule.setContent {
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
                isFavorite = isFavorite,
                onFavoriteClick = { isFavorite = !isFavorite },
                onBack = {},
            )
        }

        composeTestRule.onNodeWithContentDescription("Favorite").performClick()
        composeTestRule.onNode(hasStateDescription("$isFavorite")).assertExists()
    }
}