package com.mangala.wallet.features.onboarding.domain.model

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageResourceId: String? = null
)