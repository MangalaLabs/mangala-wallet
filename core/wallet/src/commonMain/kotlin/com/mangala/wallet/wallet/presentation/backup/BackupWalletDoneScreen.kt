package com.mangala.wallet.wallet.presentation.backup

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.mokoresources.MR
import dev.icerock.moko.resources.compose.painterResource
import com.mangala.wallet.ui.component.OnboardingButton
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics

class BackupWalletDoneScreen : BaseScreen<BackupWalletDoneScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.BACKUP_WALLET_DONE
    override val screenClassName: String = BackupWalletDoneScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): BackupWalletDoneScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: BackupWalletDoneScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val globalNavigator = LocalGlobalNavigator.current
        val homeScreen = rememberScreen(SharedScreen.HomeScreen())

        OnboardingGradientBackground(
            circleBackgroundEnabled = true,
            afterBackgroundModifier = Modifier.navigationBarsPadding().imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar with back button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { navigator.pop() }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MangalaWalletPack.ArrowLeft,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Title and Description (aligned left at top)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Wallet secured!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        letterSpacing = (-0.2).sp,
                        lineHeight = 28.sp,
                        fontFamily = getInterFontFamily()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Keep your recovery phrase safe! It's the ONLY way back if you lose access.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFA5B4CB),
                        textAlign = TextAlign.Start,
                        letterSpacing = (-0.14).sp,
                        lineHeight = 19.6.sp,
                        fontFamily = getInterFontFamily()
                    )
                }

                Spacer(modifier = Modifier.height(56.dp))

                // Success Icon - centered below description
                Image(
                    painter = painterResource(MR.images.success_image),
                    contentDescription = "Success",
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Explore Mangala Button
                OnboardingButton(
                    text = "Explore Mangala",
                    onClick = {
                        globalNavigator.replaceAll(homeScreen)
                    },
                    isPrimary = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp)
                )
            }
        }
    }
}
