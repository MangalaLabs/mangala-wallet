package com.mangala.wallet.menu_base.presentation.language

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Dimensions.IconChosenLanguageSizeLanguageScreen
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Check
import com.mangala.wallet.model.language.Language
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.component.MangalaWalletSearchBarWithBorder
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.imageloader.DisplayImageForIcon
import com.mangala.wallet.ui.modifier.roundedCornersItemShape
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

class LanguageScreen : BaseScreen<LanguageScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.LANGUAGE
    override val screenClassName: String = LanguageScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): LanguageScreenModel {
        return getScreenModel()
    }

    @Composable
    override fun ScreenContent(screenModel: LanguageScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        val uiState = screenModel.uiState.collectAsStateMultiplatform().value

        LanguageScreen(
            onBackClicked = { navigator.pop() },
            uiState = uiState,
            onLanguageChange = screenModel::changeLanguage,
            onSearchTextChanged = screenModel::onSearchTextChanged
        )
    }

    @Composable
    fun LanguageScreen(
        onBackClicked: (Boolean) -> Unit,
        uiState: LanguageScreenUiState,
        onLanguageChange: (Language) -> Unit,
        onSearchTextChanged: (String) -> Unit
    ) {
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current

        OnboardingGradientBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    }
                    .safeDrawingPadding()
            ) {
                MangalaWalletTopBarCenteredTitle(
                    title = MR.strings.all_language.desc().localized(),
                    onBackClicked = { onBackClicked(true) }
                )

                Spacer(modifier = Modifier.height(Spacing.SMALL))

                Column(
                    Modifier
                        .padding(horizontal = Dimensions.Padding.default)
                        .fillMaxSize()
                ) {
                    if (uiState is LanguageScreenUiState.Success) {
                        MangalaWalletSearchBarWithBorder(
                            query = uiState.query,
                            placeholder = MR.strings.message_language_search_language.desc()
                                .localized(),
                            onQueryChange = onSearchTextChanged
                        )

                        Spacer(modifier = Modifier.height(Spacing.SMALL))

                        if (uiState.filteredLanguageUiModels.isEmpty()) {
                            Box(Modifier.fillMaxSize()) {
                                TextDescription2(
                                    MR.strings.message_language_search_language_not_found.desc()
                                        .localized(),
                                    fontStyle = FontStyle.Italic,
                                    modifier = Modifier.align(Alignment.Center),
                                    color = MaterialTheme.mangalaColors.textSecondary
                                )
                            }
                        } else {
                            LazyColumn(modifier = Modifier.clip(RoundedCornerShape(CornerRadius.Small))) {
                                itemsIndexed(uiState.filteredLanguageUiModels) { index, it ->
                                    if (index != 0) Spacer(modifier = Modifier.height(1.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.roundedCornersItemShape(
                                            list = uiState.filteredLanguageUiModels,
                                            currentIndex = index
                                        )
                                            .background(color = MaterialTheme.mangalaColors.bgInnerCard)
                                            .fillMaxWidth()
                                            .clickable {
                                                StringDesc.localeType =
                                                    StringDesc.LocaleType.Custom(it.language.code)
                                                onLanguageChange(it.language)
                                            }
                                            .padding(Dimensions.Padding.default)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {

                                            DisplayImageForIcon(it.iconLanguage)
                                            Spacer(modifier = Modifier.width(Spacing.TINY))

                                            TextDescription2(
                                                text = it.language.languageName,
                                                color = MaterialTheme.mangalaColors.textPrimary
                                            )
                                        }

                                        if (it.isSelected) {
                                            Icon(
                                                imageVector = MangalaWalletPack.Check,
                                                tint = MaterialTheme.mangalaColors.iconPrimary,
                                                contentDescription = "",
                                                modifier = Modifier.size(
                                                    IconChosenLanguageSizeLanguageScreen
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}