package com.mangala.wallet.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val highlightGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF3B90FF),
        Color(0xFFC27DFF)
    )
)

val highlightGradientDisabled = Brush.linearGradient(
    colors = listOf(
        Color(0xFF3B90FF).copy(alpha = 0.2f),
        Color(0xFFC27DFF).copy(alpha = 0.2f)
    )
)

fun lightMangalaColors(): MangalaColors = MangalaColors(
    bg = Color(0xFFF5F5F5),
    bgInnerCard = Color(0xFFFFFFFF),
    bgAlpha = Color(0x80FFFFFF),
    bgHighlight = highlightGradient,
    bgHighlightDisabled = highlightGradientDisabled,
    bgBadge = Color(0xFFEDE7F8),
    bgButton = Color(0xFFE5E8F0),
    bgSwipeAction = Color(0xFFE7E7E7),
    skeletonBase = Color(0xFFE5E5E5),
    skeletonShimmer = Color(0xFFF6F6F6),
    buttonDestructiveContainer = Color(0xFFFF2323),
    buttonDestructiveContent = Color(0xFFFFFFFF),
    buttonNeutralContainer = Color(0xFF4F4F4F),
    buttonNeutralContent = Color(0xFFFFFFFF),
    border = Color(0xFFD2D6E0),
    borderHighlight = highlightGradient,
    textPrimary = Color(0xFF262626),
    textSecondary = Color(0xFF6D6D6D),
    textLink = Color(0xFF3B90FF),
    textOnBadge = Color(0xFF4D0570),
    iconPrimary = Color(0xFF262626),
    iconSecondary = Color(0xFFB0B0B0),
    iconTertiary = Color(0xFF6D6D6D),
    circleGradientBackgroundEnabled = false,
    bgTagLight = Color(0xFFEFD7FB),  // Light purple background for tags
    textTag = Color(0xFF4D0570),      // Dark purple text for tags
    iconFavoriteStar = Color(0xFFFFC107)  // Yellow/amber color for favorite star
)