package com.mangala.wallet.features.conversationui.presentation.components.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun NetworkSelector(
    networks: List<BlockchainNetworkData>,
    onNetworkSelected: (BlockchainNetworkData) -> Unit,
    selectedNetworkId: String?,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.TINY),
        contentPadding = PaddingValues(horizontal = Dimensions.Padding.default)
    ) {
        items(networks) { network ->
            NetworkItem(
                network = network,
                isSelected = network.blockChainUid == selectedNetworkId,
                onClick = { onNetworkSelected(network) }
            )
        }
    }
}

@Composable
private fun NetworkItem(
    network: BlockchainNetworkData,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Gradient border brush
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF227BFF),
            Color(0xFF1C8DF9),
            Color(0xFFB988EE),
            Color(0xFFEE4D5D)
        )
    )

    // Background and border styling
    val backgroundColor = if (isSelected) {
        Color(0x1A227BFF) // Light blue background when selected
    } else {
        MaterialTheme.mangalaColors.bgAlpha // Semi-transparent dark background
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                brush = gradientBrush,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(15.dp)
                )
                .clickable { onClick() }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            LocalImage(
                imageResource = network.localImage,
                modifier = Modifier.size(20.dp),
            )
            Text(network.name, style = MangalaTypography.Size12Medium(), color = MaterialTheme.mangalaColors.textPrimary)
        }
    }
}