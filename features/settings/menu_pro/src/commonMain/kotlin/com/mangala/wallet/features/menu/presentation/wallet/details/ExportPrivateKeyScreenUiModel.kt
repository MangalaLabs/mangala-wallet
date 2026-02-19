package com.mangala.wallet.features.menu.presentation.wallet.details

data class ExportPrivateKeyScreenUiModel(
    val privateKey: String = "",
    val isPrivateKeyVisible: Boolean = false,
    val isLoading: Boolean = true,
    val isError: Boolean = false
)
