package com.mangala.wallet.features.wallet.presentation.addaccount.evm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextTopBar
import com.mangala.wallet.ui.component.BasicTextFieldWithHint
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow

class AddAccountScreen(
    private val isPinVerified: Boolean = false
) : BaseScreen<AddAccountScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_ADD_ACCOUNT
    override val screenClassName: String = AddAccountScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): AddAccountScreenModel {
        return getScreenModel()
    }

    @Composable
    override fun ScreenContent(screenModel: AddAccountScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        val uiModel = screenModel.uiModel.collectAsStateMultiplatform().value

        LaunchedEffect(true) {
            screenModel.onCreateDone.receiveAsFlow().collectLatest {
                navigator.popUntilRoot()
            }
        }

        AddAccountScreen(
            uiModel = uiModel,
            onBackClicked = {
                navigator.pop()
            },
            onChangeAccountName = {
                screenModel.onChangeAccountName(it)
            },
            onClickAddNewAccount = {
                // V2 callback approach - no unlockPinCase needed
                val unlockPinScreen = ScreenRegistry.get(
                    SharedScreen.UnlockPinScreen(
                        onUnlockSuccess = {
                            screenModel.onAddNewAccount()
                        }
                    )
                )
                navigator.push(unlockPinScreen)
            }
        )
    }

    @Composable
    private fun AddAccountScreen(
        uiModel: AddAccountScreenUiModel,
        onBackClicked: () -> Unit,
        onChangeAccountName: (String) -> Unit,
        onClickAddNewAccount: () -> Unit
    ) {
        Scaffold(
            topBar = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.TINY),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        onBackClicked()
                    }) {
                        Icon(
                            imageVector = MangalaWalletPack.ArrowLeft,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    TextTopBar(
                        text = MR.strings.all_add_new_account.desc().localized(),
                    )
                }
            },
            modifier = Modifier.background(MaterialTheme.colors.primary).windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.primary)
                    .padding(vertical = Dimensions.Padding.small, horizontal = Dimensions.Padding.default),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.SMALL)) {
                    BasicTextFieldWithHint(
                        value = uiModel.accountName,
                        hint =  MR.strings.message_add_account_account_nickname.desc().localized(),
                        hintColor = Color(0xFFD7D7D7),
                        onValueChange = onChangeAccountName,
                        boxModifier = Modifier.fillMaxWidth().padding(vertical = Dimensions.Padding.small),
                        textFieldModifier = Modifier.fillMaxWidth(),
                        fontSize = FontType.REGULAR,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextDescription2(MR.strings.message_add_account_account_guide.desc().localized())
                }
                ButtonNormal(
                    text = MR.strings.all_add_new_account.desc().localized(),
                    modifier = Modifier.fillMaxWidth(),
                    buttonModifier = Modifier.height(44.dp).fillMaxWidth(),
                    fontSize = FontType.REGULAR,
                    onClick = onClickAddNewAccount,
                    enabled = uiModel.isButtonEnabled
                )
            }
        }
    }
}
