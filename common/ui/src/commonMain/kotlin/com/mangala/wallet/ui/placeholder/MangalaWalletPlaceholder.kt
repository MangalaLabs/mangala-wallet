package com.mangala.wallet.ui.placeholder

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun Modifier.mangalaWalletPlaceholder(
    visible: Boolean,
    color: Color = MaterialTheme.mangalaColors.skeletonBase,
    highlight: PlaceholderHighlight = PlaceholderHighlight.shimmer(highlightColor = MaterialTheme.mangalaColors.skeletonShimmer),
    // optional, defaults to RectangleShape
    shape: Shape = RoundedCornerShape(4.dp),
    modifier: Modifier = Modifier, // to control size of placeholder
) = if (visible) Modifier.then(modifier.placeholder(visible, color, shape, highlight)) else Modifier

@Composable
fun Modifier.mangalaWalletPlaceholder(
    visible: Boolean,
    highlightColor: Color,
    color: Color = MaterialTheme.mangalaColors.skeletonBase,
    shape: Shape = RoundedCornerShape(4.dp),
    modifier: Modifier = Modifier, // to control size of placeholder
) =
    if (visible)
        Modifier.then(
            modifier.placeholder(
                visible = visible,
                color = color,
                shape = shape,
                highlight = PlaceholderHighlight.shimmer(highlightColor = highlightColor),
            ))
    else Modifier
