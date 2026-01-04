package com.mangala.wallet.features.chains.antelope.presentation.backupaccount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Copy
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextSubTitle
import com.mangala.wallet.ui.component.GradientBackground
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.ClipboardFactory
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

class BackupAntelopeAccountScreen(
    private val accountName: String,
    private val blockchainUid: String?
) : BaseScreen<BackupAntelopeAccountScreenModel>(), KoinComponent {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_BACKUP_ACCOUNT
    override val screenClassName: String = BackupAntelopeAccountScreen::class.simpleName.orEmpty()

    @delegate:Transient
    private val clipboardFactory: ClipboardFactory by inject()

    @Composable
    override fun createScreenModel(): BackupAntelopeAccountScreenModel {
        return getScreenModel<BackupAntelopeAccountScreenModel>(parameters = {
            parametersOf(
                accountName,
                blockchainUid
            )
        })
    }

    override val isBottomBarVisible: Boolean
        get() = false

    @Composable
    override fun ScreenContent(screenModel: BackupAntelopeAccountScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val uiState = screenModel.uiState.collectAsStateMultiplatform().value

        BackupAntelopeAccountScreen(
            uiState,
            onClickContinue = {
                navigator.replaceAll(ScreenRegistry.get(SharedScreen.HomeScreen()))
            },
            onBackPressed = {
                navigator.pop()
            }
        )
    }

    @Composable
    fun BackupAntelopeAccountScreen(
        uiState: BackupAntelopeAccountScreenUiState,
        onClickContinue: () -> Unit,
        onBackPressed: () -> Unit
    ) {
        GradientBackground {
            MaxSizeColumn(verticalArrangement = Arrangement.SpaceBetween) {
                MaxWidthColumn {
                    MangalaWalletTopBar(
                        modifier = Modifier.background(Color.Transparent),
                        text = "Backup Wallet",
                        onBackClicked = onBackPressed
                    )
                }
                MaxWidthColumn(
                    modifier = Modifier.weight(1f)
                        .padding(horizontal = Dimensions.Padding.default)
                ) {
                    MaxSizeColumn {
                        Spacer(Modifier.height(Spacing.LARGE))
                        (uiState as? BackupAntelopeAccountScreenUiState.Loaded)?.let {
                            TextSubTitle(
                                text = "Backup account",
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(14.dp))
                            TextNormal(
                                text = "Backup your account",
                                color = Colors.darkDarkGray
                            )
                            MaxWidthColumn(verticalArrangement = Arrangement.spacedBy(Spacing.TINY)) {
                                ViewAndCopy("Account Name", it.accountName)
                                ViewAndCopy("Active private key", it.activePrivateKey)
                                ViewAndCopy("Active public key", it.activePublicKey)
                                ViewAndCopy("Owner private key", it.ownerPrivateKey)
                                ViewAndCopy("Owner public key", it.ownerPublicKey)
                            }
                            ButtonNormal(
                                MR.strings.all_continue.desc().localized(),
                                onClick = onClickContinue
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ViewAndCopy(label: String, value: String, modifier: Modifier = Modifier) {
        Text(label)
        MaxWidthRow(horizontalArrangement = Arrangement.SpaceBetween, modifier = modifier) {
            Text(value, modifier = Modifier.weight(1f))
            IconButton(onClick = {
                clipboardFactory.copyText(label, value)
            }) {
                Icon(MangalaWalletPack.Copy, contentDescription = "Copy")
            }
        }
    }
}