package com.mangala.wallet.features.chains.antelope.presentation.permission.createcustom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.AccountAuthAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.AccountAuthWait
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission.AccountKey
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicator
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import org.koin.core.parameter.parameterArrayOf

class CreatePermissionScreen(
    private val account: String,
    private val currentPermission: String,
    private val permissions: List<String>
) : BaseScreen<CreatePermissionScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_CREATE_PERMISSION
    override val screenClassName: String = CreatePermissionScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): CreatePermissionScreenModel =
        getScreenModel<CreatePermissionScreenModel> {
            parameterArrayOf(account, currentPermission)
        }

    @Composable
    override fun ScreenContent(screenModel: CreatePermissionScreenModel) {
        val uiState = screenModel.uiState.collectAsStateMultiplatform().value
        val navigator = LocalNavigator.currentOrThrow

        MaxSizeBox(Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) {
            when (uiState) {
                is CreatePermissionScreenUiState.Loading -> {
                    MaxSizeBox(
                        contentAlignment = Alignment.Center
                    ) {
                        MangalaCircularProgressIndicator(color = Color.Red)
                    }
                }

                is CreatePermissionScreenUiState.SingleAccount -> {
                    InputPermission(screenModel::onSubmitCreatePermission)
                }

                is CreatePermissionScreenUiState.MultiSignAccount -> {
                    Column {
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
                }

                is CreatePermissionScreenUiState.CreatePermissionSuccess -> {
                    PermissionCreatingSuccessfully(navigator)
                }

                is CreatePermissionScreenUiState.CreatePermissionFailed -> {
                    PermissionCreatingFailed(navigator, uiState.message)
                }

                is CreatePermissionScreenUiState.Error -> {
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
    fun InputPermission(
        onClickCreatePermission: (String, String, Int, List<AccountKey>, List<AccountAuthAccount>, List<AccountAuthWait>) -> Unit
    ) {
        var name by remember { mutableStateOf("") }
        var threshold by remember { mutableStateOf("1") }
        var permissionParentExpanded by remember { mutableStateOf(false) }
        var permissionParentSelected by remember { mutableStateOf(permissions.firstOrNull() ?: "") }
        var keyPairs by remember { mutableStateOf(listOf<AccountKey>()) }
        var accountPairs by remember { mutableStateOf(listOf<AccountAuthAccount>()) }
        var waitPairs by remember { mutableStateOf(listOf<AccountAuthWait>()) }

        Column {
            Text(
                text = "Create permission",
                modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
                color = Color.Black,
                fontSize = FontType.LARGE
            )

            Spacer(modifier = Modifier)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,  // Sets text color to black
                    focusedLabelColor = Color.Gray,  // Color of the label when the TextField is focused
                    unfocusedLabelColor = Color.Gray,  // Color of the label when the TextField is not focused
                    focusedBorderColor = Color.Gray,  // Color of the border when the TextField is focused
                    unfocusedBorderColor = Color.Gray   // Color of the border when the TextField is not focused
                )
            )

            Spacer(modifier = Modifier)

            Column {
                OutlinedTextField(value = permissionParentSelected,
                    onValueChange = { },
                    label = { Text("Chose permission parent") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { permissionParentExpanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                        }
                    })
                DropdownMenu(expanded = permissionParentExpanded,
                    onDismissRequest = { permissionParentExpanded = false }) {
                    permissions.forEach { item ->
                        DropdownMenuItem(onClick = {
                            permissionParentSelected = item
                            permissionParentExpanded = false
                        }) {
                            Text(text = item)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier)

            OutlinedTextField(
                value = threshold,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        threshold = it
                    }
                },
                label = { Text("Threshold") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,  // Sets text color to black
                    focusedLabelColor = Color.Gray,  // Color of the label when the TextField is focused
                    unfocusedLabelColor = Color.Gray,  // Color of the label when the TextField is not focused
                    focusedBorderColor = Color.Gray,  // Color of the border when the TextField is focused
                    unfocusedBorderColor = Color.Gray   // Color of the border when the TextField is not focused
                )
            )

            Spacer(modifier = Modifier)

            Column {
                Text("Keys")
                keyPairs.forEachIndexed { index, pair ->
                    AccountKeyRow(accountKey = pair,
                        onKeyChange = { newKey ->
                            keyPairs = keyPairs.toMutableList()
                                .apply { this[index] = this[index].copy(key = newKey) }
                        },
                        onValueChange = { newValue ->
                            keyPairs = keyPairs.toMutableList()
                                .apply { this[index] = this[index].copy(weight = newValue) }
                        },
                        onDelete = {
                            keyPairs = keyPairs.toMutableList().apply { removeAt(index) }
                        })
                }

                Spacer(modifier = Modifier)

                Button(onClick = { keyPairs = keyPairs + AccountKey("", 0) }) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier)

            Column {
                Text("Accounts")
                accountPairs.forEachIndexed { index, pair ->
                    AccountAuthAccountRow(accountKey = pair, onAccountChange = {
                        accountPairs = accountPairs.toMutableList()
                            .apply { this[index] = this[index].copy(account = it) }
                    }, onPermissionChange = {
                        accountPairs = accountPairs.toMutableList()
                            .apply { this[index] = this[index].copy(permission = it) }
                    }, onWeightChange = {
                        accountPairs = accountPairs.toMutableList()
                            .apply { this[index] = this[index].copy(weight = it) }
                    }, onDelete = {
                        accountPairs = accountPairs.toMutableList().apply { removeAt(index) }
                    })
                }

                Spacer(modifier = Modifier)

                Button(onClick = { accountPairs = accountPairs + AccountAuthAccount("", "", 0) }) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier)

            Column {
                Text("Waits")
                waitPairs.forEachIndexed { index, pair ->
                    AccountAuthWaitRow(accountAuthWait = pair, onWaitSecChange = {
                        waitPairs = waitPairs.toMutableList()
                            .apply { this[index] = this[index].copy(waitSec = it) }
                    }, onWeightChange = {
                        waitPairs = waitPairs.toMutableList()
                            .apply { this[index] = this[index].copy(weight = it) }
                    }, onDelete = {
                        waitPairs = waitPairs.toMutableList().apply { removeAt(index) }
                    })
                }

                Spacer(modifier = Modifier)

                Button(onClick = { waitPairs = waitPairs + AccountAuthWait(0, 0) }) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier)

            Button(
                onClick = {
                    onClickCreatePermission(
                        name,
                        permissionParentSelected,
                        threshold.toInt(),
                        keyPairs,
                        accountPairs,
                        waitPairs
                    )
                }, colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Blue, contentColor = Color.White
                )
            ) {
                Text("Create permission")
            }
        }
    }

    @Composable
    fun PermissionCreatingSuccessfully(navigator: Navigator) {
        Column {
            Text("Your permission is created successfully.")
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
    fun PermissionCreatingFailed(navigator: Navigator, message: String) {
        Column {
            Text("Your permission is created failed.")
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

    @Composable
    fun AccountKeyRow(
        accountKey: AccountKey,
        onKeyChange: (String) -> Unit,
        onValueChange: (Long) -> Unit,
        onDelete: () -> Unit
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            OutlinedTextField(
                value = accountKey.key,
                onValueChange = onKeyChange,
                label = { Text("Keys") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = accountKey.weight.toString(),
                onValueChange = { onValueChange(it.toLongOrNull() ?: 0) },
                label = { Text("Weight") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }

    @Composable
    fun AccountAuthAccountRow(
        accountKey: AccountAuthAccount,
        onAccountChange: (String) -> Unit,
        onPermissionChange: (String) -> Unit,
        onWeightChange: (Long) -> Unit,
        onDelete: () -> Unit
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            OutlinedTextField(
                value = accountKey.account,
                onValueChange = onAccountChange,
                label = { Text("Account") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = accountKey.account,
                onValueChange = onPermissionChange,
                label = { Text("Permission") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = accountKey.weight.toString(),
                onValueChange = { onWeightChange(it.toLongOrNull() ?: 0) },
                label = { Text("Weight") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }

    @Composable
    fun AccountAuthWaitRow(
        accountAuthWait: AccountAuthWait,
        onWaitSecChange: (Int) -> Unit,
        onWeightChange: (Long) -> Unit,
        onDelete: () -> Unit
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            OutlinedTextField(
                value = accountAuthWait.waitSec.toString(),
                onValueChange = { onWaitSecChange(it.toIntOrNull() ?: 0) },
                label = { Text("Wait sec") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = accountAuthWait.weight.toString(),
                onValueChange = { onWeightChange(it.toLongOrNull() ?: 0) },
                label = { Text("Weight") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}