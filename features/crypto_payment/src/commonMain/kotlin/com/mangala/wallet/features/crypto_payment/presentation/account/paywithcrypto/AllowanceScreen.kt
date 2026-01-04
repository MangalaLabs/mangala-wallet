package com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.MaxWidthBox
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.getSfProFamilyFont
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.utils.formatCurrencyAmount
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parameterArrayOf
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.utils.resolve
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import kotlin.jvm.Transient

class AllowanceScreen(
    @Transient private val paidAccountId: String,
    @Transient private val token: TokenBalanceModel,
    @Transient private val minimumAllowance: BigDecimal,
    @Transient private val onCallback: () -> Unit,
    @Transient private val onDismiss: () -> Unit,
): BaseScreen<AllowanceScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.PAY_WITH_CRYPTO_ALLOWANCE
    override val screenClassName: String = AllowanceScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): AllowanceScreenModel {
        return getScreenModel<AllowanceScreenModel> {
            parameterArrayOf(minimumAllowance, paidAccountId)
        }
    }

    @Composable
    override fun ScreenContent(screenModel: AllowanceScreenModel) {
        val localNavigator = LocalNavigator.currentOrThrow

        var allowanceAmount by remember { mutableStateOf("") }
        var isAllowanceValid by remember { mutableStateOf(false) }

        when(val uiState = screenModel.uiState.collectAsStateMultiplatform().value) {
            is AllowanceScreenUiState.Loading -> {}
            is AllowanceScreenUiState.Success -> {
                MaxWidthColumn(
                    modifier = Modifier.padding(horizontal = Dimensions.Padding.default)
                ) {
                    MaxWidthBox {
                        Text(
                            text = MR.strings.title_allowance_content.desc().localized(),
                            color = Colors.slateGray,
                            fontSize = FontType.REGULAR,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        IconButton(
                            onClick = { onDismiss() },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                tint = Colors.darkDarkGray,
                                contentDescription = null,
                            )
                        }
                    }
                    VerticalSpacer(Spacing.SMALL)
                    val allowanceFirstDescription = StringDesc.ResourceFormatted(
                        MR.strings.label_allowance_first_description,
                        token.contractSymbol
                    ).localized()
                    TextNormal(
                        text = buildAnnotatedString {
                            val placeholderStart = allowanceFirstDescription.indexOf(token.contractSymbol)
                            val placeholderEnd = placeholderStart + token.contractSymbol.length
                            append(allowanceFirstDescription)
                            addStyle(
                                style = SpanStyle(fontFamily = getSfProFamilyFont(FontWeight.Medium)),
                                start = placeholderStart,
                                end = placeholderEnd
                            )
                        },
                        color = Colors.darkDarkGray,
                        fontWeight = FontWeight.Normal,
                        fontSize = FontType.SMALL,
                    )
                    VerticalSpacer(Spacing.STINY)
                    TextNormal(
                        text = MR.strings.label_allowance_second_description.desc().localized(),
                        color = Colors.darkDarkGray,
                        fontWeight = FontWeight.Normal,
                        fontSize = FontType.SMALL,
                    )
                    VerticalSpacer(Spacing.XSMALL)
                    TextNormal(
                        text = MR.strings.label_allowance_amount.desc().localized(),
                        color = Colors.darkDarkGray,
                        fontWeight = FontWeight.Normal,
                        fontSize = FontType.SMALL
                    )
                    VerticalSpacer(Spacing.XTINY)
                    OutlinedTextField(
                        value = allowanceAmount,
                        onValueChange = {
                            isAllowanceValid = screenModel.isValidAllowance(it)
                            allowanceAmount = it
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Colors.grayPalate10,
                            unfocusedBorderColor = Colors.grayPalate10,
                            textColor = Colors.darkDarkGray
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(
                            fontFamily = getSfProFamilyFont(FontWeight.Normal),
                            fontSize = FontType.SMALL,
                            color = Colors.darkDarkGray
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(CornerRadius.Small),
                        singleLine = true
                    )
                    if (!isAllowanceValid) {
                        VerticalSpacer(Spacing.XTINY)
                        val allowanceError = StringDesc.ResourceFormatted(
                            MR.strings.label_allowance_invalid_amount,
                            formatCurrencyAmount(minimumAllowance, token.coinUid)
                        ).localized()
                        Text(
                            text = allowanceError,
                            color = Colors.red,
                            fontWeight = FontWeight.Normal,
                            fontSize = FontType.SMALL
                        )
                        VerticalSpacer(Spacing.HUGE)
                    } else {
                        VerticalSpacer(Spacing.XHUGE)
                    }
                    ButtonNormal(
                        text = MR.strings.button_allowance_approve_content.desc().localized(),
                        onClick = {
                            val pinScreen = ScreenRegistry.get(
                                SharedScreen.UnlockPinScreen(
                                    unlockPinCase = SharedScreen.UnlockPinScreen.CONFIRM_DAPP,
                                    unlockPinCallback = { isCorrectPin ->
                                        if (isCorrectPin) {
                                            screenModel.approveAllowanceAndCreateAccount(
                                                amountApproved = BigDecimal.parseString(allowanceAmount),
                                                tokenId = token.tokenId,
                                                tokenAddress = token.contractAddress
                                            )
                                        }
                                    },
                                    antelopeAccountName = null
                                )
                            )
                            localNavigator.push(pinScreen)
                        },
                        buttonModifier = Modifier.fillMaxWidth()
                            .padding(vertical = Dimensions.Padding.default),
                        backgroundColor = Colors.darkDarkGray,
                        disabledBackgroundColor = Colors.grayWhite,
                        textColor = Colors.white,
                        buttonMinSizeDefault = 44.dp,
                        enabled = isAllowanceValid
                    )
                }
            }

            is AllowanceScreenUiState.Approved -> {
                onCallback()
            }

            is AllowanceScreenUiState.Error -> {
                localNavigator.push(
                    ScreenRegistry.get(
                        SharedScreen.CryptoPaymentErrorScreen(
                            error = uiState.message.resolve()
                        )
                    )
                )
            }
        }
    }
}