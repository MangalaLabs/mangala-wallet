package com.mangala.wallet.wallet.presentation.restore

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.*
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.getPlatform
import com.mangala.wallet.utils.PlatformType
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.delay

class RestoreWalletGuideScreenV2 : BaseScreen<RestoreWalletGuideScreenModel>() {

    @Composable
    override fun createScreenModel() = getScreenModel<RestoreWalletGuideScreenModel>()

    override val screenName: String = MangalaAnalytics.Screens.RESTORE_WALLET_GUIDE
    override val screenClassName: String = RestoreWalletGuideScreenV2::class.simpleName.orEmpty()

    @Composable
    override fun ScreenContent(screenModel: RestoreWalletGuideScreenModel) {
        LifecycleEffect(onStarted = {
            MangalaAnalytics.trackScreenView(
                screenName,
                screenClassName
            )
        })

        val navigator = LocalNavigator.currentOrThrow

        RestoreWalletGuideScreenV2Content(
            onBackClicked = { navigator.pop() },
            onRestoreWalletClicked = {
                val importPrivateKeyScreen = ScreenRegistry.get(SharedScreen.ImportPrivateKeyScreen)
                navigator.push(importPrivateKeyScreen)
            }
        )
    }
}

@Composable
fun RestoreWalletGuideScreenV2Content(
    onBackClicked: () -> Unit,
    onRestoreWalletClicked: () -> Unit
) {
    val platformTexts = getRestoreWalletPlatformSpecificTexts()
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
                            .clickable { onBackClicked() }
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

                // Title and Description
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = platformTexts.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        letterSpacing = (-0.28).sp,
                        lineHeight = 39.2.sp,
                        fontFamily = getInterFontFamily()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = platformTexts.subtitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3B90FF),
                        textAlign = TextAlign.Start,
                        letterSpacing = (-0.18).sp,
                        lineHeight = 25.2.sp,
                        fontFamily = getInterFontFamily()
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = platformTexts.description,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFA5B4CB),
                        textAlign = TextAlign.Start,
                        letterSpacing = (-0.16).sp,
                        lineHeight = 22.4.sp,
                        fontFamily = getInterFontFamily()
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // Requirements section with animation
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
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = platformTexts.requirementsTitle,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                letterSpacing = (-0.16).sp,
                                lineHeight = 22.4.sp,
                                fontFamily = getInterFontFamily()
                            )

                            // Requirement items
                            RequirementItem(
                                icon = "📝",
                                title = platformTexts.requirement1Title,
                                description = platformTexts.requirement1Desc
                            )

                            RequirementItem(
                                icon = "⏱️",
                                title = platformTexts.requirement2Title,
                                description = platformTexts.requirement2Desc
                            )

                            RequirementItem(
                                icon = "🔒",
                                title = platformTexts.requirement3Title,
                                description = platformTexts.requirement3Desc
                            )
                        }
                    }
                }
            }
            
            // Bottom section with button
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 450)) + 
                        slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(600, delayMillis = 450)
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
                        .padding(top = 8.dp, start = 24.dp, end = 24.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tips section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        backgroundColor = Color.Transparent,
                        elevation = 0.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF22C55E).copy(alpha = 0.1f),
                                            Color(0xFF16A34A).copy(alpha = 0.1f)
                                        )
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "💡",
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                
                                Column {
                                    Text(
                                        text = platformTexts.tipTitle,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF22C55E),
                                        letterSpacing = (-0.14).sp,
                                        lineHeight = 19.6.sp,
                                        fontFamily = getInterFontFamily()
                                    )
                                    
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    Text(
                                        text = platformTexts.tipDescription,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color(0xFFA5B4CB),
                                        letterSpacing = (-0.13).sp,
                                        lineHeight = 18.2.sp,
                                        fontFamily = getInterFontFamily()
                                    )
                                }
                            }
                        }
                    }
                    
                    // Restore button
                    Button(
                        onClick = onRestoreWalletClicked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF3B90FF)
                        )
                    ) {
                        Text(
                            text = platformTexts.buttonText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            letterSpacing = (-0.16).sp,
                            fontFamily = getInterFontFamily()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RequirementItem(
    icon: String,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF3B90FF).copy(alpha = 0.1f),
                            Color(0xFFC27DFF).copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = icon,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        letterSpacing = (-0.16).sp,
                        fontFamily = getInterFontFamily()
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFA5B4CB),
                        letterSpacing = (-0.16).sp,
                        fontFamily = getInterFontFamily()
                    )
                }
            }
        }
    }
}

data class RestoreWalletGuideTexts(
    val title: String,
    val subtitle: String,
    val description: String,
    val requirementsTitle: String,
    val requirement1Title: String,
    val requirement1Desc: String,
    val requirement2Title: String,
    val requirement2Desc: String,
    val requirement3Title: String,
    val requirement3Desc: String,
    val tipTitle: String,
    val tipDescription: String,
    val buttonText: String
)

@Composable
fun getRestoreWalletPlatformSpecificTexts(): RestoreWalletGuideTexts {
    val baseTitle = MR.strings.button_restore_wallet.desc().localized()
    val baseDescription = MR.strings.message_restore_wallet_private_key.desc().localized()
    
    return when (getPlatform().type) {
        PlatformType.ANDROID -> RestoreWalletGuideTexts(
            title = "Welcome back! 👋",
            subtitle = "Let's restore your wallet",
            description = baseDescription,
            requirementsTitle = "What you'll need:",
            requirement1Title = "Your private key",
            requirement1Desc = "The private key you saved when creating your wallet",
            requirement2Title = "A few minutes",
            requirement2Desc = "The restoration process is quick and secure",
            requirement3Title = "Private space",
            requirement3Desc = "Make sure no one can see your screen",
            tipTitle = "Pro tip",
            tipDescription = "Double-check your private key as you type. Keep it secure!",
            buttonText = "Start Restoration 🚀"
        )
        PlatformType.IOS -> RestoreWalletGuideTexts(
            title = baseTitle,
            subtitle = "Restore from Private Key",
            description = baseDescription,
            requirementsTitle = "Before you begin:",
            requirement1Title = "Private key ready",
            requirement1Desc = "The private key you saved when creating your wallet",
            requirement2Title = "Time required",
            requirement2Desc = "Approximately 2-3 minutes",
            requirement3Title = "Secure environment",
            requirement3Desc = "Ensure your privacy during restoration",
            tipTitle = "Helpful hint",
            tipDescription = "Ensure you have the correct private key before proceeding",
            buttonText = "Continue"
        )
        PlatformType.DESKTOP -> RestoreWalletGuideTexts(
            title = "Wallet Recovery",
            subtitle = "Import Existing Wallet",
            description = baseDescription,
            requirementsTitle = "Prerequisites:",
            requirement1Title = "Private key",
            requirement1Desc = "The private key you saved when creating your wallet",
            requirement2Title = "Estimated time",
            requirement2Desc = "Process takes 2-5 minutes",
            requirement3Title = "Security check",
            requirement3Desc = "Verify no screen recording or observers",
            tipTitle = "Important note",
            tipDescription = "Ensure your private key is entered correctly and securely",
            buttonText = "Begin Recovery Process"
        )
    }
}