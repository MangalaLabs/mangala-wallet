package com.mangala.wallet.pin.presentation.lock

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import com.mangala.wallet.ui.utils.navigation.BackHandler
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.getPlatform
import com.mangala.wallet.utils.PlatformType
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.delay

class LockScreenV2 : BaseScreen<LockScreenModel>() {

    @Composable
    override fun createScreenModel(): LockScreenModel = getScreenModel()

    override val screenName: String = MangalaAnalytics.Screens.LOCK
    override val screenClassName: String = LockScreenV2::class.simpleName.orEmpty()

    @Composable
    override fun ScreenContent(screenModel: LockScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val platformTexts = getLockPlatformSpecificTexts()
        var contentVisible by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            onBackPressedCallback = {
                if (navigator.canPop) {
                    false // Prevent the user from navigating back manually from the lock screen if has item in back stack
                } else {
                    true // Allow the user to exit the app
                }
            }
            delay(100)
            contentVisible = true
        }

        val observer = remember {
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    if (screenModel.isDeviceSecure()) {
                        if (navigator.canPop) {
                            navigator.pop()
                        } else {
                            val homeScreen = ScreenRegistry.get(SharedScreen.HomeScreen())
                            navigator.replaceAll(listOf(homeScreen))
                        }
                        return@LifecycleEventObserver
                    }
                }
            }
        }
        LocalLifecycleOwner.current.lifecycle.addObserver(observer)

        OnboardingGradientBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                // Main content with animation
                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(animationSpec = tween(600)) + 
                            slideInVertically(
                                initialOffsetY = { it / 6 },
                                animationSpec = tween(600)
                            ),
                    exit = fadeOut(animationSpec = tween(400)) + 
                           slideOutVertically(
                               targetOffsetY = { -it },
                               animationSpec = tween(400)
                           )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Lock emoji or icon
                        Text(
                            text = platformTexts.icon,
                            fontSize = 64.sp,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                        
                        // Title
                        Text(
                            text = platformTexts.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            letterSpacing = (-0.24).sp,
                            lineHeight = 33.6.sp,
                            fontFamily = getInterFontFamily(),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // Description
                        Text(
                            text = platformTexts.description,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFFA5B4CB),
                            textAlign = TextAlign.Center,
                            letterSpacing = (-0.16).sp,
                            lineHeight = 22.4.sp,
                            fontFamily = getInterFontFamily(),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(48.dp))
                        
                        // Additional info
                        Text(
                            text = platformTexts.additionalInfo,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF8B95A7),
                            textAlign = TextAlign.Center,
                            letterSpacing = (-0.14).sp,
                            lineHeight = 19.6.sp,
                            fontFamily = getInterFontFamily(),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }
        }
    }
}

data class LockScreenTexts(
    val icon: String,
    val title: String,
    val description: String,
    val additionalInfo: String
)

@Composable
fun getLockPlatformSpecificTexts(): LockScreenTexts {
    val baseMessage = MR.strings.os_screen_lock.desc().localized()
    
    return when (getPlatform().type) {
        PlatformType.ANDROID -> LockScreenTexts(
            icon = "🔐",
            title = "Device Lock Required",
            description = baseMessage,
            additionalInfo = "Enable screen lock in Settings → Security → Screen lock to protect your wallet"
        )
        PlatformType.IOS -> LockScreenTexts(
            icon = "🔒",
            title = "Device Passcode Required",
            description = baseMessage,
            additionalInfo = "Enable device passcode in Settings → Face ID & Passcode to secure your wallet"
        )
        PlatformType.DESKTOP -> LockScreenTexts(
            icon = "🛡️",
            title = "System Lock Required",
            description = baseMessage,
            additionalInfo = "Enable system authentication to protect your wallet access"
        )
    }
}