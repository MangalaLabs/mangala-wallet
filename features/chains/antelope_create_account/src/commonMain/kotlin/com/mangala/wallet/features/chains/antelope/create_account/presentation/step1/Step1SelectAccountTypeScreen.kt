package com.mangala.wallet.features.chains.antelope.create_account.presentation.step1

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.linh.antelope_qr.domain.model.CreateAccountForFriendRequest
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step2.Step2SelectAccountNameScreen
import com.mangala.wallet.features.chains.antelope.create_account.presentation.ui.SelectionOption
import com.mangala.wallet.features.chains.antelope.create_account.presentation.ui.SelectionOptionText
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step3.createbyfriend.CreateByFriendBottomSheetUiState
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.scanqr.ScanQRCodeListener
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextSubTitle
import com.mangala.wallet.ui.component.CreateImportButton
import com.mangala.wallet.ui.component.GradientBackground
import com.mangala.wallet.ui.component.MangalaButton
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.jvm.Transient

class Step1SelectAccountTypeScreen: BaseScreen<Step1SelectAccountTypeScreenModel>(), KoinComponent {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_CREATE_ACCOUNT_SELECT_ACCOUNT_TYPE
    override val screenClassName: String = Step1SelectAccountTypeScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): Step1SelectAccountTypeScreenModel {
        return getScreenModel<Step1SelectAccountTypeScreenModel>()
    }

    override val isBottomBarVisible: Boolean = false

    @delegate:Transient
    private val scanQRCode: ScanQRCode by inject()

    @Composable
    override fun ScreenContent(screenModel: Step1SelectAccountTypeScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val uiState = screenModel.uiState.collectAsStateMultiplatform().value

        EOSAccountCreationScreen(
            uiState,
            onClickBack = {
                navigator.pop()
            },
            onClickAccountType = {
                screenModel.setSelectedAccountType(it)
            },
            onClickContinue = {
                when(it) {
                    AccountNameType.Standard, AccountNameType.Premium -> navigator.push(
                        Step2SelectAccountNameScreen(it)
                    )
                    AccountNameType.Friend -> {
                        // TODO: Pass in some args to disable receive and also change text for the QR scanning screen
                        scanQRCode.scanQRCode(
                            scanQRCodeListener = object : ScanQRCodeListener {
                                override fun onScanQRCodeResult(result: String) {
                                    val parseResult = screenModel.onScanQrCodeResult(result)

                                    if (parseResult != null && parseResult is QrCodeData.AntelopeCreateAccountForFriend) {
                                        (parseResult.request as? CreateAccountForFriendRequest)?.let { createRequest ->
                                            val screen = ScreenRegistry.get(
                                                SharedScreen.CreateAccountForFriendScreen(
                                                    accountName = createRequest.accountName,
                                                    activePublicKey = createRequest.activePublicKey,
                                                    ownerPublicKey = createRequest.ownerPublicKey,
                                                    blockchainUid = createRequest.blockchainUid
                                                )
                                            )
                                            navigator.push(screen)
                                        }
                                    }
                                }
                            },
                        )
                    }
                    else -> {}
                }
            }
        )
    }

    @Composable
    fun EOSAccountCreationScreen(
        uiState: Step1SelectAccountTypeUiState,
        onClickBack: () -> Unit,
        onClickAccountType: (AccountNameType) -> Unit,
        onClickContinue: (selectedAccountType: AccountNameType) -> Unit
    ) {
        GradientBackground {
            MaxSizeColumn(verticalArrangement = Arrangement.SpaceBetween) {
                MaxWidthColumn {
                    MangalaWalletTopBar(
                        modifier = Modifier.background(Color.Transparent),
                        text = "",
                        onBackClicked = onClickBack
                    )
                }
                MaxWidthColumn(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = Dimensions.Padding.default),
                ) {
                    VerticalSpacer(Spacing.LARGE)
                    TextSubTitle(
                        text = MR.strings.title_step_1_select_account_type_start_journey.desc().localized(),
                        fontWeight = FontWeight.Medium,
                        color = Colors.darkDarkGray
                    )
                    VerticalSpacer(Spacing.SMALL)
                    TextDescription2(
                        text = MR.strings.label_step_1_select_account_type_start_journey_description.desc().localized(),
                        color = Colors.darkDarkGray
                    )
                    VerticalSpacer(Spacing.XBASE)
                    TextNormal(
                        text = MR.strings.label_step_1_select_account_type_start_journey_prompt.desc().localized(),
                        fontWeight = FontWeight.Medium,
                        color = Colors.darkDarkGray
                    )
                    VerticalSpacer(Spacing.SMALL)
                    ListAccountType(
                        uiState.selectedAccountType,
                        onClickItem = onClickAccountType
                    )
                }
                MangalaButton(
                    label = MR.strings.all_continue.desc().localized(),
                    enabled = (uiState as? Step1SelectAccountTypeUiState.Success)?.createButtonEnabled == true,
                    onClick = {
                        (uiState as? Step1SelectAccountTypeUiState.Success)?.let {
                            onClickContinue(it.selectedAccountType)
                        }
                    },
                    modifier = Modifier.padding(horizontal = Dimensions.Padding.default).fillMaxWidth(),
                    style = MangalaTypography.Size17Medium(),
                    disabledBackgroundColor = Color.White,
                    disabledContentColor = Colors.mistGray
                )
            }
        }
    }
}

