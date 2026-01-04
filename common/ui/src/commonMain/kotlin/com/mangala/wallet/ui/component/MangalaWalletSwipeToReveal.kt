package com.mangala.wallet.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.TextButton
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.ui.TextDescription2
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MangalaSwipeRevealContainer(
    state: AnchoredDraggableState<Int>,
    revealedWidth: Float,
    revealContent: @Composable BoxScope.() -> Unit,
    mainContent: @Composable BoxScope.() -> Unit,
) {
    Box {
        Box(modifier = Modifier.matchParentSize()) {
            MaxHeightBox(
                content = revealContent,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset {
                        IntOffset(
                            x = ((-state.requireOffset()) + revealedWidth).roundToInt(),
                            y = 0
                        )
                    }
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset {
                    IntOffset(
                        x = -state
                            .requireOffset()
                            .roundToInt(),
                        y = 0,
                    )
                }
                .anchoredDraggable(
                    state = state,
                    orientation = Orientation.Horizontal,
                    reverseDirection = true,
                ),
            content = mainContent
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberSwipeRevealState(
    revealedWidth: Float,
): AnchoredDraggableState<Int> {
    return remember(revealedWidth) {
        AnchoredDraggableState(
            initialValue = 0,
            anchors = DraggableAnchors {
                0 at 0f
                1 at revealedWidth
            },
        )
    }
}

@Deprecated(
    "Use MangalaSwipeRevealContainer with rememberSwipeRevealState instead",
    ReplaceWith(
        "MangalaSwipeRevealContainer(state = rememberSwipeRevealState(revealedWidth), revealedWidth = revealedWidth, revealContent = { /* your reveal content */ }, mainContent = { mainItemContent() })",
        "androidx.compose.foundation.gestures.AnchoredDraggableState"
    )
)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MangalaWalletSwipeToReveal(
    shape: Shape,
    revealedBackgroundColor: Color,
    text: String,
    revealedTextColor: Color = Color.White,
    onClickRevealed: () -> Unit,
    mainItemContent: @Composable () -> Unit,
) {
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val swipeSizeInPx = with(LocalDensity.current) { Dimensions.SwipeToRevealSwipeSize.toPx() }
    val anchors = mapOf(0f to 0, -swipeSizeInPx to 1)
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.swipeable(
            state = swipeableState,
            anchors = anchors,
            thresholds = { _, _ -> FractionalThreshold(0.5f) },
            orientation = Orientation.Horizontal
        ).clip(shape)
    ) {
        Box(modifier = Modifier.matchParentSize(), contentAlignment = Alignment.CenterEnd) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(.5f)
                    .background(revealedBackgroundColor),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(
                    modifier = Modifier.fillMaxHeight().width(Dimensions.SwipeToRevealSwipeSize),
                    onClick = {
                        onClickRevealed()
                        coroutineScope.launch { swipeableState.snapTo(0) }
                    },
                    enabled = swipeableState.offset.value != 0f
                ) {
                    TextDescription2(
                        text = text,
                        color = revealedTextColor,
                    )
                }
            }
        }

        Box(modifier = Modifier.offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }) {
            mainItemContent()
        }
    }
}