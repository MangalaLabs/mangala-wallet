package com.mangala.wallet.features.receive.presentation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.BasicTextFieldWithHintAndTrailingIcons
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

class AddAmountToReceiveQrScreen(
    private val initialAmount: String,
    private val coinName: String,
    private val decimals: Long?,
    @Transient private val onSaveAmount: (String) -> Unit,
) : BaseScreen<AddAmountToReceiveQrScreenModel>(), KoinComponent {

    @Composable
    override fun createScreenModel(): AddAmountToReceiveQrScreenModel = getScreenModel(
        parameters = {
            parametersOf(
                initialAmount,
                decimals,
            )
        }
    )

    override val screenName: String = MangalaAnalytics.Screens.ADD_AMOUNT_TO_RECEIVE_QR
    override val screenClassName: String = AddAmountToReceiveQrScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun ScreenContent(screenModel: AddAmountToReceiveQrScreenModel) {
        val focusRequester = remember { FocusRequester() }

        val amount = screenModel.amount.collectAsStateMultiplatform()

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        OnboardingGradientBackground {
            MaxSizeColumn(
                modifier = Modifier
                    .padding(Dimensions.Padding.default)
                    .safeDrawingPadding()
            ) {
                Spacer(modifier = Modifier.height(Spacing.XXXBASE))

                Text(
                    text = MR.strings.message_receiveToken_addAmountBottomSheet.desc().localized(),
                    style = MangalaTypography.Size17SemiBold(),
                    color = MaterialTheme.mangalaColors.textPrimary,
                )

                Spacer(modifier = Modifier.height(Spacing.TINY))

                MaxWidthRow(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "$coinName ",
                        style = MangalaTypography.Size17SemiBold(),
                        color = MaterialTheme.mangalaColors.textPrimary,
                    )

                    BasicTextFieldWithHintAndTrailingIcons(
                        value = amount.value,
                        placeholder = {
                            Text(
                                text = MR.strings.placeholder_receiveToken_addAmountBottomSheet.desc()
                                    .localized(),
                                style = MangalaTypography.Size17SemiBold(),
                                color = MaterialTheme.mangalaColors.textSecondary,
                            )
                        },
                        onValueChange = screenModel::onAmountChange,
                        textStyle = MangalaTypography.Size17SemiBold().merge(
                            color = MaterialTheme.mangalaColors.textLink
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Decimal,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onSaveAmount(amount.value.text)
                            }
                        ),
                        trailingIcon = {},
                        textFieldModifier = Modifier.focusRequester(focusRequester)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                MangalaGradientButton(
                    label = MR.strings.all_save.desc().localized(),
                    onClick = {
                        onSaveAmount(amount.value.text)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}