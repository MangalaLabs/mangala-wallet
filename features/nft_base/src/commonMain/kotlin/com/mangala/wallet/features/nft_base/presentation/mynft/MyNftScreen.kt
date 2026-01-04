package com.mangala.wallet.features.nft_base.presentation.mynft

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mangala.wallet.features.nft_base.presentation.NftScreenUiModel
import com.mangala.wallet.features.nft_base.presentation.NftScreenUiState
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowDownCollapse
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowDownExpand
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.MangalaWalletDropDownWithAccountAddressImage
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.imageloader.RemoteImage
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import io.github.aakira.napier.Napier

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ColumnScope.MyNftScreen(
    uiState: NftScreenUiState,
    onSelectAccount: (index: Int) -> Unit,
    onToggleExpandCollection: (collectionContractAddress: String) -> Unit,
    onClickNft: (collectionContractAddress: String, tokenId: String) -> Unit,
    isRefreshing: Boolean,
    pullRefreshState: PullRefreshState
) {
    val account = uiState.selectedAccount?.account

    VerticalSpacer(Spacing.BASE)
    MangalaWalletDropDownWithAccountAddressImage(
        chosenOptionName = account?.account?.name.orEmpty(),
        chosenOptionAddress = account?.bip44Address.orEmpty(),
        listOptionAddress = uiState.accounts.map { it.account.bip44Address },
        listOptionName = uiState.accounts.map { it.account.account.name },
        textColor = Color.Black,
        onClickOption = {
            onSelectAccount(it)
        },
    )
    VerticalSpacer(Spacing.BASE)

    when (uiState) {
        is NftScreenUiState.Loading -> {
            MaxSizeBox(
                contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Red)
            }
        }

        is NftScreenUiState.Success -> {
            val uiModel = (uiState as? NftScreenUiState.Success)

            uiModel?.let {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Spacing.BASE),
                    modifier = Modifier.pullRefresh(pullRefreshState, enabled = !isRefreshing)
                        .fillMaxSize()
                ) {
                    items(it.collections, key = { it.contractAddress }) { item ->
                        NftCollection(item, onToggleExpandCollection, onClickNft)
                    }
                }
            }
        }

        is NftScreenUiState.Error -> {
            MaxSizeBox(
                modifier = Modifier.pullRefresh(pullRefreshState, enabled = !isRefreshing)
                    .fillMaxSize()
            ) {
                Text(MR.strings.all_error_no_params.desc().localized())
            }
        }
    }
}

@Composable
private fun NftCollection(
    item: NftScreenUiModel.NftCollectionUiModel,
    onToggleExpandCollection: (collectionContractAddress: String) -> Unit,
    onClickNft: (collectionContractAddress: String, tokenId: String) -> Unit
) {
    MaxWidthRow(horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onToggleExpandCollection(item.contractAddress) }) {
        val collectionName =
            if (item.isFavorite) MR.strings.label_favorite_nfts.desc().localized() else item.name
        TextNormal(collectionName, color = Colors.black)
        Icon(
            imageVector = if (item.isExpanded) MangalaWalletPack.ArrowDownCollapse else MangalaWalletPack.ArrowDownExpand,
            contentDescription = null,
            tint = Colors.darkGray,
            modifier = Modifier.size(Dimensions.IconExpandNftCollectionSize)
        )
    }
    NftCollectionContent(item.isExpanded, item, onClickNft)
}

@Composable
private fun NftCollectionContent(
    isExpanded: Boolean,
    item: NftScreenUiModel.NftCollectionUiModel,
    onClickNft: (collectionContractAddress: String, tokenId: String) -> Unit
) {
    AnimatedVisibility(
        isExpanded, enter = expandVertically(), exit = shrinkVertically()
    ) {
        Column {
            VerticalSpacer(Spacing.SMALL)
            NftCollectionItems(item) {
                onClickNft(it.collectionContractAddress, it.tokenId)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NftCollectionItems(
    item: NftScreenUiModel.NftCollectionUiModel,
    onClickNft: (NftScreenUiModel.NftItemUiModel) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.XSMALL),
        verticalArrangement = Arrangement.spacedBy(Spacing.XSMALL)
    ) {
        item.items.forEach {
            NftItem(onClickNft, it)
        }
    }
}

@Composable
private fun NftItem(
    onClickNft: (NftScreenUiModel.NftItemUiModel) -> Unit, it: NftScreenUiModel.NftItemUiModel
) {
    Box(
        Modifier.clip(RoundedCornerShape(CornerRadius.Small))
            .clickable(onClick = { onClickNft(it) })
    ) {
        RemoteImage(
            url = it.imageUrl, modifier = Modifier.size(Dimensions.ImageNftItemSize)
        )
    }
}