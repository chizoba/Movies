package com.remote.ghibli.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.remote.ghibli.android.films.FilmDetailView
import com.remote.ghibli.android.films.FilmListView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Box {

                        var showDetail by rememberSaveable { mutableStateOf(false) }
                        var showDetailSlug: String? by rememberSaveable { mutableStateOf(null) }
                        FilmListView(
                            navigateToDetail = {
                                showDetailSlug = it
                                showDetail = true
                            }
                        )

                        AnimatedVisibility(
                            visible = showDetail,
                            enter = slideInHorizontally { it / 2 } + fadeIn(),
                            exit = slideOutHorizontally { it / 2 } + fadeOut(),
                        ) {
                            showDetailSlug?.let {
                                FilmDetailView(
                                    slug = it,
                                    onBack = { showDetail = false }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!")
    }
}

