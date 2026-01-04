package com.mangala.wallet.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun animateText(
    offset: Animatable<Float, AnimationVector1D>,
    coroutineScope: CoroutineScope,
) {
    val shakeKeyframes: AnimationSpec<Float> = keyframes {
        durationMillis = 3000
        val easing = FastOutLinearInEasing

        // generate 8 keyframes
        for (i in 1..8) {
            val x = when (i % 3) {
                0 -> 4f
                1 -> -4f
                else -> 0f
            }
            x at durationMillis / 80 * i with easing
        }
    }

    coroutineScope.launch {
        offset.animateTo(
            targetValue = 0f,
            animationSpec = shakeKeyframes,
        )
    }
}