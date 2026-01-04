package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun ProposalTopAppBar(
    modifier: Modifier = Modifier.background(MaterialTheme.mangalaColors.bgInnerCard),
    label: String,
    onBackPressed: () -> Unit,
    trailingIcon: @Composable () -> Unit = {}
) {
    MangalaWalletTopBarCenteredTitle(
        title = label,
        onBackClicked = onBackPressed,
        modifier = modifier.statusBarsPadding(),
        trailingButton = trailingIcon
    )
}