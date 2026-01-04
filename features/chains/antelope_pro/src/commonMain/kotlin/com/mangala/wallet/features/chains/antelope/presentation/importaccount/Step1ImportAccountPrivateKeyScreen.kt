package com.mangala.wallet.features.chains.antelope.presentation.importaccount

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.features.chains.antelope.presentation.importaccount.step2.Step2ImportAccountSelectAccountScreen
import com.mangala.wallet.ui.component.CreateImportButton
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Clear
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Paste
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Scan
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.scanqr.ScanQRCodeListener
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextSubTitle
import com.mangala.wallet.ui.component.CreateImportTextField
import com.mangala.wallet.ui.component.GradientBackground
import com.mangala.wallet.ui.component.HorizontalSpacer
import com.mangala.wallet.ui.component.MangalaButton
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaTextButton
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class Step1ImportAccountPrivateKeyScreen(
    private val privateKey: String?
) : BaseScreen<Step1ImportAccountPrivateKeyScreenModel>(), KoinComponent {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_IMPORT_ACCOUNT_PRIVATE_KEY
    override val screenClassName: String = Step1ImportAccountPrivateKeyScreen::class.simpleName.orEmpty()

    private val scanQRCode: ScanQRCode by inject()

    override val isBottomBarVisible = false

    @Composable
    override fun createScreenModel(): Step1ImportAccountPrivateKeyScreenModel {
        return getScreenModel(parameters = {
            parametersOf(privateKey)
        })
    }

    @Composable
    override fun ScreenContent(screenModel: Step1ImportAccountPrivateKeyScreenModel) {
        val uiState = screenModel.uiState.collectAsState().value

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(uiState) {
            if (uiState is Step1ImportAccountPrivateKeyScreenUiState.Imported) {
                navigator.push(Step2ImportAccountSelectAccountScreen(uiState.privateKey, ArrayList(uiState.accountsByAuthorizers)))
                screenModel.onNavigateToNextStep()
            }
        }

        (uiState as? Step1ImportAccountPrivateKeyScreenUiState.NotImported)?.let {
            ImportAccountScreen(
                uiState,
                onBackPressed = { navigator.pop() },
                onClickContinue = {
                    screenModel.onImportAccount()
                },
                onPrivateKeyChange = {
                    screenModel.onPrivateKeyChange(it.trim())
                },
                navigator = navigator
            )
        }
    }

    @Composable
    fun ImportAccountScreen(
        uiState: Step1ImportAccountPrivateKeyScreenUiState.NotImported,
        onBackPressed: () -> Unit,
        onPrivateKeyChange: (String) -> Unit,
        onClickContinue: () -> Unit,
        navigator: Navigator
    ) {
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current

        GradientBackground(Modifier.pointerInput(Unit) {
            keyboardController?.hide()
            detectTapGestures { focusManager.clearFocus() }
        }) {
            MaxSizeColumn(verticalArrangement = Arrangement.SpaceBetween) {
                MangalaWalletTopBar(
                    modifier = Modifier.background(Color.Transparent),
                    text = "",
                    onBackClicked = onBackPressed
                )
                MaxWidthColumn(
                    modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                        .padding(horizontal = Dimensions.Padding.default)
                ) {
                    Spacer(Modifier.height(Spacing.LARGE))
                    if (uiState.isLoading) {
                        LoadingImportAccount()
                    } else {
                        TextSubTitle(
                            text = MR.strings.title_import_eos_account.desc().localized(),
                            fontWeight = FontWeight.Medium,
                            color = Colors.darkDarkGray
                        )
                        VerticalSpacer(Spacing.TINY)
                        TextDescription2(
                            text = MR.strings.message_guide_import_eos_account.desc().localized(),
                            color = Colors.caption
                        )
                        Spacer(Modifier.height(Spacing.BASE))
                        TextDescription2(
                            text = MR.strings.all_private_key.desc().localized(),
                            modifier = Modifier.fillMaxWidth(),
                            color = Colors.darkDarkGray
                        )
                        Spacer(Modifier.height(Spacing.TINY))
                        val clipboardManager = LocalClipboardManager.current
                        val interactionSource = remember { MutableInteractionSource() }
                        val showFocusedActions = remember { mutableStateOf(false) }
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect { interaction ->
                                when (interaction) {
                                    is FocusInteraction.Focus -> {
                                        showFocusedActions.value = true
                                    }

                                    is FocusInteraction.Unfocus -> {
                                        showFocusedActions.value = false
                                    }
                                }
                            }
                        }

                        CreateImportTextField(
                            value = uiState.privateKey,
                            onValueChange = {
                                onPrivateKeyChange(it)
                            },
                            placeholderText = MR.strings.message_enter_private_key.desc()
                                .localized(),
                            trailingIcon = {
                                if (showFocusedActions.value) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .background(Colors.alto, shape = CircleShape)
                                                .clickable {
                                                    onPrivateKeyChange("")
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                MangalaWalletPack.Clear,
                                                contentDescription = "Clear",
                                                tint = Colors.white,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        HorizontalSpacer(Spacing.XXTINY)
                                        IconButton(onClick = {
                                            val clipboardText =
                                                clipboardManager.getText()?.text ?: ""
                                            focusManager.clearFocus()
                                            onPrivateKeyChange(clipboardText)
                                        }) {
                                            Icon(
                                                imageVector = MangalaWalletPack.Paste,
                                                contentDescription = "Paste private key",
                                                tint = Colors.main1Text,
                                                modifier = Modifier.size(Dimensions.IconButtonSize)
                                            )
                                        }
                                        HorizontalSpacer(Spacing.XSMALL)
                                    }
                                } else {
                                    IconButton(onClick = {
                                        scanQRCode.scanQRCode(object : ScanQRCodeListener {
                                            override fun onScanQRCodeResult(result: String) {
                                                onPrivateKeyChange(result)
                                                focusManager.clearFocus()
                                            }
                                        })
                                    }) {
                                        Icon(
                                            imageVector = MangalaWalletPack.Scan,
                                            contentDescription = "Scan private key",
                                            tint = Colors.main1Text
                                        )
                                    }
                                }
                            },
                            keyboardType = KeyboardType.Password,
                            visualTransformation = PasswordVisualTransformation(),
                            interactionSource = interactionSource
                        )

                        uiState.error?.let {
                            VerticalSpacer(Spacing.XSMALL)
                            TextDescription2(it, color = Colors.main2)
                        }
                    }
                }
                if (!uiState.isLoading) {
                    MangalaButton(
                        label = MR.strings.all_continue.desc().localized(),
                        enabled = uiState.isImportButtonEnabled,
                        onClick = onClickContinue,
                        style = MangalaTypography.Size17Medium(),
                        modifier = Modifier.padding(horizontal = Dimensions.Padding.default).fillMaxWidth(),
                        disabledBackgroundColor = Colors.white,
                        disabledContentColor = Colors.mistGray
                    )

                    VerticalSpacer(Spacing.XTINY)
                    MangalaTextButton(
                        label = MR.strings.button_import_with_evm_snap.desc().localized(),
                        onClick = {
                            navigator.push(
                                ScreenRegistry.get(
                                    SharedScreen.ImportEOSAccountViaEVMScreen
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
                        size = MangalaButtonSize.XMedium,
                        contentColor = Colors.darkDarkGray,
                        enabled = true,
                        fontWeight = FontWeight.Medium,
                        fontSize = FontType.REGULAR,
                        fontStyle = FontStyle.Normal
                    )
                }
            }
        }
    }

    @Composable
    fun LoadingImportAccount() {
        TextSubTitle(
            text = MR.strings.title_loading_account.desc().localized(),
        )
        Spacer(Modifier.height(Spacing.SMALL))

        TextDescription2(
            text = MR.strings.message_loading_account.desc().localized(),
            fontSize = FontType.TINY_16
        )

        Spacer(Modifier.height(80.dp))
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painterResource(MR.images.loading_import_account),
                null,
                modifier = Modifier.size(273.dp)
            )
        }
    }
}