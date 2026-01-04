package com.mangala.wallet.features.conversationui.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mangala.wallet.model.blockchain.BlockchainNetworkData

@Composable
fun NetworkSelectorComponent(
    onNetworkSelected: (BlockchainNetworkData) -> Unit = {}
) {
    var selectedNetwork by remember { mutableStateOf<BlockchainNetworkData?>(null) }
    
    val supportedNetworks = remember {
        BlockchainNetworkData.getAllBlockchainNetworkSupported(includeDebugNetworks = true)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = "Select Network",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(supportedNetworks) { network ->
                NetworkItem(
                    network = network,
                    isSelected = selectedNetwork == network,
                    onSelect = { selectedNetwork = network }
                )
            }
        }
        
        if (selectedNetwork != null) {
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = {
                    selectedNetwork?.let { network ->
                        onNetworkSelected(network)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirm ${selectedNetwork?.name}")
            }
        }
    }
}

@Composable
private fun NetworkItem(
    network: BlockchainNetworkData,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    
    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onSelect() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .widthIn(min = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = network.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}