package com.mangala.features.wallet.presentationv2.evm.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowCircleDown
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Navigate
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.highlightGradientDisabled
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EVMAddAccountBottomSheet(
    onAddMoreAccount: () -> Unit,
    onCreateNewWallet: () -> Unit,
    onImportWallet: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors = MaterialTheme.mangalaColors

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.bgInnerCard,
        contentColor = colors.textPrimary,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        colors.textSecondary.copy(alpha = 0.3f),
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = stringResource(MR.strings.title_evm_add_account),
                style = MangalaTypography.Size17SemiBold(),
                color = colors.textPrimary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AddAccountOption(
                    icon = Icons.Default.Add,
                    title = stringResource(MR.strings.title_evm_add_account_option_add_more),
                    description = stringResource(MR.strings.description_evm_add_account_option_add_more),
                    onClick = {
                        onDismiss()
                        onAddMoreAccount()
                    }
                )

                AddAccountOption(
                    icon = Icons.Default.AccountCircle,
                    title = stringResource(MR.strings.title_evm_add_account_option_create),
                    description = stringResource(MR.strings.description_evm_add_account_option_create),
                    onClick = {
                        onDismiss()
                        onCreateNewWallet()
                    }
                )

                AddAccountOption(
                    icon = MangalaWalletPack.ArrowCircleDown,
                    title = stringResource(MR.strings.title_evm_add_account_option_import),
                    description = stringResource(MR.strings.description_evm_add_account_option_import),
                    onClick = {
                        onDismiss()
                        onImportWallet()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AddAccountOption(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.mangalaColors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = colors.border,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(highlightGradientDisabled),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.textLink,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MangalaTypography.Size14SemiBold(),
                    color = colors.textPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = MangalaTypography.Size12Regular(),
                    color = colors.textSecondary
                )
            }

            Icon(
                imageVector = MangalaWalletPack.Navigate,
                contentDescription = null,
                tint = colors.iconSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
