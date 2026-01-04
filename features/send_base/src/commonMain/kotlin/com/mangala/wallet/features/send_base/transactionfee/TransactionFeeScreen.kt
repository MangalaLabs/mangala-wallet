package com.mangala.wallet.features.send_base.transactionfee

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.wallet.domain.transaction.fee.TransactionFeeType
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Heart
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Send
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.StarTransactionFee
import com.mangala.wallet.features.chains.ui.BitcoinFeeOptionUiModel
import com.mangala.wallet.features.chains.ui.EvmFeeOptionUiModel
import com.mangala.wallet.features.chains.ui.FeeOptionUiModel
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextTiny
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

class TransactionFeeScreen(
    private val transactionFeeOptions: List<FeeOptionUiModel>,
    @Transient private val onFeeSelected: (FeeOptionUiModel) -> Unit,
    @Transient private val onBackClicked: () -> Unit, // extracted so that this can be used whether it's in a bottom nav or not
): BaseScreen<TransactionFeeScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_TRANSACTION_FEE
    override val screenClassName: String = TransactionFeeScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): TransactionFeeScreenModel {
        return getScreenModel(
            parameters = {
                parametersOf(
                    transactionFeeOptions
                )
            }
        )
    }

    @Composable
    override fun ScreenContent(screenModel: TransactionFeeScreenModel) {
        val uiModel = screenModel.uiModel.collectAsStateMultiplatform().value

        TransactionFeeScreen(
            uiModel,
            onBackClicked = {
                onBackClicked()
            },
            onFeeSelected = {
                screenModel.setSelectedFeeOption(it)
            },
            onConfirm = {
                val selectedFeeOption = screenModel.getSelectedFeeOption()
                selectedFeeOption?.let { onFeeSelected(it) }
            },
        )
    }

    @Composable
    fun TransactionFeeScreen(
        uiModel: TransactionFeeScreenUiModel,
        onBackClicked: () -> Unit,
        onFeeSelected: (FeeOptionUiModel) -> Unit,
        onConfirm: () -> Unit
    ) {
        LazyColumn(
            modifier = Modifier
                .background(color = MaterialTheme.colors.primary)
                .padding(bottom = Dimensions.Padding.default),
            verticalArrangement = Arrangement.spacedBy(Spacing.SMALL)
        ) {
            item(ITEM_TITLE_KEY, contentType = ITEM_TITLE_KEY) {
                MaxWidthRow(
                    Modifier.padding(end = Dimensions.Padding.default), // No padding start since IconButton has intrinsic padding
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
                    TextNormal(
                        MR.strings.title_transaction_fee.desc().localized(),
                        color = Colors.main1Text,
                        fontWeight = FontWeight.W500
                    )
                }
            }
            item(ITEM_SPACER_KEY, contentType = ITEM_SPACER_KEY) {
                VerticalSpacer(Spacing.TINY)
            }
            item(ITEM_DESCRIPTION_KEY, contentType = ITEM_DESCRIPTION_KEY) {
                TextDescription2(
                    MR.strings.message_transaction_fee_screen_lorem_ipsum_dolor.desc().localized(),
                    color = Colors.caption,
                    modifier = Modifier.padding(horizontal = Dimensions.Padding.default)
                ) // TODO: Update placeholder text
            }
            items(uiModel.feeOptions, contentType = { "FeeOptionItem" }) { feeOption ->
                Column(Modifier.fillMaxWidth().padding(horizontal = Dimensions.Padding.default)) {
                    FeeOptionItem(feeOption, onFeeSelected, Modifier.fillMaxWidth())
                }
            }
            item(ITEM_SPACER_BOTTOM_KEY, contentType = ITEM_SPACER_BOTTOM_KEY) {
                VerticalSpacer(Spacing.TINY)
            }
            item(ITEM_CONFIRM_BUTTON_KEY, contentType = ITEM_CONFIRM_BUTTON_KEY) {
                Column(Modifier.fillMaxWidth().padding(horizontal = Dimensions.Padding.default)) {
                    ButtonNormal(
                        text = MR.strings.all_select.desc().localized(),
                        onClick = { onConfirm() },
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = FontType.REGULAR,
                        buttonModifier = Modifier.padding(vertical = 10.dp),
                    )
                }
            }
        }
    }

    @Composable
    private fun FeeOptionItem(
        uiModel: FeeOptionUiModel,
        onFeeSelected: (FeeOptionUiModel) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val borderColor = if (uiModel.isSelected) {
            Colors.second
        } else {
            Colors.stroke
        }
        val borderModifier = Modifier.border(
            width = 1.dp,
            color = borderColor,
            shape = RoundedCornerShape(
                CornerRadius.Small
            )
        )
        
        when (uiModel) {
            is EvmFeeOptionUiModel -> {
                EvmFeeOptionItem(uiModel, onFeeSelected, modifier, borderModifier)
            }
            is BitcoinFeeOptionUiModel -> {
                BitcoinFeeOptionItem(uiModel, onFeeSelected, modifier, borderModifier)
            }
        }
    }
    
    @Composable
    private fun EvmFeeOptionItem(
        uiModel: EvmFeeOptionUiModel,
        onFeeSelected: (FeeOptionUiModel) -> Unit,
        modifier: Modifier = Modifier,
        borderModifier: Modifier
    ) {
        MaxWidthColumn(modifier
            .then(
                borderModifier
                    .clip(RoundedCornerShape(CornerRadius.Small))
                    .clickable { onFeeSelected(uiModel) }
                    .padding(Dimensions.Padding.default)
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)
        ) {
            val (feeTypeStringResource, feeTypeIcon) = when (uiModel.transactionFee.transactionFeeType) {
                TransactionFeeType.ECONOMY -> MR.strings.label_transaction_fee_economy to MangalaWalletPack.Heart
                TransactionFeeType.REGULAR -> MR.strings.label_transaction_fee_regular to MangalaWalletPack.StarTransactionFee
                TransactionFeeType.FAST -> MR.strings.label_transaction_fee_fast to MangalaWalletPack.Send
            }

            val feeTypeText = feeTypeStringResource.desc().localized()

            val mainTextColor = if (uiModel.isSelected) Colors.second else Colors.main1Text

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.TINY),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        feeTypeIcon,
                        contentDescription = null,
                        Modifier.size(Dimensions.ImageTransactionFeeSize)
                    )
                    TextDescription2(text = feeTypeText, color = mainTextColor, fontWeight = FontWeight.W500)
                }
                TextDescription2(text = uiModel.transactionFeeValueString, color = mainTextColor, fontWeight = FontWeight.W500)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.TINY),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(Modifier.size(Dimensions.ImageTransactionFeeSize))
                    TextTiny(
                        text = StringDesc.ResourceFormatted(
                            MR.strings.message_transaction_fee_transaction_time,
                            uiModel.transactionFee.transactionFeeType.estimatedProcessingTimeMinutes.toString(),
                        ).localized(),
                        color = Colors.caption,
                    )
                }
                TextTiny(text = uiModel.transactionFeeFiatValueString, color = Colors.caption, textAlign = TextAlign.End)
            }
        }
    }
    
    @Composable
    private fun BitcoinFeeOptionItem(
        uiModel: BitcoinFeeOptionUiModel,
        onFeeSelected: (FeeOptionUiModel) -> Unit,
        modifier: Modifier = Modifier,
        borderModifier: Modifier
    ) {
        MaxWidthColumn(modifier
            .then(
                borderModifier
                    .clip(RoundedCornerShape(CornerRadius.Small))
                    .clickable { onFeeSelected(uiModel) }
                    .padding(Dimensions.Padding.default)
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)
        ) {
            val (feeTypeStringResource, feeTypeIcon) = when (uiModel.transactionFee.transactionFeeType) {
                TransactionFeeType.ECONOMY -> MR.strings.label_transaction_fee_economy to MangalaWalletPack.Heart
                TransactionFeeType.REGULAR -> MR.strings.label_transaction_fee_regular to MangalaWalletPack.StarTransactionFee
                TransactionFeeType.FAST -> MR.strings.label_transaction_fee_fast to MangalaWalletPack.Send
            }

            val feeTypeText = uiModel.label

            val mainTextColor = if (uiModel.isSelected) Colors.second else Colors.main1Text

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.TINY),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        feeTypeIcon,
                        contentDescription = null,
                        Modifier.size(Dimensions.ImageTransactionFeeSize)
                    )
                    TextDescription2(text = feeTypeText, color = mainTextColor, fontWeight = FontWeight.W500)
                }
                TextDescription2(text = uiModel.transactionFeeValueString, color = mainTextColor, fontWeight = FontWeight.W500)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.TINY),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(Modifier.size(Dimensions.ImageTransactionFeeSize))
                    TextTiny(
                        text = uiModel.description,
                        color = Colors.caption,
                    )
                }
                TextTiny(text = uiModel.transactionFeeFiatValueString, color = Colors.caption, textAlign = TextAlign.End)
            }
        }
    }

    companion object {
        const val ITEM_TITLE_KEY = "title"
        const val ITEM_SPACER_KEY = "spacer"
        const val ITEM_DESCRIPTION_KEY = "description"
        const val ITEM_SPACER_BOTTOM_KEY = "spacer-bottom"
        const val ITEM_CONFIRM_BUTTON_KEY = "confirm-button"
    }

}