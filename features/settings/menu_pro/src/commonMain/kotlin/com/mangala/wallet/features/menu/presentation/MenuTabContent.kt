package com.mangala.wallet.features.menu.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.*
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun MenuTabContent() {
    val navigator = LocalNavigator.currentOrThrow

    val networkScreen = rememberScreen(SharedScreen.NetworkScreen)
    val themeScreen = rememberScreen(SharedScreen.ThemeScreen)
    val languageScreen = rememberScreen(SharedScreen.LanguageScreen)
    val currencyScreen = rememberScreen(SharedScreen.CurrencyScreen)
    val notificationsAndAlertsScreen = rememberScreen(SharedScreen.NotificationsScreen)
    val securityScreen = rememberScreen(SharedScreen.SecurityScreen)
    val helpCenterScreen = rememberScreen(SharedScreen.HelpCenterScreen)
    val aboutUsScreen = rememberScreen(SharedScreen.AboutUsScreen)
    val shareAppScreen = rememberScreen(SharedScreen.ShareAppScreen)
    val connectWithUsScreen = rememberScreen(SharedScreen.ConnectWithUsScreen)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.cloudGray)
            .windowInsetsPadding(WindowInsets.systemBars)
            .verticalScroll(rememberScrollState())
    ) {
        // Title section
        Text(
            text = MR.strings.all_menu.desc().localized(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Colors.darkGray,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 24.dp)
        )
        
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
        ) {
            // First section - Appearance
            Column(
                modifier = Modifier.clip(RoundedCornerShape(CornerRadius.Small)),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                MenuTabRow(
                    title = MR.strings.all_network.desc().localized(),
                    onClickNavigate = { navigator.push(networkScreen) },
                    iconRepresent = MangalaWalletPack.Network
                )

                MenuTabRow(
                    title = MR.strings.all_theme.desc().localized(),
                    onClickNavigate = { navigator.push(themeScreen) },
                    iconRepresent = MangalaWalletPack.Setting
                )

                MenuTabRow(
                    title = MR.strings.all_language.desc().localized(),
                    onClickNavigate = { navigator.push(languageScreen) },
                    iconRepresent = MangalaWalletPack.Setting
                )

                MenuTabRow(
                    title = MR.strings.all_currency.desc().localized(),
                    onClickNavigate = { navigator.push(currencyScreen) },
                    iconRepresent = MangalaWalletPack.UnitedStatesDollar
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Second section - Support
            Column(
                modifier = Modifier.clip(RoundedCornerShape(CornerRadius.Small)),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                MenuTabRow(
                    title = MR.strings.all_notifications.desc().localized(),
                    onClickNavigate = { navigator.push(notificationsAndAlertsScreen) },
                    iconRepresent = MangalaWalletPack.Notification
                )

                MenuTabRow(
                    title = MR.strings.all_security.desc().localized(),
                    onClickNavigate = { navigator.push(securityScreen) },
                    iconRepresent = MangalaWalletPack.Security
                )

                // Add Contacts row for pro version
                MenuTabRow(
                    title = MR.strings.all_contacts.desc().localized(),
                    onClickNavigate = {
                        val contactsScreen = ScreenRegistry.get(SharedScreen.ContactListScreen)
                        navigator.push(contactsScreen)
                    },
                    iconRepresent = MangalaWalletPack.Contacts
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About section
            Column(
                modifier = Modifier.clip(RoundedCornerShape(CornerRadius.Small)),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                MenuTabRow(
                title = MR.strings.all_help_center.desc().localized(),
                onClickNavigate = { navigator.push(helpCenterScreen) },
                iconRepresent = MangalaWalletPack.HelpCenter
                )

                MenuTabRow(
                    title = MR.strings.all_about_us.desc().localized(),
                    onClickNavigate = { navigator.push(aboutUsScreen) },
                    iconRepresent = MangalaWalletPack.AboutUs
                )

                MenuTabRow(
                    title = MR.strings.all_share_app.desc().localized(),
                    onClickNavigate = { navigator.push(shareAppScreen) },
                    iconRepresent = MangalaWalletPack.Share
                )
            }

            Spacer(modifier = Modifier.height(Spacing.BASE))

            // Connect with us
            Column(
                modifier = Modifier.clip(RoundedCornerShape(CornerRadius.Small))
            ) {
                MenuTabRow(
                    title = MR.strings.all_connect_with_us.desc().localized(),
                    onClickNavigate = { navigator.push(connectWithUsScreen) },
                    iconRepresent = MangalaWalletPack.ConnectWithUs
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        } // End of inner column with horizontal padding
        
        Spacer(modifier = Modifier.height(100.dp)) // Extra space for bottom navigation
    }
}

@Composable
private fun MenuTabRow(
    title: String,
    onClickNavigate: () -> Unit,
    iconRepresent: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable(onClick = { onClickNavigate() })
            .padding(
                start = Dimensions.Padding.default,
                top = Dimensions.Padding.small,
                bottom = Dimensions.Padding.small,
                end = Dimensions.Padding.default
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = iconRepresent,
                contentDescription = null,
                modifier = Modifier.size(Dimensions.IconButtonSize),
                tint = Colors.gray
            )
            Spacer(modifier = Modifier.width(Dimensions.Padding.half))
            TextNormal(text = title, color = Colors.darkGray)
        }

        Icon(
            imageVector = MangalaWalletPack.Navigate,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Colors.gray
        )
    }
}