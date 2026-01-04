package com.mangala.wallet.menu_base.presentation.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Navigate
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Wallet
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.imageloader.RemoteImage
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

abstract class BasePreferencesScreen : Screen {

    abstract val screenName: String
    abstract val screenClassName: String

    @Composable
    fun BasePreferencesScreenContent(
        uiModel: PreferencesScreenUiModel,
        additionalItems: @Composable () -> Unit,
    ) {
        LifecycleEffect(
            onStarted = {
                MangalaAnalytics.trackScreenView(screenName, screenClassName)
            }
        )

        val navigator = LocalNavigator.currentOrThrow

        val themeScreen = rememberScreen(SharedScreen.ThemeScreen)
        val languageScreen = rememberScreen(SharedScreen.LanguageScreen)

        Scaffold(
            topBar = {
                MangalaWalletTopBarCenteredTitle(
                    title = MR.strings.all_preferences.desc().localized(),
                    onBackClicked = { navigator.pop() }
                )
            },
            modifier = Modifier.statusBarsPadding(),
            backgroundColor = Color.Transparent,
        ) {
            Column(
                modifier = Modifier
                    .padding(start = Dimensions.Padding.default, end = Dimensions.Padding.default)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(Spacing.SMALL))

                Column(
                    modifier = Modifier.clip(RoundedCornerShape(CornerRadius.Small)),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    if (uiModel.isDevEnvironment) {
                        PreferencesRow(
                            title = MR.strings.all_theme.desc().localized(),
                            showSelectedName = MR.strings.title_theme_light.desc().localized(),
                            onClickNavigate = { navigator.push(themeScreen) },
                            iconSelectedRepresent = MangalaWalletPack.Wallet
                        )
                    }


                    PreferencesRow(
                        title = MR.strings.all_language.desc().localized(),
                        showSelectedName = uiModel.language?.name ?: "",
                        onClickNavigate = { navigator.push(languageScreen) },
                        url = uiModel.language?.flagUrl ?: ""
                    )

                    additionalItems()
                }
            }
        }
    }

    @Composable
    fun PreferencesRow(
        title: String,
        showSelectedName: String = "",
        onClickNavigate: () -> Unit,
        iconSelectedRepresent: ImageVector? = null,
        url: String = ""
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.mangalaColors.bgInnerCard)
                .clickable(onClick = onClickNavigate)
                .padding(
                    start = Dimensions.Padding.default,
                    top = Dimensions.Padding.small,
                    bottom = Dimensions.Padding.small,
                    end = Dimensions.Padding.default
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextNormal(
                text = title,
                color = MaterialTheme.mangalaColors.textPrimary,
            )

            Spacer(modifier = Modifier.weight(1f))

            if (showSelectedName != "") {
                TextDescription2(
                    text = showSelectedName,
                    color = MaterialTheme.mangalaColors.textSecondary,
                )
                Spacer(modifier = Modifier.width(Dimensions.Padding.half))
            }

            if (iconSelectedRepresent != null) {
                Icon(
                    imageVector = iconSelectedRepresent,
                    contentDescription = null,
                    modifier = Modifier.width(20.dp).height(20.dp),
                    tint = MaterialTheme.mangalaColors.iconSecondary
                )
                Spacer(modifier = Modifier.width(Dimensions.Padding.small))
            }

            if (url != "") {
                RemoteImage(
                    modifier = Modifier.size(Dimensions.IconButtonSize),
                    url = url,
                )
                Spacer(modifier = Modifier.width(Dimensions.Padding.small))
            } else {
                Spacer(modifier = Modifier.width(Dimensions.Padding.quarter))
            }

            Icon(
                imageVector = MangalaWalletPack.Navigate,
                contentDescription = "Navigate",
                modifier = Modifier.width(20.dp).height(20.dp),
                tint = MaterialTheme.mangalaColors.iconPrimary
            )
        }
    }
}
