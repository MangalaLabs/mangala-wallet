package com.mangala.wallet.features.chains.antelope.ram.presentation.details.bottomSheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.mangala.chart.market.MangalaMarketChart
import com.mangala.wallet.chart.market.Candle
import com.mangala.wallet.chart.market.MarketChartColors
import com.mangala.wallet.features.chains.antelope.ram.presentation.details.ButtonRowTime
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.XButton
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.DecimalFormat
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.formatDate
import com.mangala.wallet.utils.formatTime
import com.mangala.wallet.utils.truncateDecimal
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent

class ChartRamScreen(
    private val isLoading: Boolean,
    private val ramPrice: String,
    private val ramCurrency: String,
    private val pnlPercent: String,
    private val pnlColor: Color
) : BaseScreen<ChartRamScreenModel>(), KoinComponent {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_RAM_CHARTS
    override val screenClassName: String = ChartRamScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): ChartRamScreenModel = getScreenModel()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun ScreenContent(
        screenModel: ChartRamScreenModel,
    ) {
        val uiState by screenModel.uiState.collectAsStateMultiplatform()
        val uiModel = (uiState as? ChartRamUiState.Success)?.chartRamUiModel
        val bottomSheetNavigator = LocalBottomSheetNavigator.current

        MaxWidthColumn(
            Modifier
                .background(MaterialTheme.mangalaColors.bg)
                .navigationBarsPadding()
        ) {
            contentSheetBottomExpanded(
                isLoading,
                isClickIcon = { bottomSheetNavigator.hide() },
            )
            Spacer(modifier = Modifier.height(Spacing.BASE))
            ChartRam(
                uiModel?.ohlc.orEmpty(),
                uiModel?.chartTimeLabelMode
            )
            Spacer(modifier = Modifier.height(Spacing.XXXBASE))
            ButtonRowTime(
                selectedInterval = uiModel?.selectedInterval,
                onClick = { newSelectedInterval ->
                    screenModel.loadData(newSelectedInterval)
                }
            )
            Spacer(modifier = Modifier.height(Spacing.BASE))
        }
    }

    @Composable
    fun contentSheetBottomExpanded(
        isLoading: Boolean,
        isClickIcon: () -> Unit,
    ) {
        Box(modifier = Modifier.padding(Dimensions.Padding.default)) {
            Column {
                TextDescription2(
                    text ="${ramPrice.truncateDecimal(5)} $ramCurrency",
                    fontSize = FontType.REGULAR,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.mangalaColors.textSecondary,
                    modifier = Modifier.mangalaWalletPlaceholder(isLoading)
                        .align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(Spacing.MEDIUM))
                TextDescription2(
                    text = pnlPercent ,
                    fontSize = FontType.TINY,
                    fontWeight = FontWeight.Normal,
                    color = pnlColor,
                    modifier = Modifier.mangalaWalletPlaceholder(isLoading)
                        .align(Alignment.Start)
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopEnd)
                        .mangalaWalletPlaceholder(isLoading),
                    onClick = { isClickIcon() })
                {
                    Image(
                        modifier = Modifier.size(Dimensions.IconButtonSize),
                        imageVector = MangalaWalletPack.XButton,
                        contentDescription = null,
                    )
                }
            }
        }
    }

    @Composable
    fun ChartRam(
        candle: List<Candle>,
        chartTimeLabelMode: ChartTimeLabelMode?
    ) {
        val decimalFormat = remember { DecimalFormat("0.0000") }
        val currentTimeZone = remember { TimeZone.currentSystemDefault() }

        MangalaMarketChart(
            modifier = Modifier.fillMaxWidth().height(300.dp)
                .padding(horizontal = Dimensions.Padding.default),
            candles = candle,
            marketChartColors = MarketChartColors.defaults().copy(
                backgroundColor = MaterialTheme.mangalaColors.bg,
                positiveColor = Colors.green,
                negativeColor = Colors.coral,
                textColor = MaterialTheme.mangalaColors.textPrimary,
                lineColor = MaterialTheme.mangalaColors.border
            ),
            dateTransform = {
                val localDateTime = it.toLocalDateTime(currentTimeZone)

                when(chartTimeLabelMode) {
                    ChartTimeLabelMode.TIME_ONLY -> localDateTime.formatTime(currentTimeZone)
                    ChartTimeLabelMode.DATE_TIME -> localDateTime.formatTime(currentTimeZone) + "\n" + localDateTime.formatDate(currentTimeZone)
                    ChartTimeLabelMode.DATE_ONLY -> localDateTime.formatDate(currentTimeZone)
                    null -> ""
                }
            },
            priceTransform = {
                decimalFormat.format(it.toDouble())
            }
        )
    }
}

