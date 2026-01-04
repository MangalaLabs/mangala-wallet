package com.mangala.wallet.menu_base.presentation.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Check
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

class ThemeScreen : BaseScreen<ThemeScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.THEME
    override val screenClassName: String = ThemeScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): ThemeScreenModel {
        return getScreenModel()
    }

    @Composable
    override fun ScreenContent(screenModel: ThemeScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        Theme(
            onBackPressed = { navigator.pop() }
        )
    }

    private val supportedThemes = listOf("Light", "Dark", "Use Device Setting")

    @Composable
    fun Theme(onBackPressed: (Boolean) -> Unit) {
        var selectedThemeIndex by remember { mutableStateOf(0) }

        OnboardingGradientBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                MangalaWalletTopBarCenteredTitle(
                    title = MR.strings.all_theme.desc().localized(),
                    onBackClicked = { onBackPressed(true) }
                )

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(supportedThemes) { index, theme ->
                        val isSelected = index == selectedThemeIndex

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable { selectedThemeIndex = index }
                        ) {

//                            Image(
//                                imageVector = MangalaWalletPack.Check,
//                                contentDescription = "Currency",
//                                modifier = Modifier.size(24.dp)
//                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = theme,
                                color = MaterialTheme.mangalaColors.textPrimary
                            )

                            if (theme == "Use Device Setting") {
                                Row {
                                    Text(
                                        text = MR.strings.message_theme_screen_device_settings.desc()
                                            .localized(),
                                        modifier = Modifier.padding(start = 16.dp),
                                        color = MaterialTheme.mangalaColors.textSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            if (isSelected) {
                                Icon(
                                    imageVector = MangalaWalletPack.Check,
                                    tint = MaterialTheme.mangalaColors.iconPrimary,
                                    contentDescription = "Selected",
                                    modifier = Modifier.size(24.dp)
                                )
                            }


                        }
                    }
                }
            }
        }
    }
}