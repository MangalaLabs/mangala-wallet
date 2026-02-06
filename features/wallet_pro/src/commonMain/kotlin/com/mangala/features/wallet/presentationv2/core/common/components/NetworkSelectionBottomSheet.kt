package com.mangala.features.wallet.presentationv2.core.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkSelectionBottomSheet(
    availableNetworks: List<BlockchainNetworkData>,
    selectedNetwork: BlockchainNetworkData?,
    onNetworkSelected: (BlockchainNetworkData) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val colors = MaterialTheme.mangalaColors

    ModalBottomSheet(
        modifier = Modifier.safeDrawingPadding(),
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
                text = stringResource(MR.strings.all_select_network),
                style = MangalaTypography.Size17SemiBold(),
                color = colors.textPrimary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableNetworks) { network ->
                    NetworkSwitchItem(
                        network = network,
                        isSelected = network == selectedNetwork,
                        onClick = {
                            scope.launch {
                                bottomSheetState.hide()
                                onNetworkSelected(network)
                                onDismiss()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun NetworkSwitchItem(
    network: BlockchainNetworkData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.mangalaColors
    val testnetLabel = stringResource(MR.strings.label_testnet)
    val selectedDescription = stringResource(MR.strings.content_description_selected)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Transparent)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                brush = if (isSelected) {
                    colors.borderHighlight
                } else {
                    Brush.linearGradient(
                        colors = listOf(colors.border, colors.border)
                    )
                },
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
            if (network.localImage != null) {
                LocalImage(
                    imageResource = network.localImage!!,
                    modifier = Modifier.size(40.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = colors.textLink.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = network.blockchainType.name.first().toString(),
                        style = MangalaTypography.Size14SemiBold(),
                        color = colors.textLink
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = network.blockchainType.name,
                    style = MangalaTypography.Size14Medium(),
                    color = colors.textPrimary.copy(alpha = if (isSelected) 0.95f else 0.8f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (network.isTestNet) {
                    Text(
                        text = testnetLabel,
                        style = MangalaTypography.Size13Regular(),
                        color = colors.textSecondary
                    )
                }
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(colors.textLink),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = selectedDescription,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}
