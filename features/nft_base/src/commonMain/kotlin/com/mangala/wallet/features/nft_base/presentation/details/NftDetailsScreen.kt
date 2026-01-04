package com.mangala.wallet.features.nft_base.presentation.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.nft_base.presentation.send.SendNftScreen
import com.mangala.wallet.features.nft_base.presentation.ui.NftImage
import com.mangala.wallet.features.nft_base.presentation.ui.NftImageType
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Star
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.StarYellow
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.formattedAddress
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

class NftDetailsScreen(
    private val accountId: String,
    private val collectionContractAddress: String,
    private val tokenId: String
) : BaseScreen<NftDetailsScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_NFT_DETAILS
    override val screenClassName: String = NftDetailsScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible = false

    @Composable
    override fun createScreenModel(): NftDetailsScreenModel = getScreenModel(
        parameters = {
            parametersOf(
                accountId,
                collectionContractAddress,
                tokenId
            )
        }
    )

    @Composable
    override fun ScreenContent(screenModel: NftDetailsScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        val importNftScreen = rememberScreen(SharedScreen.ImportNftScreen)

        val uiState = screenModel.uiState.collectAsStateMultiplatform().value

        NftDetailsScreen(
            uiState,
            onClickBack = {
                navigator.pop()
            },
            onClickSend = {
                navigator.push(
                    SendNftScreen(
                        accountId = accountId,
                        collectionContractAddress = collectionContractAddress,
                        tokenId = tokenId
                    )
                )
            },
            onToggleFavorite = {
                screenModel.toggleNftFavoriteStatus()
            }
        )
    }

    @Composable
    fun NftDetailsScreen(
        uiState: NftDetailsScreenUiState,
        onClickBack: () -> Unit,
        onClickSend: (NftDetailsScreenUiModel) -> Unit,
        onToggleFavorite: () -> Unit
    ) {
        MaxSizeColumn(Modifier.background(MaterialTheme.colors.background).windowInsetsPadding(WindowInsets.safeDrawing)) {
            MangalaWalletTopBar(
                text = MR.strings.all_my_nft.desc().localized(),
                onBackClicked = onClickBack
            )
            MaxSizeColumn(
                Modifier
                    .padding(horizontal = Dimensions.ScreenNftDetailsPadding)
                    .weight(1f)
            ) {
                NftDetailsScreenContent(uiState, onClickSend)
            }
            Footer(uiState, onClickSend, onToggleFavorite)
        }
    }

    @Composable
    private fun ColumnScope.NftDetailsScreenContent(
        uiState: NftDetailsScreenUiState,
        onClickSend: (NftDetailsScreenUiModel) -> Unit
    ) {
        MaxSizeBox {
            when (uiState) {
                is NftDetailsScreenUiState.Loading -> {
                    CircularProgressIndicator()
                }

                 is NftDetailsScreenUiState.Success -> {
                    MaxSizeColumn(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(Spacing.SMALL),
                    ) {
                        VerticalSpacer(Spacing.BASE)
                        uiState.uiModel.nftCollection.let { nftCollection ->
                            val nft = nftCollection.nft.first()

                            NftImage(nftCollection.contractName, nft, NftImageType.LARGE)
                            NftInfoRow(MR.strings.label_nft_details_token_standard.desc().localized(), nftCollection.type.name)
                            NftInfoRow(MR.strings.label_nft_details_asset_contract.desc().localized(), Address(nftCollection.contractAddress).eip55.formattedAddress())
                        }
                    }
                }
                is NftDetailsScreenUiState.Error -> {
                    Text(MR.strings.message_nft_details_screen_error_loading_nft_details.desc().localized())
                }
            }
        }
    }

    @Composable
    private fun Footer(
        uiState: NftDetailsScreenUiState,
        onClickSend: (NftDetailsScreenUiModel) -> Unit,
        onToggleFavorite: () -> Unit
    ) {
        val uiState = uiState as? NftDetailsScreenUiState.Success

        MaxWidthRow(
            Modifier.padding(vertical = Dimensions.Padding.default, horizontal = Dimensions.ScreenNftDetailsPadding),
            horizontalArrangement = Arrangement.spacedBy(Spacing.TINY),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ButtonNormal(
                MR.strings.all_send.desc().localized(),
                enabled = true,
                buttonModifier = Modifier.height(IntrinsicSize.Min).weight(1f)
            ) {
                uiState?.uiModel?.let { onClickSend(it) }
            }
            Box(
                Modifier
                    .height(IntrinsicSize.Max)
                    .clip(RoundedCornerShape(CornerRadius.Tiny))
                    .background(Colors.caption)
                    .clickable(enabled = uiState != null) {
                        onToggleFavorite()
                    }
                    .padding(Dimensions.Padding.small)
            ) {
                val isFavorite = uiState?.uiModel?.nftCollection?.nft?.firstOrNull()?.isFavorite
                Image(
                    imageVector = if (isFavorite == true) MangalaWalletPack.StarYellow else MangalaWalletPack.Star,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center).size(24.dp)
                )
            }
        }
    }

    @Composable
    fun NftInfoRow(label: String, value: String) {
        MaxWidthRow(horizontalArrangement = Arrangement.spacedBy(Spacing.BASE)) {
            TextNormal(label, modifier = Modifier.weight(1f).fillMaxWidth(), color = Colors.caption, textAlign = TextAlign.Start)
            TextNormal(value, modifier = Modifier.weight(1f).fillMaxWidth(), color = Colors.main1Text, textAlign = TextAlign.End)
        }
    }
}