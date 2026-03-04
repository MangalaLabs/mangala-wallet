package com.mangala.wallet.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Theme constants for Wallet V2 following the onboarding design system
 */
object WalletThemeV2 {

    object Colors {
        // Background colors
        val primaryBackground = Color(0xFF0A0E1A)
        val secondaryBackground = Color(0xFF1A1A1A)
        val cardBackground = Color(0xFF1D263E)
        val cardBorder = Color.White.copy(alpha = 0.05f)
        val divider = Color.White.copy(alpha = 0.03f)

        // Text colors
        val primaryText = Color(0xFFF1F5F9)
        val secondaryText = Color(0xFFA5B4CB)
        val tertiaryText = Color(0xFF6B7280)
        val captionText = Color(0xFF64748B)

        // Accent colors
        val accentBlue = Color(0xFF3B90FF)
        val accentPurple = Color(0xFFC27DFF)
        val purple = Color(0xFF8647F3)

        // Status colors
        val success = Color(0xFF10B981)
        val error = Color(0xFFEF4444)
        val warning = Color(0xFFF9A207)

        // PnL colors - muted tones with black for better UX
        val positiveGain = Color(0xFF4A9B3E)     // Muted green with black
        val negativeLoss = Color(0xFFD65A5A)     // Muted red with black

        // Gradient colors
        val gradientStart = accentBlue
        val gradientEnd = accentPurple

        // Network specific
        val antelopeAccent = Color(0xFF007AFF)
        val bitcoinAccent = Color(0xFFF7931A)
        val evmAccent = Color(0xFF627EEA)

        // Chart colors
        val positiveChange = Color(0xFF00FF88)
        val negativeChange = Color(0xFFFF4444)
        val neutralChange = Color(0xFF888888)
    }

    object Dimensions {
        // Padding
        val paddingSmall = 8.dp
        val paddingMedium = 16.dp
        val paddingLarge = 24.dp
        val paddingXLarge = 32.dp

        // Component heights
        val buttonHeight = 52.dp
        val inputHeight = 56.dp
        val headerHeight = 60.dp
        val balanceCardHeight = 180.dp
        val quickActionHeight = 100.dp
        val tokenItemHeight = 72.dp

        // Corner radius
        val cornerRadiusSmall = 8.dp
        val cornerRadiusMedium = 12.dp
        val cornerRadiusLarge = 16.dp
        val cornerRadiusButton = 1000.dp // Pill shape

        // Icon sizes
        val iconSizeSmall = 16.dp
        val iconSizeMedium = 20.dp
        val iconSizeLarge = 24.dp
        val iconSizeXLarge = 40.dp

        // Spacing
        val spacingXSmall = 4.dp
        val spacingSmall = 8.dp
        val spacingMedium = 12.dp
        val spacingLarge = 16.dp
        val spacingXLarge = 24.dp
    }

    object Typography {
        // Font sizes
        val fontSizeCaption = 11.sp
        val fontSizeSmall = 12.sp
        val fontSizeBody = 14.sp
        val fontSizeMedium = 16.sp
        val fontSizeLarge = 17.sp
        val fontSizeTitle = 20.sp
        val fontSizeHeader = 24.sp
        val fontSizeBalance = 36.sp

        // Letter spacing
        val letterSpacingTight = (-0.2).sp
        val letterSpacingNormal = (-0.14).sp
        val letterSpacingLoose = (-0.12).sp

        // Line heights
        val lineHeightSmall = 16.sp
        val lineHeightBody = 19.6.sp
        val lineHeightTitle = 28.sp
        val lineHeightBalance = 44.sp
    }

    object Animation {
        // Durations
        val durationShort = 200
        val durationMedium = 300
        val durationLong = 600
        val durationExtraLong = 800

        // Delays for staggered animations
        val delayStep1 = 0
        val delayStep2 = 200
        val delayStep3 = 400
        val delayStep4 = 600
        val delayStep5 = 800
    }
}