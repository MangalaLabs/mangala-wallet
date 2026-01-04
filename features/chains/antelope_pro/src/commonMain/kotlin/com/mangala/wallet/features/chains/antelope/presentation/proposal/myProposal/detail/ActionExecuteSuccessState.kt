package com.mangala.wallet.features.chains.antelope.presentation.proposal.myProposal.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.component.ExecuteTransactionSuccess
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaTextButton
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun ActionExecuteSuccessState(
    action: String,
    onContinueTransaction: () -> Unit,
    onBackHome: () -> Unit
) {
    MaxSizeBox(
        modifier = Modifier.background(MaterialTheme.mangalaColors.bg)
    ) {
        ExecuteTransactionSuccess(
            onClickBack = {},
            textTitle = "$action successfully",
        ) {
            MangalaGradientButton(
                label = "Continue",
                onClick = onContinueTransaction,
                enabled = true,
                modifier = Modifier.fillMaxWidth()
            )

            VerticalSpacer(Spacing.SMALL)

            MangalaTextButton(
                modifier = Modifier.fillMaxWidth(),
                label = "Back to Home",
                onClick = onBackHome,
                size = MangalaButtonSize.Big,
                style = MangalaTypography.Size14Medium()
            )
        }
    }
}