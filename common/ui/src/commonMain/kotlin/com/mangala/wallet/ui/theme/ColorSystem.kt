package com.mangala.wallet.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Immutable
data class MangalaColors(
    val bg: Color,
    val bgInnerCard: Color, 
    val bgAlpha: Color,
    val bgHighlight: Brush,
    val bgHighlightDisabled: Brush,
    val bgBadge: Color,
    val bgButton: Color,
    val bgSwipeAction: Color,
    val skeletonBase: Color,
    val skeletonShimmer: Color,
    val buttonDestructiveContainer: Color,
    val buttonDestructiveContent: Color,
    val buttonNeutralContainer: Color,
    val buttonNeutralContent: Color,
    val border: Color,
    val borderHighlight: Brush,
    val textPrimary: Color,
    val textSecondary: Color,
    val textLink: Color,
    val textOnBadge: Color,
    val iconPrimary: Color,
    val iconSecondary: Color,
    val iconTertiary: Color,
    val circleGradientBackgroundEnabled: Boolean,
    val bgTagLight: Color,
    val textTag: Color,
    val iconFavoriteStar: Color
)