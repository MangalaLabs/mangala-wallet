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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.mangala.features.wallet.presentationv2.evm.model.EVMFilterOptions
import com.mangala.features.wallet.presentationv2.evm.model.EVMTokenSortBy
import com.mangala.features.wallet.presentationv2.evm.model.EVMViewMode
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.WalletThemeV2
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EVMFilterBottomSheet(
    currentOptions: EVMFilterOptions,
    onFilterOptionsChanged: (EVMFilterOptions) -> Unit,
    onDismiss: () -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val colors = MaterialTheme.mangalaColors

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
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
                text = stringResource(MR.strings.label_filter_and_sort),
                style = MangalaTypography.Size17SemiBold(),
                color = colors.textPrimary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            EVMFilterToggleItem(
                title = stringResource(MR.strings.label_hide_small_balances),
                isSelected = currentOptions.hideSmallBalances,
                onClick = {
                    onFilterOptionsChanged(
                        currentOptions.copy(hideSmallBalances = !currentOptions.hideSmallBalances)
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(MR.strings.label_sort_by),
                style = MangalaTypography.Size14SemiBold(),
                color = colors.textSecondary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            EVMFilterSelectableItem(
                title = stringResource(MR.strings.label_sort_by_name),
                isSelected = currentOptions.sortBy == EVMTokenSortBy.NAME,
                onClick = {
                    onFilterOptionsChanged(currentOptions.copy(sortBy = EVMTokenSortBy.NAME))
                }
            )

            EVMFilterSelectableItem(
                title = stringResource(MR.strings.label_sort_by_value),
                isSelected = currentOptions.sortBy == EVMTokenSortBy.VALUE,
                onClick = {
                    onFilterOptionsChanged(currentOptions.copy(sortBy = EVMTokenSortBy.VALUE))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(MR.strings.label_view_mode),
                style = MangalaTypography.Size14SemiBold(),
                color = colors.textSecondary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            EVMFilterSelectableItem(
                title = stringResource(MR.strings.label_view_mode_single_account),
                isSelected = currentOptions.viewMode == EVMViewMode.SINGLE_ACCOUNT,
                onClick = {
                    onFilterOptionsChanged(
                        currentOptions.copy(viewMode = EVMViewMode.SINGLE_ACCOUNT)
                    )
                }
            )

            EVMFilterSelectableItem(
                title = stringResource(MR.strings.label_view_mode_all_accounts),
                isSelected = currentOptions.viewMode == EVMViewMode.ALL_ACCOUNTS,
                onClick = {
                    onFilterOptionsChanged(
                        currentOptions.copy(viewMode = EVMViewMode.ALL_ACCOUNTS)
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun EVMFilterToggleItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.mangalaColors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Transparent)
            .border(
                width = 1.dp,
                brush = SolidColor(colors.border),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MangalaTypography.Size14Medium(),
                color = colors.textSecondary
            )

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(WalletThemeV2.Colors.evmAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EVMFilterSelectableItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.mangalaColors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Transparent)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                brush = if (isSelected) {
                    colors.borderHighlight
                } else {
                    SolidColor(colors.border)
                },
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MangalaTypography.Size14Medium(),
                color = if (isSelected) colors.textPrimary else colors.textSecondary
            )

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(WalletThemeV2.Colors.evmAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}
