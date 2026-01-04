package com.mangala.browser_bridge_base.switchchain

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowRight
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Clear
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextTitle4
import com.mangala.wallet.ui.component.MangalaWalletIconButton
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.fontFamilyResource

class SwitchChainScreen(
    val currentChainId: Long,
    val newChainId: Long,
    val onConfirm: () -> Unit,
    val onDecline: () -> Unit,
) : BaseScreen<SwitchChainScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.BROWSER_SWITCH_CHAIN
    override val screenClassName: String = SwitchChainScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): SwitchChainScreenModel {
        return getScreenModel<SwitchChainScreenModel>()
    }

    @Composable
    override fun ScreenContent(screenModel: SwitchChainScreenModel) {
        Body(
            screenModel,
            currentChainId = currentChainId,
            newChainId = newChainId,
            onConfirm = onConfirm,
            onDecline = onDecline,
        )
    }

    @Composable
    fun Body(
        screenModel: SwitchChainScreenModel,
        currentChainId: Long,
        newChainId: Long,
        onConfirm: () -> Unit,
        onDecline: () -> Unit,
    ) {
        val blockchainNetworkData = BlockchainNetworkData.getBlockchainByChainId(newChainId, screenModel.isDevelopmentEnvironment)
        val currentBlockChain = BlockchainNetworkData.getBlockchainByChainId(currentChainId, screenModel.isDevelopmentEnvironment)
        val title = "Switch Chain Request"
        val message =
            "This site is requesting you to switch to the ${blockchainNetworkData?.name} with chain ID: $newChainId. This will reload the page."
        val button = "Switch & Reload"
        Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colors.primary)) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(Spacing.SMALL)
            ) {
                Spacer(modifier = Modifier.height(Spacing.TINY))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextTitle4(
                        text = title,
                    )
                    MangalaWalletIconButton(
                        icon = MangalaWalletPack.Clear,
                        modifier = Modifier.size(36.dp),
                        onClick = onDecline
                    )
                }

                Spacer(Modifier.height(Spacing.BASE))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(
                        modifier = Modifier.height(65.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally // Centering horizontally
                    ) {
                        LocalImage(
                            modifier = Modifier.size(32.dp).clip(CircleShape),
                            imageResource = currentBlockChain?.localImage ?: MR.images.bitcoin,
                            isLoading = false,
                            placeholderModifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = currentBlockChain?.name ?: "",
                            fontWeight = FontWeight.Normal,
                            fontSize = FontType.SMALL,
                            fontFamily = fontFamilyResource(MR.fonts.sfpro)
                        )
                    }

                    Image(
                        imageVector = MangalaWalletPack.ArrowRight, // Replace with your arrow image resource
                        contentDescription = "Arrow",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                    Column(
                        modifier = Modifier.height(65.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally // Centering horizontally
                    ) {
                        LocalImage(
                            modifier = Modifier.size(32.dp).clip(CircleShape),
                            imageResource = blockchainNetworkData?.localImage ?: MR.images.bitcoin,
                            isLoading = false,
                            placeholderModifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = blockchainNetworkData?.name ?: "",
                            fontWeight = FontWeight.Normal,
                            fontSize = FontType.SMALL,
                            fontFamily = fontFamilyResource(MR.fonts.sfpro)
                        )
                    }
                }


                Spacer(Modifier.height(16.dp))
//            Text(
//                text = url,
//                color = MaterialTheme.colors.onSecondary,
//                fontWeight = FontWeight.SemiBold,
//                fontSize = FontType.TITLE_3,
//                fontFamily = fontFamilyResource(MR.fonts.sfpro.sfpro),
//            )

                TextNormal(text = "$message")

                Spacer(modifier = Modifier.height(Spacing.BASE))

                Button(
                    onClick = {
                        screenModel.saveNetwork(newChainId)
                        onConfirm()
                              },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.onSecondary)
                ) {
                    TextNormal(
                        text = button,
                        color = Color.White
                    )
                }
            }
        }
    }

}