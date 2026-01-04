package com.mangala.wallet.ui.theme

import androidx.compose.ui.graphics.Color

fun darkMangalaColors(): MangalaColors = MangalaColors(
    bg = Color(0xFF0A0E1A),
    bgInnerCard = Color(0xFF1D263E),
    bgAlpha = Color(0x4D1C2526),
    bgHighlight = highlightGradient,
    bgHighlightDisabled = highlightGradientDisabled,
    bgBadge = Color(0xFF8647F3),
    bgButton = Color(0xFF343F5A),
    bgSwipeAction = Color(0xFF3B4A6B),
    skeletonBase = Color(0xFF2A3441),
    skeletonShimmer = Color(0xFF4A5768),
    buttonDestructiveContainer = Color(0xFFFF2323),
    buttonDestructiveContent = Color(0xFFFFFFFF),
    buttonNeutralContainer = Color(0xFF2E3441),
    buttonNeutralContent = Color(0xFFFFFFFF),
    border = Color(0xFF2A3E6C),
    borderHighlight = highlightGradient,
    textPrimary = Color(0xFFF1F5F9),
    textSecondary = Color(0xFFA5B4CB),
    textLink = Color(0xFF3B90FF),
    textOnBadge = Color(0xFFEDE7F8),
    iconPrimary = Color(0xFFF1F5F9),
    iconSecondary = Color(0xFFB0B0B0),
    iconTertiary = Color(0xFFE2E8F0),
    circleGradientBackgroundEnabled = true,
    textTag = Color(0xFF3A2B47),  // Dark purple text for tags in dark mode
    bgTagLight = Color(0xFFD4B5F7),      // Light purple background for tags in dark mode
    iconFavoriteStar = Color(0xFFFFC107)  // Brighter yellow/amber color for dark mode
)