package com.mangala.wallet.features.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.getSfProFamilyFont
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

class CreateAccountNotificationScreen(
    private val isSuccess: Boolean,
    private val accountName: String,
    private val chainId: String,
    private val errorMessage: String = "",
    private val onDismiss: () -> Unit
) : BaseScreen<CreateAccountNotificationScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_PAY_WITH_CRYPTO_CREATE_ACCOUNT_NOTIFICATION
    override val screenClassName: String = CreateAccountNotificationScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean
        get() = false

    @Composable
    override fun createScreenModel(): CreateAccountNotificationScreenModel =
        getScreenModel<CreateAccountNotificationScreenModel>()

    @Composable
    override fun ScreenContent(screenModel: CreateAccountNotificationScreenModel) {
        val localNavigator = LocalNavigator.currentOrThrow
        println("=== localNavigator === $localNavigator")
        if (isSuccess) {
            NotificationDialog(
                titleResource = MR.strings.label_notification_account_created_successfully,
                descriptionResource = MR.strings.label_notification_account_created_successfully_description,
                onOK = {
                    println("=== pre push pin screen ===")
                    screenModel.updateEosAccountStatus(accountName, chainId, AntelopeAccount.CreateAccountState.DONE)
                    val pinScreen = ScreenRegistry.get(
                        SharedScreen.UnlockPinScreen(
                            SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION,
                            onUnlockSuccess = {
                                localNavigator.push(
                                    ScreenRegistry.get(
                                        SharedScreen.BackupAntelopeAccountScreen(
                                            accountName,
                                            BlockchainType.fromChainId(chainId).uid
                                        )
                                    )
                                )
                            },
                            antelopeAccountName = null
                        )
                    )
                    localNavigator.push(pinScreen)
                },
                onContactSupport = {}
            )
        } else {
            NotificationDialog(
                titleResource = MR.strings.label_notification_account_created_failure,
                descriptionResource = MR.strings.label_notification_account_created_failure_description,
                onOK = {
                    println("=== pre push pin screen ===")
                    screenModel.updateEosAccountStatus(accountName, chainId, AntelopeAccount.CreateAccountState.EVM_CREATE_ACCOUNT_FAILED)
                    onDismiss()
                },
                onContactSupport = {}
            )
        }
    }

    @Composable
    fun NotificationDialog(
        titleResource: StringResource,
        descriptionResource: StringResource,
        onOK: () -> Unit,
        onContactSupport: () -> Unit
    ) {
        Dialog(onDismissRequest = { }) {
            MaxWidthColumn(
                modifier = Modifier.padding(horizontal = Dimensions.Padding.default)
                    .background(
                        color = Colors.white,
                        shape = RoundedCornerShape(CornerRadius.BottomSheet)
                    ),
                verticalArrangement = Arrangement.Center,
            ) {
                MaxWidthColumn(
                    modifier = Modifier.padding(
                        top = Dimensions.Padding.double,
                        start = Dimensions.Padding.xDefault,
                        end = Dimensions.Padding.xDefault,
                        bottom = Dimensions.Padding.large
                    )
                ) {
                    val title = StringDesc.ResourceFormatted(
                        titleResource,
                        "EOS"
                    ).localized()
                    TextNormal(
                        text = title,
                        textAlign = TextAlign.Center,
                        fontSize = FontType.REGULAR,
                        fontWeight = FontWeight.Bold,
                        color = Colors.darkDarkGray
                    )
                    VerticalSpacer(Spacing.TINY)
                    val description = StringDesc.ResourceFormatted(
                        descriptionResource,
                        "EOS", accountName
                    ).localized()
                    TextNormal(
                        text = buildAnnotatedString {
                            val placeholderStart = description.indexOf(accountName)
                            val placeholderEnd = placeholderStart + accountName.length
                            append(description)
                            addStyle(
                                style = SpanStyle(fontFamily = getSfProFamilyFont(FontWeight.Medium)),
                                start = placeholderStart,
                                end = placeholderEnd
                            )
                        },
                        color = Colors.darkDarkGray,
                        fontWeight = FontWeight.Normal,
                        fontSize = FontType.SMALL,
                        textAlign = TextAlign.Center
                    )
                    VerticalSpacer(Spacing.BASE)
                    ButtonNormal(
                        text = MR.strings.all_ok.desc().localized(),
                        onClick = {
                            onOK()
                        },
                        fontSize = FontType.REGULAR,
                        textColor = Colors.white,
                        backgroundColor = Colors.darkDarkGray,
                        buttonModifier = Modifier.width(Dimensions.Width.huge)
                            .align(Alignment.CenterHorizontally),
                        buttonMinSizeDefault = 40.dp
                    )
                }
            }
        }
    }
}