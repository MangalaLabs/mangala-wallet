package com.mangala.wallet.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Onboarding-style button that delegates to [MangalaGradientButton].
 * Uses [MangalaButtonStyle.GRADIENT] for primary and [MangalaButtonStyle.TRANSPARENT] for secondary.
 */
@Composable
fun OnboardingButton(
    text: String,
    onClick: () -> Unit,
    isPrimary: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    MangalaGradientButton(
        label = text,
        onClick = onClick,
        enabled = enabled,
        size = MangalaButtonSize.Big,
        buttonStyle = if (isPrimary) MangalaButtonStyle.GRADIENT else MangalaButtonStyle.TRANSPARENT,
        modifier = modifier
    )
}
