package com.mangala.wallet.features.wallet.presentation.addaccount.evm

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowRight
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.BasicTextFieldWithHint
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.getSfProFamilyFont
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow

class AddAccountScreen(
    private val isPinVerified: Boolean = false,
    private val walletId: String? = null
) : BaseScreen<AddAccountScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_ADD_ACCOUNT
    override val screenClassName: String = AddAccountScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): AddAccountScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: AddAccountScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val uiModel = screenModel.uiModel.collectAsStateMultiplatform().value

        LaunchedEffect(true) {
            screenModel.onCreateDone.receiveAsFlow().collectLatest {
                navigator.pop()
            }
        }

        AddAccountContent(
            uiModel = uiModel,
            onBackClicked = { navigator.pop() },
            onChangeAccountName = screenModel::onChangeAccountName,
            onClickAddNewAccount = {
                if (isPinVerified) {
                    screenModel.onAddNewAccount(walletId)
                } else {
                    val unlockPinScreen = ScreenRegistry.get(
                        SharedScreen.UnlockPinScreen(
                            onUnlockSuccess = { screenModel.onAddNewAccount(walletId) }
                        )
                    )
                    navigator.push(unlockPinScreen)
                }
            }
        )
    }

    @Composable
    private fun AddAccountContent(
        uiModel: AddAccountScreenUiModel,
        onBackClicked: () -> Unit,
        onChangeAccountName: (String) -> Unit,
        onClickAddNewAccount: () -> Unit
    ) {
        val colors = MaterialTheme.mangalaColors

        OnboardingGradientBackground(circleBackgroundEnabled = true) {
            Scaffold(
                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                topBar = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.TINY),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimensions.Padding.default),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClicked) {
                            Icon(
                                imageVector = MangalaWalletPack.ArrowLeft,
                                contentDescription = "Back",
                                modifier = Modifier.size(24.dp),
                                tint = colors.iconPrimary
                            )
                        }
                        Text(
                            text = MR.strings.message_add_account_account_nickname.desc().localized(),
                            style = MangalaTypography.Size17SemiBold(),
                            color = colors.textPrimary
                        )
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = Dimensions.Padding.default),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Dimensions.Padding.small)
                            .background(
                                color = colors.bgInnerCard.copy(alpha = 0.58f),
                                shape = RoundedCornerShape(28.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = colors.border.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(28.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 18.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = MR.strings.message_add_account_account_guide.desc().localized(),
                                color = colors.textSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                fontFamily = getSfProFamilyFont(FontWeight.Normal)
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                BasicTextFieldWithHint(
                                    value = uiModel.accountName,
                                    hint = MR.strings.message_add_account_account_nickname.desc().localized(),
                                    hintColor = colors.textSecondary.copy(alpha = 0.45f),
                                    textColor = colors.textPrimary,
                                    onValueChange = onChangeAccountName,
                                    boxModifier = Modifier.fillMaxWidth(),
                                    textFieldModifier = Modifier.fillMaxWidth(),
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .background(colors.textLink)
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(bottom = Dimensions.Padding.small)
                    ) {
                        MangalaGradientButton(
                            onClick = onClickAddNewAccount,
                            enabled = uiModel.isButtonEnabled,
                            size = MangalaButtonSize.Big,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = MR.strings.all_confirm.desc().localized(),
                                    style = MangalaTypography.Size17SemiBold(),
                                    color = colors.textPrimary
                                )
                                Icon(
                                    imageVector = MangalaWalletPack.ArrowRight,
                                    contentDescription = "Confirm",
                                    tint = colors.textPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
