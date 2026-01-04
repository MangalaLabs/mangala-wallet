package com.mangala.wallet.features.chains.antelope.presentation.proposal

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.theme.MangalaTypography
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun ErrorPopup(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(
                text = MR.strings.title_proposal_details_error_popup.desc().localized(),
                color = MaterialTheme.mangalaColors.textPrimary,
                style = MangalaTypography.Size17SemiBold()
            )
        },
        text = {
            Text(
                text = message,
                color = MaterialTheme.mangalaColors.textPrimary,
                style = MangalaTypography.Size13Regular()
            )
        },
        confirmButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(
                    text = MR.strings.button_close_all.desc().localized(),
                    color = MaterialTheme.mangalaColors.textPrimary,
                    style = MangalaTypography.Size13Regular()
                )
            }
        }
    )
}