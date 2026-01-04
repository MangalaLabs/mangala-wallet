package com.mangala.wallet.pin.presentation.forgot

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.*
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.getPlatform
import com.mangala.wallet.utils.PlatformType
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.delay

class ForgotPinScreenV2 : Screen {

    @Composable
    override fun Content() {
        LifecycleEffect(onStarted = {
            MangalaAnalytics.trackScreenView(
                MangalaAnalytics.Screens.FORGOT_PIN,
                ForgotPinScreenV2::class.simpleName.orEmpty()
            )
        })

        val navigator = LocalNavigator.currentOrThrow
        val resetWalletScreen = rememberScreen(SharedScreen.ResetWalletScreen)
        val restoreWalletScreen = rememberScreen(SharedScreen.RestoreWalletScreen)

        ForgotPinScreenV2Content({
            navigator.push(resetWalletScreen)
        }, {
            navigator.push(restoreWalletScreen)
        }, {
            navigator.pop()
        })
    }

    @Composable
    private fun ForgotPinScreenV2Content(
        onClickResetWallet: (Boolean) -> Unit,
        onClickRestoreWallet: (Boolean) -> Unit,
        onClickClose: (Boolean) -> Unit
    ) {
        var contentVisible by remember { mutableStateOf(false) }
        
        LaunchedEffect(Unit) {
            delay(100)
            contentVisible = true
        }

        OnboardingGradientBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Top Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { onClickClose(true) }
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

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Title and Description
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Forgot your PIN? 🤔",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Start,
                            letterSpacing = (-0.24).sp,
                            lineHeight = 33.6.sp,
                            fontFamily = getInterFontFamily()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No worries! You've got options. Your wallet is safe, and we'll help you get back in.",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFFA5B4CB),
                            textAlign = TextAlign.Start,
                            letterSpacing = (-0.16).sp,
                            lineHeight = 22.4.sp,
                            fontFamily = getInterFontFamily()
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    // Options section with animation
                    AnimatedVisibility(
                        visible = contentVisible,
                        enter = fadeIn(animationSpec = tween(600, delayMillis = 300)) + 
                                slideInVertically(
                                    initialOffsetY = { it / 4 },
                                    animationSpec = tween(600, delayMillis = 300)
                                ),
                        exit = fadeOut(animationSpec = tween(400)) + 
                               slideOutVertically(
                                   targetOffsetY = { it },
                                   animationSpec = tween(400)
                               )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            // Option 1: Restore wallet
                            OptionCard(
                                icon = "🔑",
                                title = "Restore with private key",
                                description = "Have your private key? Perfect! Restore your wallet in seconds.",
                                onClick = { onClickRestoreWallet(true) }
                            )

                            // Option 2: Reset wallet
                            OptionCard(
                                icon = "🔄",
                                title = "Start fresh",
                                description = "Reset everything and create a new wallet. Your current wallet will be erased.",
                                isDestructive = true,
                                onClick = { onClickResetWallet(true) }
                            )
                        }
                    }
                }
                
                // Bottom warning
                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 450)),
                    exit = fadeOut(animationSpec = tween(400))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⚠️ Resetting will permanently delete your current wallet. Make sure you have your recovery phrase!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF8B95A7),
                            textAlign = TextAlign.Center,
                            letterSpacing = (-0.12).sp,
                            lineHeight = 16.8.sp,
                            fontFamily = getInterFontFamily()
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    @Composable
    private fun OptionCard(
        icon: String,
        title: String,
        description: String,
        isDestructive: Boolean = false,
        onClick: () -> Unit
    ) {
        val gradient = if (isDestructive) {
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFFEF4444).copy(alpha = 0.1f),
                    Color(0xFFDC2626).copy(alpha = 0.1f)
                )
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFF3B90FF).copy(alpha = 0.1f),
                    Color(0xFFC27DFF).copy(alpha = 0.1f)
                )
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            backgroundColor = Color.Transparent,
            elevation = 0.dp
        ) {
            Box(
                modifier = Modifier
                    .background(gradient)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = icon,
                        fontSize = 32.sp,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDestructive) Color(0xFFEF4444) else Color.White,
                            letterSpacing = (-0.18).sp,
                            lineHeight = 25.2.sp,
                            fontFamily = getInterFontFamily()
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = description,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFFA5B4CB),
                            letterSpacing = (-0.14).sp,
                            lineHeight = 19.6.sp,
                            fontFamily = getInterFontFamily()
                        )
                    }
                }
            }
        }
    }
}