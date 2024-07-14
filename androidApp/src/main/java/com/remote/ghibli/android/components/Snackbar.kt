package com.remote.ghibli.android.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.remote.ghibli.android.R
import com.remote.ghibli.usecases.models.ErrorTag

@Composable
fun Snackbar(
    feedback: SnackbarFeedback,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val message = when (feedback.tag) {
        ErrorTag.Favorite -> stringResource(R.string.mark_favorite_error)
        else -> stringResource(R.string.error_unknown)
    }

    Box(modifier = modifier.fillMaxSize()) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .systemBarsPadding(),
        )

        LaunchedEffect(feedback) {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short,
            )
        }
    }
}

data class SnackbarFeedback(
    val id: Long = System.currentTimeMillis(),
    val tag: ErrorTag,
)
