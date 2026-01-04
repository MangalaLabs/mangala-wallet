package com.mangala.wallet.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.theme.mangalaColors

fun LazyListScope.NetworkList(
    networks: List<BlockchainNetworkData>,
    onNetworkSelected: (BlockchainNetworkData) -> Unit,
    filter: String,
) {
    items(networks.size) { index ->
        val network = networks[index]
        NetworkItem(onNetworkSelected, network, filter)
    }
}

@Composable
fun NetworkItem(
    onNetworkSelected: (BlockchainNetworkData) -> Unit,
    network: BlockchainNetworkData,
    filter: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.mangalaColors.bgInnerCard
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onNetworkSelected(network)
                }
                .padding(horizontal = Dimensions.Padding.default, vertical = Spacing.XSMALL),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LocalImage(
                modifier = Modifier.size(40.dp).clip(CircleShape),
                network.localImage,
                isLoading = false,
                placeholderModifier = Modifier.size(40.dp)
            )
            HorizontalSpacer(Spacing.SMALL)
            MaxWidthColumn {
                HighlightedNetworkText(network, filter)
            }
        }
    }
}

@Composable
private fun HighlightedNetworkText(
    network: BlockchainNetworkData,
    filter: String
) {
    val text = buildAnnotatedString {
        val name = network.name
        val defaultColor = MaterialTheme.mangalaColors.textPrimary
        val highlightColor = MaterialTheme.mangalaColors.textLink

        var startIndex = 0
        val pattern = filter.toRegex(RegexOption.IGNORE_CASE)

        pattern.findAll(name).forEach { result ->
            val matchStart = result.range.first
            val matchEnd = result.range.last
            if (matchStart > startIndex) {
                withStyle(style = SpanStyle(color = defaultColor)) {
                    append(name.substring(startIndex, matchStart))
                }
            }
            withStyle(style = SpanStyle(color = highlightColor)) {
                append(name.substring(matchStart, matchEnd + 1))
            }
            startIndex = matchEnd + 1
        }

        if (startIndex < name.length) {
            withStyle(style = SpanStyle(color = defaultColor)) {
                append(name.substring(startIndex))
            }
        }
    }

    TextNormal(
        text = text,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.mangalaColors.textPrimary
    )
}