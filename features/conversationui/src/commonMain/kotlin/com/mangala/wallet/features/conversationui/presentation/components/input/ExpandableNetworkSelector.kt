package com.mangala.wallet.features.conversationui.presentation.components.input

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

/**
 * An expandable network selector component that shows popular networks in a compact view
 * and expands to show a searchable list of all networks.
 * 
 * @param networks List of all available blockchain networks
 * @param onNetworkSelected Callback when a network is selected
 * @param onMinimize Callback when the selector is minimized (collapsed to compact view)
 * @param message Message to display in the banner
 * @param modifier Optional modifier for the component
 */
@Composable
fun ExpandableNetworkSelector(
    networks: List<BlockchainNetworkData>,
    onNetworkSelected: (BlockchainNetworkData) -> Unit,
    onMinimize: () -> Unit,
    message: String = "Please select the network for your contact",
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    val popularNetworks = remember(networks) {
        networks.getPopularNetworks()
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.mangalaColors.bgAlpha)
            .border(
                width = 1.dp,
                color = Color(0xFF227BFF).copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        // Network request banner
        NetworkRequestBanner(
            isExpanded = isExpanded,
            message = message,
            onMinimize = {
                // When banner is minimized, collapse to compact view
                if (isExpanded) {
                    isExpanded = false
                    searchQuery = ""
                }
                onMinimize()
            }
        )
        
        // Network selector content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            AnimatedContent(
                targetState = isExpanded,
                transitionSpec = {
                    if (targetState) {
                        expandVertically() + fadeIn() togetherWith 
                        shrinkVertically() + fadeOut()
                    } else {
                        expandVertically() + fadeIn() togetherWith 
                        shrinkVertically() + fadeOut()
                    }
                }
            ) { expanded ->
                if (expanded) {
                    ExpandedNetworkView(
                        networks = networks,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        onNetworkSelected = { network ->
                            onNetworkSelected(network)
                            isExpanded = false
                            searchQuery = ""
                        },
                        onCollapse = {
                            isExpanded = false
                            searchQuery = ""
                        }
                    )
                } else {
                    CompactNetworkView(
                        popularNetworks = popularNetworks,
                        onNetworkSelected = onNetworkSelected,
                        onExpandClick = { isExpanded = true }
                    )
                }
            }
        }
    }
}

/**
 * Banner that shows the network selection request message with minimize button
 */
@Composable
private fun NetworkRequestBanner(
    isExpanded: Boolean,
    message: String,
    onMinimize: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF227BFF).copy(alpha = 0.1f),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFF8CC8FF),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF8CC8FF)
            )
        }

        if (isExpanded) {
            IconButton(
                onClick = onMinimize,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "Minimize",
                    tint = Color(0xFF8CC8FF),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Compact view showing popular networks with expand button
 */
@Composable
private fun CompactNetworkView(
    popularNetworks: List<BlockchainNetworkData>,
    onNetworkSelected: (BlockchainNetworkData) -> Unit,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        popularNetworks.forEach { network ->
            CompactNetworkItem(
                network = network,
                onClick = { onNetworkSelected(network) },
                modifier = Modifier.weight(1f)
            )
        }
        
        ExpandButton(
            onClick = onExpandClick,
            modifier = Modifier.width(56.dp)
        )
    }
}

/**
 * Individual network item in compact view
 */
@Composable
private fun CompactNetworkItem(
    network: BlockchainNetworkData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = MaterialTheme.mangalaColors.bgAlpha,
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF227BFF),
                    Color(0xFFB988EE)
                )
            )
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LocalImage(
                imageResource = network.localImage,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = network.name,
                style = MangalaTypography.Size10Medium(),
                color = MaterialTheme.mangalaColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Expand button to show all networks
 */
@Composable
private fun ExpandButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = MaterialTheme.mangalaColors.bgAlpha,
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFF227BFF).copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "More networks",
                tint = MaterialTheme.mangalaColors.iconPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Expanded view with search and full network list
 */
@Composable
private fun ExpandedNetworkView(
    networks: List<BlockchainNetworkData>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onNetworkSelected: (BlockchainNetworkData) -> Unit,
    onCollapse: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Search field
        NetworkSearchField(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Filtered network list
        FilteredNetworkList(
            networks = networks,
            searchQuery = searchQuery,
            onNetworkSelected = onNetworkSelected,
            modifier = Modifier.heightIn(max = 200.dp)
        )
    }
}

/**
 * Search field for filtering networks
 */
@Composable
private fun NetworkSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                "Search networks...",
                color = MaterialTheme.mangalaColors.textPrimary.copy(alpha = 0.5f)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.mangalaColors.iconPrimary.copy(alpha = 0.7f)
            )
        },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = MaterialTheme.mangalaColors.iconPrimary.copy(alpha = 0.7f)
                    )
                }
            }
        } else null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF227BFF),
            unfocusedBorderColor = Color(0xFF227BFF).copy(alpha = 0.3f),
            focusedTextColor = MaterialTheme.mangalaColors.textPrimary,
            unfocusedTextColor = MaterialTheme.mangalaColors.textPrimary,
            cursorColor = Color(0xFF227BFF)
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        )
    )
}

/**
 * Filtered list of networks based on search query
 */
@Composable
private fun FilteredNetworkList(
    networks: List<BlockchainNetworkData>,
    searchQuery: String,
    onNetworkSelected: (BlockchainNetworkData) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredNetworks = remember(networks, searchQuery) {
        if (searchQuery.isBlank()) {
            networks
        } else {
            networks.filter { network ->
                network.name.contains(searchQuery, ignoreCase = true) ||
                network.blockChainUid.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (filteredNetworks.isEmpty()) {
            item {
                EmptySearchState(searchQuery)
            }
        } else {
            items(filteredNetworks) { network ->
                ExpandedNetworkItem(
                    network = network,
                    onClick = { onNetworkSelected(network) }
                )
            }
        }
    }
}

/**
 * Individual network item in expanded view
 */
@Composable
private fun ExpandedNetworkItem(
    network: BlockchainNetworkData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = Color(0x1A227BFF),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LocalImage(
                imageResource = network.localImage,
                modifier = Modifier.size(24.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = network.name,
                    style = MangalaTypography.Size14Medium(),
                    color = MaterialTheme.mangalaColors.textPrimary
                )
                if (network.isTestNet) {
                    Text(
                        text = "Testnet",
                        style = MangalaTypography.Size12Regular(),
                        color = Color(0xFFFFB74D)
                    )
                }
            }
        }
    }
}

/**
 * Empty state when no networks match the search
 */
@Composable
private fun EmptySearchState(
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No networks found",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.mangalaColors.textPrimary.copy(alpha = 0.7f)
        )
        if (searchQuery.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Try a different search term",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.mangalaColors.textPrimary.copy(alpha = 0.5f)
            )
        }
    }
}

/**
 * Extension function to get the most popular networks from a list
 */
fun List<BlockchainNetworkData>.getPopularNetworks(): List<BlockchainNetworkData> {
    // Popular network IDs in order of popularity
    val popularIds = listOf(
        "ethereum",
        "bitcoin", 
        "solana",
        "binance-smart-chain"
    )
    
    // Filter and sort networks based on popularity
    val popularNetworks = mutableListOf<BlockchainNetworkData>()
    
    // First, add networks in the order of popularIds
    popularIds.forEach { id ->
        this.find { network -> 
            network.blockChainUid.lowercase() == id.lowercase()
        }?.let { popularNetworks.add(it) }
    }
    
    // Return up to 4 popular networks
    return popularNetworks.take(4)
}