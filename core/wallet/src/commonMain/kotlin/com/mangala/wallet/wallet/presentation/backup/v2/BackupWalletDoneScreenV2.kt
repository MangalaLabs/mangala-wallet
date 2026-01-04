package com.mangala.wallet.wallet.presentation.backup.v2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.mokoresources.MR
import dev.icerock.moko.resources.compose.painterResource
import com.mangala.wallet.ui.component.OnboardingButton
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.wallet.presentation.backup.BackupWalletDoneScreenModel
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

class BackupWalletDoneScreenV2 : BaseScreen<BackupWalletDoneScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.BACKUP_WALLET_DONE
    override val screenClassName: String = BackupWalletDoneScreenV2::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): BackupWalletDoneScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: BackupWalletDoneScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        val title = MR.strings.title_backed_up_wallet_done.desc().localized()
        val description = MR.strings.message_backed_up_wallet_done.desc().localized()

        OnboardingGradientBackground(
            circleBackgroundEnabled = true,
            afterBackgroundModifier = Modifier.navigationBarsPadding().imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar with progress dots
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
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
                    
                    // Progress dots (4 dots, all filled for completed)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(4) { index ->
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(2.dp)
                                    .background(
                                        color = Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(1.dp)
                                    )
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.size(40.dp)) // Balance the layout
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
                        text = "Wallet secured! 🔒",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        letterSpacing = (-0.2).sp,
                        lineHeight = 28.sp,
                        fontFamily = getInterFontFamily()
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // GenZ-friendly description alternatives that set proper expectations:
                    // "Keep your recovery phrase safe! It's the ONLY way back if you lose access."
                    // "Remember: No phrase = no access. Store it somewhere super safe!"
                    // "Your backup is ready! Just don't forget where you saved those 12 words 📝"
                    // "All set! Keep those words safe - we can't recover them for you."
                    // "Backup complete! Pro tip: Screenshot it, write it down, whatever works for you!"
                    // "You're in control now! Those 12 words = your wallet. Don't lose them!"
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
                        navigator.popUntilRoot()
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