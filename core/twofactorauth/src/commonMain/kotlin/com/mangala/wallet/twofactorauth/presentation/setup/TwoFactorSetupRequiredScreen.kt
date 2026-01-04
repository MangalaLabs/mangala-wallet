package com.mangala.wallet.twofactorauth.presentation.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Security
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

class TwoFactorSetupRequiredScreen(
    @Transient private val onSetup2Fa: () -> Unit,
    @Transient private val onCancel: () -> Unit,
    @Transient private val onFallbackToPin: () -> Unit
) : BaseScreen<TwoFactorSetupRequiredScreenModel>() {

    override val screenName: String = "Two Factor Setup Required"
    override val screenClassName: String = "TwoFactorSetupRequiredScreen"
    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): TwoFactorSetupRequiredScreenModel {
        return getScreenModel { parametersOf(onCancel, onFallbackToPin) }
    }

    @Composable
    override fun ScreenContent(screenModel: TwoFactorSetupRequiredScreenModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = MangalaWalletPack.Security,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Enhanced Security Recommended",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Two-Factor Authentication (2FA) provides an extra layer of security for your sensitive data. We recommend setting it up for operations like exporting contacts.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onSetup2Fa() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Set Up 2FA Now")
            }

            OutlinedButton(
                onClick = { screenModel.onContinueWithPINClicked() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue with PIN Only")
            }

            TextButton(
                onClick = { screenModel.onCancelClicked() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}