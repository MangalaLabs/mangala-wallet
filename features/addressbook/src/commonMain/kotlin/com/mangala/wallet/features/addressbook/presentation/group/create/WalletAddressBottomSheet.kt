package com.mangala.wallet.features.addressbook.presentation.group.create

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class WalletSelectionBottomSheetScreen(
    private val viewModel: WalletAddressBottomSheetViewModel,
    private val initialSelectedWalletIds: List<String> = emptyList(),
    private val onDismiss: () -> Unit,
    private val onWalletsSelected: (List<String>) -> Unit,
    private val onSelectionChanged: ((List<String>) -> Unit)? = null, // Real-time selection changes
    private val onCopyClick: (Int) -> Unit,
    private val onQrCodeClick: (Int) -> Unit
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val uiState by viewModel.uiState.collectAsState()

        // Trigger sheet initialization with selected wallet IDs (mimic legacy behavior)
        LaunchedEffect(initialSelectedWalletIds) {
            viewModel.onSheetShown(initialSelectedWalletIds)
        }

        // Monitor selection changes and notify parent in real-time
        LaunchedEffect(uiState.selectedWalletAddressIds) {
            val currentSelection = uiState.selectedWalletAddressIds.toList()
            onSelectionChanged?.invoke(currentSelection)
        }

        WalletBottomSheetWithPagination(
            uiState = uiState,
            onDismiss = {
                onDismiss()
                bottomSheetNavigator.hide()
            },
            onSearchQueryChanged = { query ->
                viewModel.onSearchQueryChanged(query)
            },
            onWalletToggle = { walletId ->
                viewModel.toggleWalletAddressSelection(walletId)
            },
            onConfirm = {
                viewModel.confirmSelection()
                onDismiss()
                bottomSheetNavigator.hide()
            },
            onLoadMore = {
                viewModel.loadMoreWallets()
            },
            onCopyClick = onCopyClick,
            onQrCodeClick = onQrCodeClick
        )
    }
}