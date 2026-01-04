package com.mangala.wallet.features.menu.presentation.wallet.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextTiny
import com.mangala.wallet.ui.component.AccountAddressImage
import com.mangala.wallet.ui.component.DataInput
import com.mangala.wallet.ui.component.MangalaWalletSwipeToReveal
import com.mangala.wallet.ui.component.MangalaWalletTextField
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.modifier.roundedCornerItemShape
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

internal class WalletDetailsScreen(private val walletId: String) : BaseScreen<WalletDetailsScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_WALLET_DETAILS
    override val screenClassName: String = WalletDetailsScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible = false

    @Composable
    override fun createScreenModel(): WalletDetailsScreenModel = getScreenModel(
        parameters = {
            parametersOf(walletId)
        }
    )

    @Composable
    override fun ScreenContent(screenModel: WalletDetailsScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val walletDetail by screenModel._walletName.collectAsStateMultiplatform()
        val uiModel by screenModel._uiModel.collectAsStateMultiplatform()
        val viewPhrase =
            rememberScreen(SharedScreen.UnlockPinScreen(antelopeAccountName = null, unlockPinCase = SharedScreen.UnlockPinScreen.SHOW_WORDS_PHRASE))

        WalletDetails(uiModel = uiModel,
            walletName = walletDetail,
            onBackClicked = {
                navigator.pop()
            },
            onNameChanged = screenModel::updateWalletName,
            onClickChangeHiddenAccount = { screenModel.onClickChangeHiddenAccount(it) },
            onClickViewPhrase = { navigator.push(viewPhrase) }

        )
    }

    @Composable
    fun WalletDetails(
        uiModel: WalletDetailsScreenUiModel,
        walletName: String,
        onBackClicked: () -> Unit,
        onNameChanged: (String) -> Unit,
        onClickChangeHiddenAccount: (String) -> Unit,
        onClickViewPhrase: () -> Unit
    ) {
        Scaffold(topBar = {
            MangalaWalletTopBar(
                modifier = Modifier.background(Colors.cloudGray),
                text = MR.strings.all_wallet.desc().localized(),
                onBackClicked = onBackClicked
            )
        }, modifier = Modifier.background(Colors.cloudGray).windowInsetsPadding(WindowInsets.safeDrawing)) {
            Spacer(Modifier.width(Spacing.SMALL))
            Column(
                modifier = Modifier.fillMaxSize().background(Colors.cloudGray)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = Dimensions.Padding.default)
                ) {
                    DataInput(label = MR.strings.message_label_wallet_name.desc().localized(),
                        inputField = {
                            MangalaWalletTextField(
                                value = walletName,
                                hint = "",//TODO handle hint after Mr.Son confirm
                                onValueChange = onNameChanged,
                            )
                        })
                }
                Spacer(Modifier.height(Spacing.SMALL))
                TextDescription2(
                    text = MR.strings.message_label_wallet_account.desc().localized(),
                    modifier = Modifier.padding(horizontal = Dimensions.Padding.default)
                )
                Spacer(Modifier.height(Spacing.TINY))
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                        .padding(horizontal = Dimensions.Padding.default),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {

                    itemsIndexed(uiModel.accounts,
                        key = { _: Int, item: AccountItemUiModel -> item.account.account.id }) { index, item ->
                        AccountItem(
                            item,
                            shape = roundedCornerItemShape(uiModel.accounts, index),
                            Modifier.background(MaterialTheme.colors.surface),
                            onClickChangeHiddenAccount = onClickChangeHiddenAccount
                        )
                    }
                    item {
                        Spacer(Modifier.height(Spacing.SMALL))
                        ClickableText(
                            text = AnnotatedString(
                                MR.strings.message_label_view_my_recovery_phrase.desc().localized()
                            ),
                            onClick = {
                                onClickViewPhrase()
                            },
                            style = MaterialTheme.typography.body1.copy(
                                fontSize = 17.sp, textDecoration = TextDecoration.Underline
                            ),
                        )

                    }
                }
            }
        }
    }

    @Composable
    private fun AccountItem(
        accountBlockchainModel: AccountItemUiModel,
        shape: Shape,
        modifier: Modifier = Modifier,
        onClickChangeHiddenAccount: (String) -> Unit
    ) {
        val accountModel = accountBlockchainModel.account
        val isHiddenAccount = accountModel.account.isHidden
        val hiddenAccountAlphaModifier = if (isHiddenAccount) Modifier.alpha(0.5f) else Modifier
        val currentItem by rememberUpdatedState(accountBlockchainModel.account.account)

        MangalaWalletSwipeToReveal(
            shape = shape,
            revealedBackgroundColor = Colors.gray,
            text = MR.strings.all_hidden.desc().localized(),
            onClickRevealed = { onClickChangeHiddenAccount(currentItem.id) }
        ){
            Row(
                modifier = Modifier.clip(shape).then(modifier)
                    .background(MaterialTheme.colors.surface),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f).padding(Dimensions.Padding.default).then(hiddenAccountAlphaModifier)
                ) {
                    AccountAddressImage(accountBlockchainModel.account.bip44Address)
                    Spacer(Modifier.width(Spacing.TINY))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        TextNormal(
                            text = accountModel.account.name,
                            color = Colors.darkGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isHiddenAccount) {
                        TextTiny(
                            text = MR.strings.all_hidden.desc().localized(),
                            color = Colors.darkGray,
                            modifier = Modifier.background(
                                Colors.cloudGray, RoundedCornerShape(
                                    CornerRadius.Medium
                                )
                            ).padding(
                                horizontal = Dimensions.Padding.half,
                                vertical = Dimensions.Padding.quarter
                            ),
                        )
                        Spacer(Modifier.width(Spacing.TINY))
                    }
                }
            }
        }
    }
}
