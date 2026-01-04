package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui

import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun ProposalInputLabel(label: String, requiredInput: Boolean) {
    MaxWidthRow {
        Text(
            text = label,
            style = MangalaTypography.Size13Medium(),
            color = MaterialTheme.mangalaColors.textSecondary
        )
        if (requiredInput) {
            Text(
                text = "*",
                style = MangalaTypography.Size13Medium(),
                modifier = Modifier.align(Alignment.CenterVertically),
                color = MaterialTheme.mangalaColors.buttonDestructiveContainer
            )
        }
    }
}