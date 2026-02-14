package com.mangala.wallet.features.onboarding.presentation.notification

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.features.onboarding.presentation.onboarding.OnboardingScreen
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.MangalaButtonStyle
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

class PrePermissionScreen : BaseScreen<PrePermissionScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.NOTIFICATION_PERMISSION
    override val screenClassName: String = "PrePermissionScreen"

    @Composable
    override fun createScreenModel(): PrePermissionScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: PrePermissionScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        fun navigateToOnboarding() {
            navigator.replace(OnboardingScreen())
        }
        val requestNotificationPermission = rememberNotificationPermissionRequester {
            screenModel.onNotifyMeResult(
                onSaved = { granted ->
                    if (granted) {
                        navigateToOnboarding()
                    }
                },
                isGranted = it
            )
        }

        OnboardingGradientBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.weight(1f))

                // TODO: Replace with a proper notification bell icon
                LocalImage(
                    imageResource = MR.images.character,
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = MR.strings.pre_permission_title.desc().localized(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = MR.strings.pre_permission_description.desc().localized(),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFD1D1D1),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.weight(2f))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MangalaGradientButton(
                        label = MR.strings.pre_permission_button_notify_me.desc().localized(),
                        onClick = {
                            requestNotificationPermission()
                        },
                        buttonStyle = MangalaButtonStyle.GRADIENT,
                        modifier = Modifier.fillMaxWidth()
                    )
                    MangalaGradientButton(
                        label = MR.strings.pre_permission_button_maybe_later.desc().localized(),
                        onClick = {
                            screenModel.onMaybeLater {
                                navigateToOnboarding()
                            }
                        },
                        buttonStyle = MangalaButtonStyle.TRANSPARENT,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
