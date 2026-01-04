package com.mangala.wallet.features.nft_base.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mangala.wallet.features.nft_base.domain.model.NftCollection
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.imageloader.RemoteImage

@Composable
fun NftImage(nftCollectionName: String, nft: NftCollection.Nft, nftImageType: NftImageType) {

    val sizeModifier = remember(nftImageType) {
        when (nftImageType) {
            NftImageType.LARGE -> Modifier.fillMaxWidth()
            NftImageType.MEDIUM -> Modifier.size(200.dp)
            NftImageType.SMALL -> Modifier.size(160.dp)
        }
    }
    val infoTextHorizontalPadding = remember(nftImageType) {
        when(nftImageType) {
            NftImageType.LARGE, NftImageType.MEDIUM -> Dimensions.Padding.default
            NftImageType.SMALL -> Dimensions.Padding.small
        }
    }
    val infoTextTopPadding = remember(nftImageType) {
        if (nftImageType == NftImageType.LARGE) Dimensions.Padding.small else Dimensions.Padding.half
    }
    val infoTextBottomPadding = remember(nftImageType) {
        when(nftImageType) {
            NftImageType.LARGE, NftImageType.MEDIUM -> Dimensions.Padding.small
            NftImageType.SMALL -> Dimensions.Padding.small
        }
    }

    Box(
        Modifier
        .then(sizeModifier)
        .aspectRatio(1f)
        .clip(RoundedCornerShape(CornerRadius.Small))
    ) {
        Box(
            Modifier.then(sizeModifier)
        ) {
            RemoteImage(
                Modifier
                    .then(sizeModifier)
                    .aspectRatio(1f),
                url = nft.image
            )
        }
        Column(
            Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Colors.darkGray.copy(alpha = 0f),
                            Colors.darkGray
                        )
                    )
                )
                .padding(
                    start = infoTextHorizontalPadding,
                    end = infoTextHorizontalPadding,
                    bottom = infoTextBottomPadding,
                    top = infoTextTopPadding
                )
        ) {
            TextNormal(
                nft.name.ifBlank { nftCollectionName },
                fontWeight = FontWeight.W500,
                color = Colors.white
            )
            if (nftImageType == NftImageType.SMALL) {
                VerticalSpacer(Spacing.XTINY)
            }
            TextDescription2("#${nft.tokenId}", color = Colors.stroke2)
        }
    }
}

enum class NftImageType {
    SMALL,
    MEDIUM,
    LARGE
}