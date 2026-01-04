package com.mangala.wallet.features.chains.antelope.presentation.permission.linkauth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicator
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import org.koin.core.parameter.parameterArrayOf

class LinkAuthScreen(
    private val account: String,
    private val currentPermission: String,
    private val permissions: List<String>
) : BaseScreen<LinkAuthScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_LINK_AUTH
    override val screenClassName: String = LinkAuthScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): LinkAuthScreenModel = getScreenModel<LinkAuthScreenModel> {
        parameterArrayOf(account, currentPermission)
    }

    @Composable
    override fun ScreenContent(screenModel: LinkAuthScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        MaxSizeBox(Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) {
            when (val uiState = screenModel.uiState.collectAsStateMultiplatform().value) {
                is AuthLinkUIState.Loading -> {
                    MaxSizeBox(
                        contentAlignment = Alignment.Center
                    ) {
                        MangalaCircularProgressIndicator(color = Color.Red)
                    }
                }

                is AuthLinkUIState.MultiSignAccount -> {
                    MultiSignAccountUI(navigator)
                }

                is AuthLinkUIState.SingleSignAccount -> {
                    UpdateAuthLinkUI(navigator, screenModel::linkAuth)
                }

                is AuthLinkUIState.AuthLinkSuccess -> {
                    UpdateAuthLinkSuccess(navigator)
                }

                is AuthLinkUIState.AuthLinkFailed -> {
                    UpdateAuthLinkFailed(navigator, uiState.message)
                }

                is AuthLinkUIState.Error -> {
                    Column {
                        Text(
                            text = "Error ${uiState.message}",
                            color = Color.Black
                        )
                        Button(
                            onClick = { navigator.pop() }, colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.Blue, contentColor = Color.White
                            )
                        ) {
                            Text("Back")
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun MultiSignAccountUI(navigator: Navigator) {
        Text(
            text = "Your account is multiple sign account, you need to create propose and wait to approvals",
            color = Color.Black
        )
        Button(
            onClick = { navigator.pop() }, colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Blue, contentColor = Color.White
            )
        ) {
            Text("Back")
        }
    }

    @Composable
    fun UpdateAuthLinkUI(
        navigator: Navigator,
        onclickLinkAuth: (String, String, String) -> Unit
    ) {
        var contract by remember { mutableStateOf("") }
        var action by remember { mutableStateOf("") }
        var selectedPermission by remember { mutableStateOf(permissions[0]) }

        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = contract,
                onValueChange = { contract = it },
                label = { Text("Contract") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                isError = contract.isBlank()
            )
            if (contract.isBlank()) {
                Text(
                    "Contract cannot be blank",
                    color = Color.Red,
                    style = MaterialTheme.typography.body1
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = action,
                onValueChange = { action = it },
                label = { Text("Action") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                isError = action.isBlank()
            )
            if (action.isBlank()) {
                Text(
                    "Action cannot be blank",
                    color = Color.Red,
                    style = MaterialTheme.typography.body1
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            DropdownMenu(
                selectedPermission = selectedPermission,
                permissions = permissions,
                onSelectionChange = { selectedPermission = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { onclickLinkAuth(selectedPermission, contract, action) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Link", color = Color.White)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { navigator.pop() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back", color = Color.White)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun DropdownMenu(
        selectedPermission: String?,
        permissions: List<String>,
        onSelectionChange: (String) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }
        var text1 by remember { mutableStateOf(selectedPermission ?: "Select Permission") }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                readOnly = true,
                value = text1,
                onValueChange = {},
                label = { Text("Permission") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                permissions.forEach { permission ->
                    DropdownMenuItem(
                        content = { Text(permission) },
                        onClick = {
                            text1 = permission
                            expanded = false
                            onSelectionChange(permission)
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun UpdateAuthLinkSuccess(navigator: Navigator) {
        Column {
            Text("Your link auth is executed successfully.")
            Button(
                onClick = { navigator.pop() }, colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White, backgroundColor = Color.Blue
                )
            ) {
                Text("Back")
            }
        }
    }

    @Composable
    fun UpdateAuthLinkFailed(navigator: Navigator, message: String) {
        Column {
            Text("Your link auth is executed failed.")
            Text("Failure details: $message")
            Button(
                onClick = { navigator.pop() }, colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White, backgroundColor = Color.Blue
                )
            ) {
                Text("Back")
            }
        }
    }
}