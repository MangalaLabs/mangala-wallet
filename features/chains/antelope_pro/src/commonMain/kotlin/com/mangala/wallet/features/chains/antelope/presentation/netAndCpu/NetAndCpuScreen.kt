package com.mangala.wallet.features.chains.antelope.presentation.netAndCpu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextTopBar
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaTextButton
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.BuildEnvironmentProvider
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

class NetAndCpuScreen(private val accountName: String, private val isCPU: Boolean) :
    BaseScreen<NetAndCpuScreenModel>(), KoinComponent {

    override val screenName: String =
        if (isCPU) MangalaAnalytics.Screens.ANTELOPE_RESOURCE_CPU else MangalaAnalytics.Screens.ANTELOPE_RESOURCE_NET
    override val screenClassName: String = NetAndCpuScreen::class.simpleName.orEmpty()

    @delegate:Transient
    private val buildEnvironmentProvider: BuildEnvironmentProvider by inject()

    @Composable
    override fun createScreenModel(): NetAndCpuScreenModel {
        return getScreenModel(parameters = {
            parametersOf(isCPU)
        })
    }

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun ScreenContent(screenModel: NetAndCpuScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        val uiState = screenModel.uiState.collectAsStateMultiplatform().value

        CPUResourceScreen(
            uiState = uiState,
            onBackPressed = { navigator.pop() },
            onClickPowerUp = {
                navigator.push(
                    ScreenRegistry.get(
                        SharedScreen.PowerUpScreen(
                            accountName = accountName,
                            isCpu = isCPU
                        )
                    )
                )
            },
            onClickRex = {
                val screen = ScreenRegistry.get(
                    SharedScreen.RentViaRexScreen(
                        accountName = accountName,
                        isCpu = isCPU
                    )
                )
                navigator.push(screen)
            },
            onClickStaking = {
                val screen = ScreenRegistry.get(
                    SharedScreen.StakeForResourceScreen(
                        accountName = accountName,
                        isStakeRex = true,
                        isCpu = isCPU
                    )
                )
                navigator.push(screen)
            },
            onClickUnStaking = {
                val screen = ScreenRegistry.get(
                    SharedScreen.StakeForResourceScreen(
                        accountName = accountName,
                        isStakeRex = false,
                        isCpu = isCPU
                    )
                )
                navigator.push(screen)
            }
        )
    }

    @Composable
    fun CPUResourceScreen(
        uiState: NetAndCpuScreenUiState,
        onBackPressed: () -> Unit,
        onClickPowerUp: () -> Unit,
        onClickRex: () -> Unit,
        onClickStaking: () -> Unit,
        onClickUnStaking: () -> Unit,
    ) {
        val messageIntroduce = if (isCPU) StringDesc.ResourceFormatted(
            MR.strings.message_net_and_cpu_screen_introduce,
            MR.strings.all_antelope_cpu.desc().localized(),
        ).localized()
        else StringDesc.ResourceFormatted(
            MR.strings.message_net_and_cpu_screen_introduce,
            MR.strings.all_antelope_net.desc().localized(),
        ).localized()

        val titleIntroduce = if (isCPU) StringDesc.ResourceFormatted(
            MR.strings.title_net_and_cpu_screen_introduce,
            MR.strings.all_antelope_cpu.desc().localized(),
        ).localized()
        else StringDesc.ResourceFormatted(
            MR.strings.title_net_and_cpu_screen_introduce,
            MR.strings.all_antelope_net.desc().localized(),
        ).localized()
        val titleTopBar =
            if (isCPU)
                MR.strings.all_antelope_cpu.desc().localized()
            else
                MR.strings.all_antelope_net.desc().localized()

        MaxSizeColumn(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .background(MaterialTheme.mangalaColors.bg)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            MaxWidthColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                MangalaWalletTopBarCenteredTitle(
                    title = titleTopBar,
                    onBackClicked = onBackPressed,
                )
            }

            MaxWidthColumn(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                    .background(MaterialTheme.mangalaColors.bg)
                    .padding(start = Dimensions.Padding.default, end = Dimensions.Padding.default)
            ) {
                val loadedUiState = uiState as? NetAndCpuScreenUiState.Loaded

                Spacer(Modifier.height(46.dp))
                TextTopBar(
                    text = titleIntroduce,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.mangalaColors.textPrimary,
                )
                Spacer(Modifier.height(Spacing.TINY))
                TextDescription2(
                    text = messageIntroduce,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.mangalaColors.textSecondary,
                )
                Spacer(Modifier.height(Spacing.SMALL))

                val unitString = StringDesc.ResourceFormatted(
                    MR.strings.title_net_and_cpu_screen_unit,
                    if (isCPU) MR.strings.total_net_and_cpu_screen_unit_cpu.desc()
                        .localized() else MR.strings.total_net_and_cpu_screen_unit_net.desc()
                        .localized()
                ).localized()

                ResourceProviderCard(
                    title = MR.strings.all_antelope_power_up.desc().localized(),
                    cost = loadedUiState?.powerUpRateFormatted,
                    unit = unitString,
                    description = MR.strings.message_net_and_cpu_screen_description_power_up.desc()
                        .localized(),
                    buttonText = MR.strings.bottom_net_and_cpu_screen_power_up.desc().localized(),
                    onClick = onClickPowerUp
                )
                Spacer(Modifier.height(Spacing.SMALL))

                ResourceProviderCard(
                    title = MR.strings.title_net_and_cpu_screen_rex.desc().localized(),
                    cost = loadedUiState?.rexRateFormatted,
                    unit = unitString,
                    description = MR.strings.message_net_and_cpu_screen_description_rex.desc()
                        .localized(),
                    buttonText = MR.strings.bottom_net_and_cpu_screen_rex.desc().localized(),
                    onClick = onClickRex
                )
                Spacer(Modifier.height(Spacing.SMALL))

                if (buildEnvironmentProvider.isDevelopmentEnvironment()) {
                    ResourceProviderCard(
                        title = MR.strings.title_net_and_cpu_screen_staking.desc().localized(),
                        cost = loadedUiState?.stakingRateFormatted,
                        unit = unitString,
                        description = MR.strings.message_net_and_cpu_screen_description_staking.desc()
                            .localized(),
                        buttonText = MR.strings.bottom_net_and_cpu_screen_staking.desc()
                            .localized(),
                        onClick = onClickStaking
                    )
                    Spacer(Modifier.height(Spacing.SMALL))

                    ResourceProviderCard(
                        title = MR.strings.title_net_and_cpu_screen_un_staking.desc().localized(),
                        cost = loadedUiState?.stakingRateFormatted,
                        unit = unitString,
                        description = MR.strings.message_net_and_cpu_screen_description_un_staking.desc()
                            .localized(),
                        buttonText = MR.strings.bottom_net_and_cpu_screen_unstaking.desc()
                            .localized(),
                        onClick = onClickUnStaking
                    )
                }
                Spacer(Modifier.height(Spacing.SMALL))
            }
        }
    }

    @Composable
    fun ResourceProviderCard(
        title: String,
        cost: String? = null,
        unit: String,
        description: String,
        buttonText: String,
        onClick: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.Medium))
                .border(
                    1.dp,
                    color = MaterialTheme.mangalaColors.border,
                    RoundedCornerShape(CornerRadius.Medium)
                )
                .background(MaterialTheme.mangalaColors.bgInnerCard)
                .padding(horizontal = Dimensions.Padding.default),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(Spacing.SMALL))
            TextDescription2(
                text = title,
                color = MaterialTheme.mangalaColors.textPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = FontType.SMALL,
            )
            Spacer(Modifier.height(Spacing.SMALL))
            TextTopBar(
                text = cost ?: "0.0000", // string for placeholder
                color = MaterialTheme.mangalaColors.textPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.mangalaWalletPlaceholder(cost.isNullOrEmpty())
            )
            Spacer(Modifier.height(Spacing.XTINY))
            TextDescription2(
                text = unit,
                fontSize = FontType.SMALL,
                color = MaterialTheme.mangalaColors.textSecondary
            )
            Spacer(Modifier.height(Spacing.TINY))
            TextDescription2(
                text = description,
                fontSize = FontType.SMALL,
                color = MaterialTheme.mangalaColors.textSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(Spacing.BASE))
            MangalaTextButton(
                onClick = onClick,
                label = buttonText,
                size = MangalaButtonSize.Medium
            )
            Spacer(Modifier.height(Spacing.SMALL))
        }
    }
}
