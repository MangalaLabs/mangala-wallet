package com.mangala.wallet.features.chains.antelope.presentation.permission.unlinkauth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.antelope.base.api.model.LinkedAction
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.MultiSignAccountCheckingUseCase
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicator
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import org.koin.core.parameter.parameterArrayOf

class UnLinkAuthScreen(
    private val account: String,
    private val currentPermission: String
) : BaseScreen<UnLinkAuthScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_UNLINK_AUTH
    override val screenClassName: String = UnLinkAuthScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): UnLinkAuthScreenModel =
        getScreenModel<UnLinkAuthScreenModel> {
            parameterArrayOf(account, currentPermission)
        }

    @Composable
    override fun ScreenContent(screenModel: UnLinkAuthScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        Column(Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) {
            Button(
                onClick = { navigator.pop() }, colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Black, contentColor = Color.White
                )
            ) {
                Text("Back")
            }
            when (val uiState = screenModel.uiState.collectAsStateMultiplatform().value) {
                is UnLinkAuthUiState.Loading -> {
                    MaxSizeBox(
                        contentAlignment = Alignment.Center
                    ) {
                        MangalaCircularProgressIndicator(color = Color.Red)
                    }
                }

                is UnLinkAuthUiState.MultiSignAccount -> {
                    MultiSignAccountUI()
                }

                is UnLinkAuthUiState.Success -> {
                    UnLinkAuthUI(uiState.linkedActions, screenModel::unlinkAuth)
                }

                is UnLinkAuthUiState.UnLinkAuthSuccess -> {
                    Text("Un-link auth successfully")
                }

                is UnLinkAuthUiState.UnLinkAuthFailed -> {
                    Text("Un-link auth failed.")
                    Text("failure message: ${uiState.message}")
                }

                is UnLinkAuthUiState.Error -> {
                    Text("General error: ${uiState.message}")
                }
            }
        }
    }

    @Composable
    fun MultiSignAccountUI() {
        Text(
            text = "Your account is multiple sign account, you need to create propose and wait to approvals",
            color = Color.Black
        )
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun UnLinkAuthUI(actions: List<LinkedAction>, unLinkAuth: (String?, String?) -> Unit) {
        // State for selected action
        var selectedAction by remember { mutableStateOf(actions.firstOrNull()) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dropdown to select action
            if (actions.isNotEmpty()) {
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        readOnly = true,
                        value = selectedAction?.let { "${it.account} - ${it.action}" }
                            ?: "Select action",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Select Action") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        actions.forEach { action ->
                            DropdownMenuItem(
                                content = { Text("${action.account} - ${action.action}") },
                                onClick = {
                                    selectedAction = action
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to unlink
            Button(
                onClick = { selectedAction?.let { unLinkAuth(it.account, it.action) } },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                enabled = selectedAction != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Unlink", color = Color.White)
            }
        }
    }
}