@Composable
fun ListAccountType(
    accountTypeSelected: AccountNameType,
    onClickItem: (AccountNameType) -> Unit,
    forceHideCreateAccountForFriend: Boolean = false
) {
    AccountNameOption(
        title = MR.strings.all_antelope_standard_account_name.desc().localized(),
        description = MR.strings.label_step_1_select_account_type_standard_account_description.desc().localized(),
        example = "exampleuser1, johnsmith123",
        isSelected = accountTypeSelected == AccountNameType.Standard,
        onClick = { onClickItem(AccountNameType.Standard) }
    )
    VerticalSpacer(Spacing.SMALL)
    AccountNameOption(
        title = MR.strings.all_antelope_premium_account_name.desc().localized(),
        description = MR.strings.label_step_1_select_account_type_premium_account_description.desc().localized(),
        example = "jane.man, crypto.man",
        isSelected = accountTypeSelected == AccountNameType.Premium,
        onClick = { onClickItem(AccountNameType.Premium) }
    )
    VerticalSpacer(Spacing.SMALL)
    // norelease hide create account for friend since we haven't implemented UI for this
//    if (forceHideCreateAccountForFriend.not()) {
//        AccountNameOption(
//            title = MR.strings.title_step_1_select_account_type_create_for_friend.desc().localized(),
//            description = MR.strings.label_step_1_select_account_type_create_for_friend_description.desc().localized(),
//            example = "exampleuser1, johnsmith123",
//            isSelected = accountTypeSelected == AccountNameType.Friend,
//            onClick = { onClickItem(AccountNameType.Friend) }
//        )
//    }
}

@Composable
fun AccountNameOption(
    title: String,
    description: String,
    example: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    SelectionOption(
        onClick = onClick,
        selected = isSelected,
        paddingStart = Spacing.SMALL,
        paddingEnd = Spacing.SMALL,
        paddingTop = Spacing.XSMALL,
        paddingBottom = Spacing.XSMALL
    ) {
        MaxWidthColumn(Modifier.animateContentSize()) {
            MaxWidthRow(verticalAlignment = Alignment.CenterVertically) {
                SelectionOptionText(title, color = Colors.black)
                // norelease hide info button since we haven't implemented UI for this
//                HorizontalSpacer(Spacing.TINY)
//                Icon(
//                    MangalaWalletPack.InfoCircle,
//                    contentDescription = null,
//                    modifier = Modifier.size(14.dp)
//                )
            }
            MaxWidthColumn(verticalArrangement = Arrangement.spacedBy(Spacing.TINY)) {
                TextDescription2(
                    text = description,
                    color = Colors.appleSubText
                )
                example?.let {
                    TextDescription2(
                        text = MR.strings.label_step_1_select_account_type_example.format(example)
                            .localized(),
                        color = Colors.appleSubText
                    )
                }
            }
        }
    }
}