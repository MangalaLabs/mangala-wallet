package com.mangala.wallet.features.onboarding.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.mangala.wallet.features.onboarding.domain.model.OnboardingPage
import com.mangala.wallet.features.onboarding.presentation.components.AIAssistantButton
import com.mangala.wallet.ui.component.MangalaBrandText
import com.mangala.wallet.features.onboarding.presentation.components.OnboardingPageIndicator
import com.mangala.wallet.features.onboarding.presentation.trywithai.TryWithAIScreen
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaButtonStyle
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

class OnboardingScreen : BaseScreen<OnboardingScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ONBOARDING
    override val screenClassName: String = OnboardingScreen::screenName.name
    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): OnboardingScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: OnboardingScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        // Resolve localized strings outside remember
        val page1Title = MR.strings.onboarding_page_1_title.desc().localized()
        val page1Description = MR.strings.onboarding_page_1_description.desc().localized()
        val page2Title = MR.strings.onboarding_page_2_title.desc().localized()
        val page2Description = MR.strings.onboarding_page_2_description.desc().localized()
        val page3Title = MR.strings.onboarding_page_3_title.desc().localized()
        val page3Description = MR.strings.onboarding_page_3_description.desc().localized()
        val page4Title = MR.strings.onboarding_page_4_title.desc().localized()
        val page4Description = MR.strings.onboarding_page_4_description.desc().localized()
        
        val onboardingPages = remember(
            page1Title, page1Description,
            page2Title, page2Description,
            page3Title, page3Description,
            page4Title, page4Description
        ) {
            listOf(
                OnboardingPage(
                    title = page1Title,
                    description = page1Description,
                    imageResourceId = IMAGE_LOGO
                ),
                OnboardingPage(
                    title = page2Title,
                    description = page2Description,
                    imageResourceId = IMAGE_LOADING_IMPORT
                ),
                OnboardingPage(
                    title = page3Title,
                    description = page3Description,
                    imageResourceId = IMAGE_TRANSFER_SUCCESS
                ),
                OnboardingPage(
                    title = page4Title,
                    description = page4Description,
                    imageResourceId = IMAGE_TRANSACTION_HISTORY_EMPTY
                )
            )
        }

        val pagerState = rememberPagerState(pageCount = { onboardingPages.size })

        OnboardingGradientBackground(
            afterBackgroundModifier = Modifier.safeDrawingPadding().imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Content area with pager and indicator - full width for smooth swiping
                // Pager content - includes both image and text
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) { pageIndex ->
                    val page = onboardingPages[pageIndex]
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp), // Apply padding inside each page
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        // Add flexible space at top for better centering
                        Spacer(modifier = Modifier.weight(0.1f))

                        // Dynamic image for each page
                        page.imageResourceId?.let { imageId ->
                            val imageResource = when (imageId) {
                                IMAGE_LOGO -> MR.images.character
                                IMAGE_LOADING_IMPORT -> MR.images.character
                                IMAGE_TRANSFER_SUCCESS -> MR.images.character
                                IMAGE_TRANSACTION_HISTORY_EMPTY -> MR.images.character
                                else -> MR.images.character
                            }

                            LocalImage(
                                imageResource = imageResource,
                                modifier = Modifier.size(200.dp) // Slightly larger for better visual impact
                            )
                        } ?: Box(
                            modifier = Modifier
                                .size(200.dp)
                                .background(
                                    Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            val fallbackEmoji = MR.strings.onboarding_fallback_emoji.desc().localized()
                            Text(
                                text = fallbackEmoji,
                                fontSize = 60.sp,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(48.dp)) // Optimized spacing between image and text

                        // Text content
                        OnboardingPageContent(
                            page = page,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp) // Additional padding for text readability
                        )

                        // Add flexible space at bottom
                        Spacer(modifier = Modifier.weight(0.3f))
                    }
                }

                // Page indicator positioned at the bottom of content area
                OnboardingPageIndicator(
                    pageCount = onboardingPages.size,
                    currentPage = pagerState.currentPage,
                    currentPageOffset = pagerState.currentPageOffsetFraction,
                )

                Spacer(modifier = Modifier.height(32.dp)) // Space between content and buttons

                // Action buttons with horizontal padding
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp) // Add horizontal padding for buttons
                        .padding(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp) // Tighter button spacing
                ) {
                    // AI Assistant Button
                    AIAssistantButton(
                        onClick = {
                            MangalaAnalytics.trackEvent(
                                MangalaAnalytics.EventName.ONBOARDING_INITIATED,
                                mapOf(MangalaAnalytics.EventParam.ONBOARDING_STEP_NAME to MangalaAnalytics.EventParamValue.ONBOARDING_STEP_CONVERSATION_UI)
                            )

                            navigator.push(TryWithAIScreen())
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp) // Only top padding for AI button
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    MangalaGradientButton(
                        label = MR.strings.onboarding_button_create_wallet.desc().localized(),
                        onClick = {
                            MangalaAnalytics.trackEvent(
                                MangalaAnalytics.EventName.ONBOARDING_INITIATED,
                                mapOf(MangalaAnalytics.EventParam.ONBOARDING_STEP_NAME to MangalaAnalytics.EventParamValue.ONBOARDING_STEP_CREATE_WALLET)
                            )

                            if (screenModel.isPinSetup()) {
                                // PIN already set, go directly to create wallet screen
                                val createWalletScreen = ScreenRegistry.get(screenModel.getCreateWalletScreen())
                                navigator.push(createWalletScreen)
                            } else {
                                // PIN not set, go to setup PIN first with callback
                                val setupPinScreen = ScreenRegistry.get(
                                    screenModel.getSetupPinScreen {
                                        // After PIN setup success, navigate to create wallet screen
                                        navigator.push(
                                            ScreenRegistry.get(screenModel.getCreateWalletScreen())
                                        )
                                    }
                                )
                                navigator.push(setupPinScreen)
                            }
                        },
                        buttonStyle = MangalaButtonStyle.GRADIENT,
                        modifier = Modifier.fillMaxWidth()
                    )
                    MangalaGradientButton(
                        label = MR.strings.onboarding_button_import_wallet.desc().localized(),
                        onClick = {
                            MangalaAnalytics.trackEvent(
                                MangalaAnalytics.EventName.ONBOARDING_INITIATED,
                                mapOf(MangalaAnalytics.EventParam.ONBOARDING_STEP_NAME to MangalaAnalytics.EventParamValue.ONBOARDING_STEP_IMPORT_WALLET)
                            )

                            val importPrivateKeyScreen =
                                ScreenRegistry.get(SharedScreen.ImportPrivateKeyScreen)
                            navigator.push(importPrivateKeyScreen)
                        },
                        buttonStyle = MangalaButtonStyle.TRANSPARENT,
                        modifier = Modifier.fillMaxWidth()
                    )

                }
            }
        }
    }

    companion object {
        private const val IMAGE_LOGO = "logo"
        private const val IMAGE_LOADING_IMPORT = "loading_import_account"
        private const val IMAGE_TRANSFER_SUCCESS = "transfer_success"
        private const val IMAGE_TRANSACTION_HISTORY_EMPTY = "transaction_history_empty"
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Use MangalaBrandText component for consistent branding
        MangalaBrandText(
            fullText = page.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 39.2.sp,
            letterSpacing = (-0.28).sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = page.description,
            fontSize = 17.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFFD1D1D1),
            textAlign = TextAlign.Center,
            letterSpacing = (-0.17).sp,
            lineHeight = 23.8.sp,
            fontFamily = getInterFontFamily(),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
