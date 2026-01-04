package com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.antelope_key_manager.domain.model.AccountKeyPairs
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Dropdown
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.InfoCircle
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.PayWithCryptoBackground
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.ExecuteTransactionSuccess
import com.mangala.wallet.ui.component.HorizontalSpacer
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.getSfProFamilyFont
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.resolve
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.formatCurrencyAmount
import com.memtrip.eos.core.crypto.EosPrivateKey
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.koin.core.parameter.parameterArrayOf
import kotlin.jvm.Transient

class PayWithCryptoScreen(
    private val accountName: String,
    private val paidAccountId: String,
    private val accountBlockchainTypeUid: String,
    private val accountNameType: AccountNameType,
    private val eosOwnerPrivateKey: String? = null,
    private val eosActivePrivateKey: String? = null
) : BaseScreen<PayWithCryptoScreenModel>() {
    override val screenName: String = MangalaAnalytics.Screens.PAY_WITH_CRYPTO
    override val screenClassName: String = PayWithCryptoScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): PayWithCryptoScreenModel =
        getScreenModel<PayWithCryptoScreenModel> {
            parameterArrayOf(paidAccountId, accountBlockchainTypeUid, eosOwnerPrivateKey, eosActivePrivateKey)
        }

    override val isBottomBarVisible: Boolean = false

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: PayWithCryptoScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        val totalEvmPaid = remember { mutableStateOf<BigDecimal?>(null) }

        MangalaBottomSheetNavigator(
            sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            val bottomSheetNavigator = LocalBottomSheetNavigator.current

            MaxWidthColumn(
                Modifier
                    .fillMaxSize()
                    .background(Colors.appleBg)
                    .windowInsetsPadding(WindowInsets.safeDrawing),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                MaxWidthColumn {

                    LaunchedEffect(true) {
                        onBackPressedCallback = {
                            screenModel.changeToOriginalNetwork()
                            navigator.pop()
                            false
                        }

                        launch {
                            val flow = screenModel.checkAllowanceChannel.receiveAsFlow()
                            flow.collect { data ->
                                if (data.isEnoughAllowance) {
                                    val pinScreen = ScreenRegistry.get(SharedScreen.UnlockPinScreen(
                                        SharedScreen.UnlockPinScreen.CONFIRM_DAPP,
                                        unlockPinCallback = { isCorrectPin ->
                                            if (isCorrectPin) {
                                                screenModel.createAccount()
                                            }
                                        },
                                        antelopeAccountName = null
                                    ))
                                    navigator.push(pinScreen)
                                } else {
                                    val allowanceScreen = ScreenRegistry.get(
                                        SharedScreen.AllowanceScreen(
                                            paidAccountId = paidAccountId,
                                            minimumAllowance = data.tokenPaid,
                                            token = data.token,
                                            onCallback = {
                                                bottomSheetNavigator.hide()
                                                screenModel.createAccount()
                                            },
                                            onDismiss = {
                                                bottomSheetNavigator.hide()
                                            }
                                        )
                                    )
                                    bottomSheetNavigator.show(allowanceScreen)
                                }
                            }
                        }

                        launch {
                            screenModel.totalEvmSharedFlow.collect {
                                totalEvmPaid.value = it
                            }
                        }
                    }

                    MangalaWalletTopBar(
                        text = MR.strings.title_create_eos_account_paid_with_crypto.desc()
                            .localized(),
                        onBackClicked = {
                            screenModel.changeToOriginalNetwork()
                            navigator.pop()
                        }
                    )

                    when (val uiState = screenModel.uiState.collectAsStateMultiplatform().value) {
                        is CreateAccountByEvmUiState.Loading -> {
                            TextNormal(
                                text = MR.strings.all_loading.desc().localized(),
                                color = Colors.darkDarkGray
                            )
                        }

                        is CreateAccountByEvmUiState.Error -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(MR.strings.all_error.format(uiState.message).localized())
                            }
                            navigator.push(
                                ScreenRegistry.get(
                                    SharedScreen.CryptoPaymentErrorScreen(
                                        error = uiState.message.resolve(),
                                        errorDescription = null,
                                        accountBlockchainTypeUid
                                    )

                                )
                            )
                        }

                        is CreateAccountByEvmUiState.Success -> {
                            CreateAccountByEvmScreenContent(
                                accountName,
                                uiState.uiModel,
                                totalEvmPaid,
                                screenModel,
                                bottomSheetNavigator
                            )
                        }

                        is CreateAccountByEvmUiState.AccountCreatingSuccessfully -> {
                            val homeScreen = rememberScreen(SharedScreen.HomeScreen())
                            ExecuteTransactionSuccess(
                                onClickBack = {},
                                textTitle = MR.strings.label_pay_with_crypto_requested_success_description.desc().localized(),
                            ) {
                                Button(
                                    onClick = { navigator.replaceAll(homeScreen) },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Colors.darkDarkGray
                                    ),
                                    shape = RoundedCornerShape(CornerRadius.Tiny),
                                    enabled = true,
                                    modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 44.dp),
                                ) {
                                    Text(
                                        text = MR.strings.all_back_to_home.desc().localized(),
                                        color = Colors.white,
                                        fontFamily = getSfProFamilyFont(FontWeight.Medium),
                                        fontSize = FontType.REGULAR,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    @Composable
    fun CreateAccountByEvmScreenContent(
        accountName: String,
        uiModel: UiModel,
        totalEvmPaid: MutableState<BigDecimal?>,
        screenModel: PayWithCryptoScreenModel,
        bottomSheetNavigator: BottomSheetNavigator
    ) {
        val paymentMethod = remember { mutableStateOf<TokenBalanceModel?>(null) }
        MaxWidthColumn(
            modifier = Modifier.padding(Dimensions.Padding.default),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            VerticalSpacer(Spacing.SMALL)
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    imageVector = MangalaWalletPack.PayWithCryptoBackground,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.clip(CircleShape)
                )
            }
            VerticalSpacer(Spacing.BASE)
            PayWithCryptoDescription(
                "${
                    formatCurrencyAmount(
                        uiModel.totalEos,
                        accountBlockchainTypeUid
                    )
                } EOS"
            )
            VerticalSpacer(Spacing.SMALL)
            AccountDetails(accountNameType.name, accountName)
            VerticalSpacer(Spacing.SMALL)
            PaymentDetail(
                uiModel = uiModel,
                bottomSheetNavigator = bottomSheetNavigator,
                screenModel = screenModel,
                paymentMethod = paymentMethod,
                totalEvmPaid = totalEvmPaid
            )
            Spacer(modifier = Modifier.weight(1f))
            ButtonNormal(
                text = MR.strings.all_continue.desc().localized(),
                onClick = {
                    totalEvmPaid.value?.let { totalEvm ->
                        paymentMethod.value?.let {
                            screenModel.checkAllowance(
                                accountName,
                                totalEvm,
                                it
                            )
                        }
                    }
                },
                backgroundColor = Colors.darkDarkGray,
                textColor = Colors.white,
                buttonModifier = Modifier.fillMaxWidth(),
                disabledBackgroundColor = Colors.grayWhite,
                enabled = totalEvmPaid.value?.let { totalEvm ->
                    paymentMethod.value?.let {
                        totalEvm > BigDecimal.ZERO
                    } ?: false
                } ?: false,
                shape = RoundedCornerShape(CornerRadius.Tiny),
                fontSize = FontType.REGULAR,
                buttonMinSizeDefault = 44.dp
            )
            VerticalSpacer(Spacing.SMALL)
        }
    }

    @Composable
    fun PayWithCryptoDescription(amount: String) {
        val formattedDescription = StringDesc.ResourceFormatted(
            MR.strings.label_pay_with_crypto_description,
            amount
        ).localized()
        Text(
            text = buildAnnotatedString {
                val placeholderStart = formattedDescription.indexOf(amount)
                val placeholderEnd = placeholderStart + amount.length
                append(formattedDescription)
                addStyle(
                    style = SpanStyle(fontFamily = getSfProFamilyFont(FontWeight.Medium)),
                    start = placeholderStart,
                    end = placeholderEnd
                )
            },
            color = Colors.darkDarkGray,
            style = MaterialTheme.typography.body1,
            fontSize = FontType.SMALL,
            fontWeight = FontWeight.Normal,
            fontFamily = getSfProFamilyFont(FontWeight.Normal)
        )
    }

    @Composable
    fun AccountDetails(accountType: String, accountName: String) {
        Text(
            text = MR.strings.label_pay_with_crypto_account_detail.desc().localized(),
            fontSize = FontType.SMALL,
            color = Colors.darkGray,
            fontFamily = getSfProFamilyFont(FontWeight.Medium),
            fontWeight = FontWeight.Medium
        )
        VerticalSpacer(Spacing.TINY)
        MaxWidthColumn(
            modifier = Modifier.fillMaxWidth()
                .background(
                    color = Colors.white,
                    shape = RoundedCornerShape(CornerRadius.Medium)
                )
        ) {
            MaxWidthColumn(
                modifier = Modifier.fillMaxWidth()
                    .padding(Dimensions.Padding.default)
            ) {
                AccountDetailProperty(
                    label = MR.strings.label_pay_with_crypto_account_detail_account_type.desc()
                        .localized(),
                    value = accountType
                )
                VerticalSpacer(Spacing.SMALL)
                AccountDetailProperty(
                    label = MR.strings.label_pay_with_crypto_account_detail_account_name.desc()
                        .localized(),
                    value = accountName
                )
            }
        }
    }

    @Composable
    fun AccountDetailProperty(
        label: String,
        value: String
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontFamily = getSfProFamilyFont(FontWeight.Normal),
                color = Colors.darkDarkGray,
                fontWeight = FontWeight.Normal,
                fontSize = FontType.SMALL
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = value,
                fontFamily = getSfProFamilyFont(FontWeight.SemiBold),
                color = Colors.darkDarkGray,
                fontWeight = FontWeight.SemiBold,
                fontSize = FontType.SMALL
            )
        }
    }

    @Composable
    fun PaymentDetail(
        uiModel: UiModel,
        bottomSheetNavigator: BottomSheetNavigator,
        screenModel: PayWithCryptoScreenModel,
        paymentMethod: MutableState<TokenBalanceModel?>,
        totalEvmPaid: MutableState<BigDecimal?>
    ) {
        Text(
            text = MR.strings.label_pay_with_crypto_payment_detail.desc().localized(),
            fontSize = FontType.SMALL,
            color = Colors.darkGray,
            fontFamily = getSfProFamilyFont(FontWeight.Medium),
            fontWeight = FontWeight.Medium
        )
        VerticalSpacer(Spacing.TINY)
        MaxWidthColumn(
            modifier = Modifier.fillMaxWidth()
                .background(
                    color = Colors.white,
                    shape = RoundedCornerShape(CornerRadius.Medium)
                )
        ) {
            MaxWidthColumn(
                modifier = Modifier.fillMaxWidth()
                    .padding(Dimensions.Padding.default)
            ) {
                Row {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = MR.strings.label_pay_with_crypto_account_detail_account_type.desc()
                                .localized(),
                            fontFamily = getSfProFamilyFont(FontWeight.Normal),
                            color = Colors.darkDarkGray,
                            fontWeight = FontWeight.Normal,
                            fontSize = FontType.SMALL
                        )
                        HorizontalSpacer(Spacing.XTINY)
                        IconButton(
                            onClick = {
                                bottomSheetNavigator.show(
                                    ScreenRegistry.get(
                                        SharedScreen.PaymentDetailScreen(
                                            cpu = uiModel.cpu,
                                            net = uiModel.net,
                                            ram = uiModel.ram,
                                            serviceFee = uiModel.serviceFee,
                                            totalEos = uiModel.totalEos,
                                            onDismiss = {
                                                bottomSheetNavigator.hide()
                                            },
                                            coinUid = accountBlockchainTypeUid
                                        )
                                    )
                                )
                            },
                            modifier = Modifier.size(Dimensions.IconButtonSize14)
                        ) {
                            Icon(
                                MangalaWalletPack.InfoCircle,
                                contentDescription = null,
                                tint = Colors.darkDarkGray
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = MR.strings.all_unit_eos.format(formatCurrencyAmount(uiModel.totalEos, accountBlockchainTypeUid)).localized(),
                            fontFamily = getSfProFamilyFont(FontWeight.SemiBold),
                            color = Colors.darkDarkGray,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = FontType.SMALL
                        )
                    }
                    VerticalSpacer(Spacing.SMALL)
                }
                VerticalSpacer(Spacing.SMALL)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = MR.strings.label_pay_with_crypto_payment_detail_payment_token.desc().localized(),
                        fontFamily = getSfProFamilyFont(FontWeight.Normal),
                        color = Colors.darkDarkGray,
                        fontWeight = FontWeight.Normal,
                        fontSize = FontType.SMALL
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier.clickable {
                            val selectPaymentMethodScreen = ScreenRegistry.get(
                                SharedScreen.SelectPaymentMethodScreen(
                                    uiModel.accountSupportedList,
                                    onDismiss = {
                                        bottomSheetNavigator.hide()
                                    }
                                ) {
                                    paymentMethod.value = it
                                    screenModel.reCalculateTotalEvm(uiModel.totalEos, it)
                                    bottomSheetNavigator.hide()
                                }
                            )
                            bottomSheetNavigator.show(selectPaymentMethodScreen)
                        },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        paymentMethod.value?.let {
                            Text(
                                text = it.contractSymbol,
                                fontFamily = getSfProFamilyFont(FontWeight.SemiBold),
                                color = Colors.darkDarkGray,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = FontType.SMALL
                            )
                        } ?: run {
                            Text(
                                text = MR.strings.label_pay_with_crypto_payment_detail_select_payment_token_place_holder.desc().localized(),
                                fontFamily = getSfProFamilyFont(FontWeight.Normal),
                                color = Colors.gray,
                                fontWeight = FontWeight.Normal,
                                fontSize = FontType.SMALL
                            )
                        }
                        Icon(
                            MangalaWalletPack.Dropdown,
                            contentDescription = "Dropdown",
                            tint = Colors.darkDarkGray
                        )
                    }
//                    ExposedDropdownMenuBox(
//                        expanded = expanded,
//                        onExpandedChange = { expanded = !expanded }
//                    ) {
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.End
//                        ) {
//                            paymentMethod.value?.let {
//                                Text(
//                                    text = it.contractSymbol,
//                                    fontFamily = getSfProFamilyFont(FontWeight.SemiBold),
//                                    color = Colors.darkDarkGray,
//                                    fontWeight = FontWeight.SemiBold,
//                                    fontSize = FontType.SMALL
//                                )
//                            } ?: run {
//                                Text(
//                                    text = MR.strings.label_pay_with_crypto_payment_detail_select_payment_token_place_holder.desc().localized(),
//                                    fontFamily = getSfProFamilyFont(FontWeight.Normal),
//                                    color = Colors.gray,
//                                    fontWeight = FontWeight.Normal,
//                                    fontSize = FontType.SMALL
//                                )
//                            }
//                            Box(
//                                modifier = Modifier
//                                    .clickable(
//                                        enabled = true,
//                                        onClick = { expanded = true }
//                                    )
//                            ) {
//                                Icon(
//                                    MangalaWalletPack.Dropdown,
//                                    contentDescription = "Dropdown",
//                                    tint = Colors.darkDarkGray
//                                )
//                            }
//                        }
//                        ExposedDropdownMenu(
//                            expanded = expanded,
//                            onDismissRequest = { expanded = false },
//                            modifier = Modifier.exposedDropdownSize()
//                        ) {
//                            uiModel.accountSupportedList.forEach { account ->
//                                DropdownMenuItem(
//                                    onClick = {
//                                        paymentMethod.value = account
//                                        screenModel.reCalculateTotalEvm(uiModel.totalEos, account) {
//                                            totalEvmPaid.value = it
//                                        }
//                                        expanded = false
//                                    }
//                                ) {
//                                    Text(
//                                        text = account.contractSymbol,
//                                        fontFamily = getSfProFamilyFont(FontWeight.SemiBold),
//                                        color = Colors.darkDarkGray,
//                                        fontWeight = FontWeight.SemiBold,
//                                        fontSize = FontType.SMALL,
//                                        textAlign = TextAlign.End,
//                                        modifier = Modifier.fillMaxWidth()
//                                    )
//                                }
//                            }
//                        }
//                    }
                }
                VerticalSpacer(Spacing.SMALL)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = MR.strings.label_pay_with_crypto_payment_detail_equivalent.desc().localized(),
                        fontFamily = getSfProFamilyFont(FontWeight.Normal),
                        color = Colors.darkDarkGray,
                        fontWeight = FontWeight.Normal,
                        fontSize = FontType.SMALL
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = totalEvmPaid.value?.let {totalEvm ->
                            paymentMethod.value?.let {
                                "${formatCurrencyAmount(totalEvm, it.coinUid)} ${it.contractSymbol}"
                            } ?: MR.strings.label_pay_with_crypto_payment_detail_equivalent_default_value.desc().localized()
                        } ?: MR.strings.label_pay_with_crypto_payment_detail_equivalent_default_value.desc().localized(),
                        fontFamily = getSfProFamilyFont(FontWeight.SemiBold),
                        color = Colors.darkDarkGray,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = FontType.SMALL
                    )
                }
            }
        }
    }
}