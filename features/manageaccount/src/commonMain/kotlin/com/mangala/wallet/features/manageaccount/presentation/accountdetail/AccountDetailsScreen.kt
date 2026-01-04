package com.mangala.wallet.features.manageaccount.presentation.accountdetail

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Copy
import com.mangala.wallet.ui.MangalaTextButton
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextTiny
import com.mangala.wallet.ui.component.AccountAddressImage
import com.mangala.wallet.ui.component.BasicTextFieldWithHint
import com.mangala.wallet.ui.component.MangalaWalletIconButton
import com.mangala.wallet.ui.component.MangalaWalletSwitch
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AccountDetailsScreen(private val accountId: String) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val screenModel = rememberScreenModel { AccountDetailsScreenModel(accountId) }

        val uiState = screenModel.uiState.collectAsStateMultiplatform().value

        AddAccountScreen(
            uiState = uiState,
            onBackClicked = {
                navigator.pop()
            },
            onClickCopy = {
                screenModel.onClickCopy()
            },
            onUpdateAccountName = {
                screenModel.onUpdateAccountName(it)
            },
            onToggleHide = {
                screenModel.onToggleHide(it)
            },
            onClickSave = {
                screenModel.onClickSave()
            }
        )
    }

    @Composable
    private fun AddAccountScreen(
        uiState: AccountDetailsScreenUiState,
        onUpdateAccountName: (String) -> Unit,
        onBackClicked: () -> Unit,
        onClickCopy: () -> Unit,
        onToggleHide: (Boolean) -> Unit,
        onClickSave: () -> Unit
    ) {
        val scaffoldState: ScaffoldState = rememberScaffoldState()
        val coroutineScope: CoroutineScope = rememberCoroutineScope()

        Scaffold(
            scaffoldState = scaffoldState,
            snackbarHost = {
               SnackbarHost(it) { data ->
                   Snackbar(
                       snackbarData = data,
                       backgroundColor = Colors.teal,
                       contentColor = Color.White
                   )
               }
            },
            topBar = {
                MangalaWalletTopBar(
                    modifier = Modifier.background(Colors.cloudGray),
                    text = MR.strings.all_manage_accounts.desc().localized(),
                    onBackClicked = onBackClicked,
                    trailingButton = {
                        val snackbarText = MR.strings.message_account_details_saved_successfully.desc().localized()
                        MangalaTextButton(
                            MR.strings.all_save.desc().localized(),
                            fontWeight = FontWeight.Normal
                        ) {
                            coroutineScope.launch {
                                onClickSave()
                                scaffoldState.snackbarHostState.showSnackbar(snackbarText)
                            }
                        }
                    }
                )
            },
            modifier = Modifier.background(Colors.cloudGray).windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            val uiModel = (uiState as? AccountDetailsScreenUiState.Success)?.accountBlockchainModel

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(Colors.cloudGray)
                    .padding(horizontal = Dimensions.Padding.default)
            ) {
                Spacer(Modifier.height(Spacing.SMALL))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(CornerRadius.Small))
                        .padding(Dimensions.Padding.default),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    uiModel?.let {
                        AccountAddressImage(uiModel.bip44Address)
                        Spacer(Modifier.width(Spacing.TINY))
                        Column {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                TextDescription2(Address(uiModel.bip44Address).eip55, color = Colors.darkGray, modifier = Modifier.weight(1f))
                                Spacer(Modifier.width(Spacing.SMALL))
                                MangalaWalletIconButton(
                                    MangalaWalletPack.Copy,
                                    onClick = {
                                        onClickCopy()
                                    },
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            TextDescription2(MR.strings.title_account_details_address_index.desc().localized() + it.derivationPathIndex)
                        }

                    }
                }
                Spacer(Modifier.height(Spacing.SMALL))
                TextDescription2(
                    MR.strings.message_account_details_account_name.desc().localized(),
                )
                Spacer(Modifier.height(Spacing.TINY))
                BasicTextFieldWithHint(
                    value = uiModel?.bip44Address.orEmpty(),
                    hint = "",
                    boxModifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(CornerRadius.Small))
                        .padding(vertical = 10.dp, horizontal = Spacing.SMALL),
                    textFieldModifier = Modifier.fillMaxWidth(),
                    onValueChange = onUpdateAccountName,
                    fontSize = FontType.REGULAR,
                    singleLine = false
                )
                Spacer(Modifier.height(Spacing.SMALL))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(CornerRadius.Small))
                        .padding(start = Dimensions.Padding.default, end = Dimensions.Padding.half),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextNormal(
                        MR.strings.message_account_details_hide_account.desc().localized(),
                        modifier = Modifier.weight(1f),
                        color = Colors.darkGray
                    )
                    uiModel?.let {
                        MangalaWalletSwitch(checked = it.isHidden, onCheckedChange = { onToggleHide(it) })
                    }
                }
                Spacer(Modifier.height(Spacing.XTINY))
                // Copilot suggestion: Hiding this account will remove it from your account list. You can still access it by going to the settings menu and selecting 'Hidden Accounts'.
                TextTiny(MR.strings.message_account_details_hide_account_description.desc().localized(), modifier = Modifier.padding(horizontal = Dimensions.Padding.default))
            }
        }
    }
}