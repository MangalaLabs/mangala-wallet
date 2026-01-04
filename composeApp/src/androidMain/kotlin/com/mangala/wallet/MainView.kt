package com.mangala.wallet

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mangala.wallet.viewmodel.ApplicationViewModel

@Composable
fun MainView(viewModel: ApplicationViewModel) {
    App(viewModel, modifier = Modifier.systemBarsPadding())
}