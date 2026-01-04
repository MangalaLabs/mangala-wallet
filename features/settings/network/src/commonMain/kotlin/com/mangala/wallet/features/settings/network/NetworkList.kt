package com.mangala.wallet.features.settings.network

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Check
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.component.MangalaWalletSearchBarWithBorder
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.modifier.roundedCornerItemShape
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun NetworkList(
    uiModel: NetworkScreenModelUiModel,
    onChangeQuery: (String) -> Unit,
    onItemSelected: (NetworkScreenModelItemUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        MangalaWalletSearchBarWithBorder(
            query = uiModel.query,
            placeholder = MR.strings.message_network_hint.desc().localized(),
            onQueryChange = onChangeQuery
        )
        Spacer(Modifier.height(Spacing.SMALL))
        if (uiModel.filteredItems.isEmpty()) {
            Box(Modifier.fillMaxSize()) {
                TextDescription2(
                    MR.strings.message_network_no_networks_found.desc().localized(),
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.mangalaColors.textSecondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(1.dp),
                contentPadding = PaddingValues(bottom = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding())
            ) {
                itemsIndexed(
                    uiModel.filteredItems,
                    key = { _, item -> item.network.name }
                ) { index, item ->
                    NetworkItem(
                        item = item,
                        shape = roundedCornerItemShape(uiModel.filteredItems, index),
                        onClick = onItemSelected
                    )
                }
            }
        }
    }
}

@Composable
private fun NetworkItem(
    item: NetworkScreenModelItemUiModel,
    shape: Shape,
    onClick: (NetworkScreenModelItemUiModel) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.mangalaColors.bgInnerCard)
            .clickable(onClick = { onClick(item) })
            .padding(Dimensions.Padding.default),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            LocalImage(
                modifier = Modifier.size(24.dp),
                imageResource = item.network.localImage,
            )
            Spacer(modifier = Modifier.width(Spacing.TINY))
            TextDescription2(text = item.network.name, color = MaterialTheme.mangalaColors.textPrimary, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(Spacing.SMALL))
        }

        if (item.isSelected) {
            Icon(
                imageVector = MangalaWalletPack.Check,
                tint = MaterialTheme.mangalaColors.iconPrimary,
                contentDescription = "Selected",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}