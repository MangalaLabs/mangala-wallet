package com.mangala.wallet.menu_base.presentation.security

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.biometry.presentation.IBiometryScreenModel
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Navigate
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.MangalaWalletSwitch
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class SecurityScreen : BaseScreen<SecurityScreenScreenModel>(), KoinComponent {

    override val screenName: String = MangalaAnalytics.Screens.SECURITY
    override val screenClassName: String = SecurityScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): SecurityScreenScreenModel {
        return getScreenModel()
    }

    @Composable
    override fun ScreenContent(screenModel: SecurityScreenScreenModel) {
        val biometryScreenModel = get<IBiometryScreenModel>()
        val navigator = LocalNavigator.currentOrThrow
        val biometryScreen = rememberScreen(
            SharedScreen.UnlockPinScreen(
                SharedScreen.UnlockPinScreen.ENABLE_BIOMETRY,
                antelopeAccountName = null
            )
        )

        Security(
            biometryScreenModel = biometryScreenModel,
            onBackPressed = navigator::pop,
            onNavigateBiometricClicked = { navigator.push(biometryScreen) }
        )
    }


    @Composable
    fun Security(
        biometryScreenModel: IBiometryScreenModel,
        onBackPressed: () -> Unit,
        onNavigateBiometricClicked: () -> Unit
    ) {
        OnboardingGradientBackground {
            Scaffold(
                topBar = {
                    MangalaWalletTopBarCenteredTitle(
                        title = MR.strings.all_security.desc().localized(),
                        onBackClicked = onBackPressed
                    )
                },
                modifier = Modifier.statusBarsPadding(),
                backgroundColor = Color.Transparent,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Spacing.SMALL)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(Spacing.BASE))
                    if (biometryScreenModel.isBiometricAvailable()) {
                        FaceIdRow(biometryScreenModel, onNavigateBiometricClicked)
                        Spacer(Modifier.height(Spacing.SMALL))
                    }
                    AdvancedSettingRow(onClickAdvancedSettings = {})
                }
            }
        }
    }

    @Composable
    fun FaceIdRow(
        biometryScreenModel: IBiometryScreenModel,
        onNavigateBiometricClicked: () -> Unit
    ) {
        val faceIdEnabled = biometryScreenModel.enableBiometricFlow.collectAsStateMultiplatform()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .background(
                    MaterialTheme.mangalaColors.bgInnerCard,
                    RoundedCornerShape(CornerRadius.Small)
                )
                .padding(
                    horizontal = Dimensions.Padding.default,
                    vertical = Dimensions.Padding.half
                )
                .fillMaxSize()
        ) {
            TextNormal(
                text = MR.strings.title_security_face_id.desc().localized(),
                modifier = Modifier.weight(1f),
                color = MaterialTheme.mangalaColors.textPrimary
            )
            MangalaWalletSwitch(
                checked = faceIdEnabled.value,
                onCheckedChange = {
                    if (it && biometryScreenModel.isBiometricAvailable()) {
                        onNavigateBiometricClicked()
                    }
                    if (!it) {
                        biometryScreenModel.enableBiometric(it)
                    }
                }
            )
        }
    }

    @Composable
    fun AdvancedSettingRow(onClickAdvancedSettings: () -> Unit) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .clip(RoundedCornerShape(CornerRadius.Small))
                .background(MaterialTheme.mangalaColors.bgInnerCard)
                .clickable(onClick = onClickAdvancedSettings)
                .padding(Dimensions.Padding.default)
                .fillMaxSize()
        ) {
            TextNormal(
                text = MR.strings.title_security_advanced_settings.desc().localized(),
                modifier = Modifier.weight(1f),
                color = MaterialTheme.mangalaColors.textPrimary
            )
            Icon(
                imageVector = MangalaWalletPack.Navigate,
                contentDescription = "Navigate",
                modifier = Modifier.width(20.dp).height(20.dp),
                tint = MaterialTheme.mangalaColors.iconPrimary
            )
        }
    }
}