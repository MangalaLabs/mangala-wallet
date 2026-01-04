package com.mangala.wallet.features.addressbook.presentation.contact.create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.addressbook.presentation.components.BlockchainIconBox
import com.mangala.wallet.features.addressbook.presentation.components.SearchBar
import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.ui.component.HorizontalSpacer
import com.mangala.wallet.ui.component.KeyboardDismissBox
import com.mangala.wallet.ui.theme.mangalaColors

/**
 * Bottom Sheet để chọn network blockchain
 * Hiển thị danh sách các networks với icon, tên mạng và tên đầy đủ
 * Cho phép tìm kiếm mạng bằng thanh tìm kiếm
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkSelectionBottomSheet(
    blockchainEntities: List<BlockchainTypeEntity>,
    onBlockchainSelected: (BlockchainTypeEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        containerColor = MaterialTheme.mangalaColors.bg,
        shape = RoundedCornerShape(topStart = CornerRadius.Medium, topEnd = CornerRadius.Medium),
        dragHandle = null
    ) {
        NetworkSelectionBottomSheetContent(
            blockchainEntities = blockchainEntities,
            onBlockchainSelected = onBlockchainSelected
        )
    }
}

@Composable
private fun NetworkSelectionBottomSheetContent(
    blockchainEntities: List<BlockchainTypeEntity>,
    onBlockchainSelected: (BlockchainTypeEntity) -> Unit
) {
    KeyboardDismissBox(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f)
            .background(MaterialTheme.mangalaColors.bg)
            .safeDrawingPadding()
            .padding(bottom = Spacing.SMALL)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            var searchQuery by remember { mutableStateOf("") }
            val filteredBlockchains = remember(searchQuery, blockchainEntities) {
                blockchainEntities.filter {
                    it.symbol.startsWith(searchQuery, ignoreCase = true) ||
                            it.name.startsWith(searchQuery, ignoreCase = true)
                }
            }

            BottomSheetHeader()

            SearchBarSection(
                searchQuery = searchQuery,
                onQueryChange = {
                    searchQuery = it
                }
            )

            NetworkListSection(
                filteredBlockchains = filteredBlockchains,
                onBlockchainSelected = onBlockchainSelected
            )
        }
    }
}
@Composable
private fun SearchBarSection(
    searchQuery : String,
    onQueryChange : (String) -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.SMALL)
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange =onQueryChange,
            placeholder = "Search network"
        )
    }
}
@Composable
private fun NetworkListSection(
    filteredBlockchains: List<BlockchainTypeEntity> = emptyList(),
    onBlockchainSelected: (BlockchainTypeEntity) -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.SMALL)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(CornerRadius.Small)),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            itemsIndexed(
                items = filteredBlockchains,
                key = { _, blockchain -> blockchain.id }
            ) { index, blockchain ->
                NetworkItem(
                    blockchain = blockchain,
                    isFirst = index == 0,
                    isLast = index == filteredBlockchains.size - 1,
                    onClick = {
                        onBlockchainSelected(blockchain)
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomSheetHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.TINY, Spacing.TINY),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(Spacing.XXBASE)
                .height(Spacing.XTINY)
                .background(
                    color = MaterialTheme.mangalaColors.border,
                    shape = RoundedCornerShape(CornerRadius.Medium)
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.SMALL),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Select network",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = FontType.SMALL
                ),
                color = MaterialTheme.mangalaColors.textPrimary
            )
        }
    }
}

@Composable
private fun NetworkItem(
    blockchain: BlockchainTypeEntity,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val shape = when {
            isFirst && isLast -> RoundedCornerShape(CornerRadius.Small)
            isFirst -> RoundedCornerShape(
                topStart = CornerRadius.Small,
                topEnd = CornerRadius.Small
            )
            isLast -> RoundedCornerShape(
                bottomStart = CornerRadius.Small,
                bottomEnd = CornerRadius.Small
            )
            else -> RoundedCornerShape(0.dp)
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.mangalaColors.bgInnerCard
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = Spacing.MICRO
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.TINY, horizontal = Spacing.SMALL),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BlockchainIconBox(
                    symbol = blockchain.symbol,
                    size = Spacing.XXXBASE - Spacing.TINY,
                    iconSize = Spacing.XMEDIUM + Spacing.MICRO,
                    iconPath = blockchain.icon
                )

                HorizontalSpacer(width = Spacing.SMALL)

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = blockchain.symbol,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = FontType.SMALL
                        ),
                        color = MaterialTheme.mangalaColors.textPrimary
                    )

                    Text(
                        text = blockchain.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = FontType.SMALL
                        ),
                        color = MaterialTheme.mangalaColors.textSecondary
                    )
                }
            }
        }

        if (!isLast) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth(),
                color = MaterialTheme.mangalaColors.border,
                thickness = 0.5.dp
            )
        }
    }
}