package com.mangala.wallet.features.onboarding.presentation.trywithai

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.features.onboarding.presentation.TermsOfServiceScreen
import com.mangala.wallet.ui.component.MangalaBrandText
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.GradientTermsCheckbox
import com.mangala.wallet.ui.component.MangalaButtonStyle
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.component.PasskeyBottomSheet
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.launch

class TryWithAIScreen : BaseScreen<TryWithAIScreenModel>() {

    @Composable
    override fun createScreenModel(): TryWithAIScreenModel = getScreenModel()

    override val screenName: String = MangalaAnalytics.Screens.TRY_WITH_AI
    override val screenClassName: String = TryWithAIScreen::class.simpleName.orEmpty()

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun ScreenContent(screenModel: TryWithAIScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        var isTermsAgreed by remember { mutableStateOf(false) }
        var showPasskeyBottomSheet by remember { mutableStateOf(false) }
        val bottomSheetState = rememberModalBottomSheetState()
        val coroutineScope = rememberCoroutineScope()

        OnboardingGradientBackground(
            afterBackgroundModifier = Modifier.safeDrawingPadding().imePadding()
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {

                MaxSizeColumn {
                    MangalaWalletTopBarCenteredTitle(
                        title = "",
                        onBackClicked = navigator::pop
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Conversation demo image
                        LocalImage(
                            imageResource = MR.images.conversation_demo,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(450.dp)
                                .padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Title and description
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        ) {
                            // First line with Mangala brand
                            MangalaBrandText(
                                fullText = MR.strings.try_with_ai_title_meet.desc().localized(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 39.2.sp,
                                letterSpacing = (-0.28).sp
                            )

                            // Second line
                            Text(
                                text = MR.strings.try_with_ai_title_subtitle.desc().localized(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                letterSpacing = (-0.28).sp,
                                lineHeight = 39.2.sp,
                                fontFamily = getInterFontFamily()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = MR.strings.try_with_ai_description.desc().localized(),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFFD1D1D1),
                                textAlign = TextAlign.Center,
                                letterSpacing = (-0.17).sp,
                                lineHeight = 23.8.sp,
                                fontFamily = getInterFontFamily()
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Terms agreement checkbox
                        GradientTermsCheckbox(
                            isChecked = isTermsAgreed,
                            onCheckedChange = { isTermsAgreed = it },
                            onTermsClick = {
                                navigator.push(TermsOfServiceScreen())
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        MangalaGradientButton(
                            label = MR.strings.try_with_ai_button_continue.desc().localized(),
                            onClick = {
                                if (isTermsAgreed) {
                                    coroutineScope.launch {
                                        screenModel.completeOnboarding()
                                        val homeScreen = ScreenRegistry.get(
                                            SharedScreen.HomeScreen(
                                                SharedScreen.HomeScreen.InitialTab.CONVERSATION_UI
                                            )
                                        )
                                        navigator.replaceAll(homeScreen)
                                    }
                                }
                            },
                            enabled = isTermsAgreed,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        MangalaGradientButton(
                            label = MR.strings.try_with_ai_button_what_is_passkey.desc().localized(),
                            onClick = { showPasskeyBottomSheet = true },
                            buttonStyle = MangalaButtonStyle.TRANSPARENT,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }

                // Character image positioned at right edge of screen, below middle of conversation
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 300.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    LocalImage(
                        imageResource = MR.images.character,
                        modifier = Modifier
                            .size(120.dp)
                            .offset(x = -10.dp)
                    )
                }
            }
        }

        // Passkey Bottom Sheet
        PasskeyBottomSheet(
            showBottomSheet = showPasskeyBottomSheet,
            onDismiss = { showPasskeyBottomSheet = false },
            sheetState = bottomSheetState
        )
    }
}