package com.mangala.wallet.ui

import androidx.compose.runtime.Composable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlin.time.Duration

@Composable
fun Toast(message: String, duration: Long = 2000L) {
    var isVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(duration)
        isVisible = false
    }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .background(
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.9f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(8.dp)
            .widthIn(min = 200.dp)
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = message,
                color = MaterialTheme.colors.background
            )
        }
    }
}
