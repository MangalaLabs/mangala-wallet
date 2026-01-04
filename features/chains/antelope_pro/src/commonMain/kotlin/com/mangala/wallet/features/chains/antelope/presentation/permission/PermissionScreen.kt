package com.mangala.wallet.features.chains.antelope.presentation.permission

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.features.chains.antelope.presentation.permission.createcustom.CreatePermissionScreen
import com.mangala.wallet.features.chains.antelope.presentation.permission.linkauth.LinkAuthScreen
import com.mangala.wallet.features.chains.antelope.presentation.permission.list.PermissionListScreen
import com.mangala.wallet.features.chains.antelope.presentation.permission.unlinkauth.UnLinkAuthScreen
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.MangalaTextButton
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicator
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parameterArrayOf

class PermissionScreen(
    private val account: String,
    private val permission: String,
) : BaseScreen<PermissionScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_PERMISSIONS
    override val screenClassName: String = PermissionScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): PermissionScreenModel =
        getScreenModel<PermissionScreenModel> {
            parameterArrayOf(account, permission)
        }

    @Composable
    override fun ScreenContent(screenModel: PermissionScreenModel) {
        val nav = LocalNavigator.currentOrThrow
        val uiState = screenModel.uiState.collectAsStateMultiplatform().value

        Column(Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) {
            MangalaTextButton(
                text = MR.strings.all_back.desc().localized(),
                color = Color.Blue,
                onClick = {
                    nav.pop()
                }
            )
            when (uiState) {
                is PermissionScreenUiState.Loading -> {
                    MaxSizeBox(
                        contentAlignment = Alignment.Center
                    ) {
                        MangalaCircularProgressIndicator(color = Color.Red)
                    }
                }

                is PermissionScreenUiState.Success -> {
                    MangalaTextButton(
                        text = MR.strings.button_permission_screen_list_permission.desc().localized(),
                        color = Color.Blue,
                        onClick = {
                            nav.push(
                                PermissionListScreen(
                                    account,
                                    permission,
                                    uiState.uiModel.permissionParentsName
                                )
                            )
                        }
                    )
                    Spacer(modifier = Modifier)
                    MangalaTextButton(
                        text = MR.strings.button_permission_screen_create_new_custom_permission.desc().localized(),
                        color = Color.Blue,
                        onClick = {
                            nav.push(
                                CreatePermissionScreen(
                                    account,
                                    permission,
                                    uiState.uiModel.permissionParentsName
                                )
                            )
                        }
                    )
                    Spacer(modifier = Modifier)
                    MangalaTextButton(
                        text = MR.strings.button_permission_screen_link_auth.desc().localized(),
                        color = Color.Blue,
                        onClick = {
                            nav.push(
                                LinkAuthScreen(
                                    account,
                                    permission,
                                    uiState.uiModel.permissionParentsName
                                )
                            )
                        }
                    )
                    Spacer(modifier = Modifier)
                    MangalaTextButton(
                        text = MR.strings.button_permission_screen_unlink_auth.desc().localized(),
                        color = Color.Blue,
                        onClick = {
                            nav.push(
                                UnLinkAuthScreen(
                                    account,
                                    permission
                                )
                            )
                        }
                    )
                }

                is PermissionScreenUiState.Error -> {
                    Text(MR.strings.message_permission_screen_load_account_failed.desc().localized())
                }
            }
        }
    }
}