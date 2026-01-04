package com.mangala.wallet.features.chains.antelope.presentation.permission.updatepermission

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.antelope.base.api.model.Account
import com.mangala.antelope.base.api.model.Key
import com.mangala.antelope.base.api.model.Permission
import com.mangala.antelope.base.api.model.PermissionAccount
import com.mangala.antelope.base.api.model.Wait
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform

//TODO
class UpdatePermissionScreen(
    private val permission: Permission,
    private val authorizationName: String,
    private val authorizationPermission: String,
    private val permissionParentsName: List<String>
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: UpdatePermissionScreenModel = getScreenModel()
        InputPermission(permission, screenModel)

        when (val uiState = screenModel.uiState.collectAsStateMultiplatform().value) {
            is UpdatePermissionScreenUiState.Loading -> {
                Text("Loading")
            }

            is UpdatePermissionScreenUiState.Success -> {
                Text(uiState.data)
            }

            is UpdatePermissionScreenUiState.Error -> {
                println("Error: ${uiState.message}")
            }
        }
    }

    @Composable
    fun InputPermission(
        permission: Permission,
        screenModel: UpdatePermissionScreenModel,
    ) {
        var name by remember { mutableStateOf(permission.permName) }
        var threshold by remember { mutableStateOf(permission.requiredAuth!!.threshold) }
        var permissionParentExpanded by remember { mutableStateOf(false) }
        var permissionParentSelected by remember { mutableStateOf(permission.parent) }
        var keyPairs by remember { mutableStateOf(permission.requiredAuth!!.keys!!) }
        var accountPairs by remember { mutableStateOf(permission.requiredAuth!!.accounts!!) }
        var waitPairs by remember { mutableStateOf(permission.requiredAuth!!.waits!!) }

        Column {
            Text(
                text = "Update permission",
                modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
                color = Color.Black,
                fontSize = FontType.LARGE
            )

            Spacer(modifier = Modifier)

            name?.let {
                OutlinedTextField(
                    value = it,
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
            }

            Spacer(modifier = Modifier)

            Column {
                Text("Permission parent")
                OutlinedTextField(
                    value = permissionParentSelected ?: "",
                    onValueChange = { },
                    label = { Text("Chose permission parent") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { permissionParentExpanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                        }
                    }
                )

                DropdownMenu(expanded = permissionParentExpanded,
                    onDismissRequest = { permissionParentExpanded = false }) {
                    permissionParentsName.forEach { parent ->
                        DropdownMenuItem(onClick = {
                            permissionParentSelected = parent
                            permissionParentExpanded = false
                        }) {
                            Text(
                                text = parent,
                                modifier = Modifier.padding(8.dp),
                                color = Color.Black
                            )
                        }
                    }
                }
            }


            Spacer(modifier = Modifier)

            threshold?.let {
                OutlinedTextField(
                    value = it.toString(),
                    onValueChange = { threshold = it.toIntOrNull() ?: 0 },
                    label = { Text("Threshold") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Color.Black,  // Sets text color to black
                        focusedLabelColor = Color.Gray,  // Color of the label when the TextField is focused
                        unfocusedLabelColor = Color.Gray,  // Color of the label when the TextField is not focused
                        focusedBorderColor = Color.Gray,  // Color of the border when the TextField is focused
                        unfocusedBorderColor = Color.Gray   // Color of the border when the TextField is not focused
                    )
                )
            }

            Spacer(modifier = Modifier)

            Column {
                Text("Keys")
                keyPairs.forEachIndexed { index, pair ->
                    AccountKeyRow(accountKey = pair.copy(key = pair.key, weight = pair.weight),
                        onKeyChange = { newKey ->
                            keyPairs = keyPairs.toMutableList()
                                .apply { this[index] = this[index].copy(key = newKey) }
                        },
                        onValueChange = { newValue ->
                            keyPairs = keyPairs.toMutableList()
                                .apply {
                                    this[index] = this[index].copy(weight = newValue.toInt())
                                }
                        },
                        onDelete = {
                            keyPairs = keyPairs.toMutableList().apply { removeAt(index) }
                        }
                    )
                }

                Spacer(modifier = Modifier)

                Button(onClick = { keyPairs = keyPairs + Key("", 0) }) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier)

            Column {
                Text("Accounts")
                accountPairs.forEachIndexed { index, pair ->
                    AccountAuthAccountRow(
                        accountKey = pair,
                        onAccountChange = { updatedActor ->
                            accountPairs = accountPairs.toMutableList().also { list ->
                                val updatedPermission =
                                    pair.permission?.copy(actor = updatedActor)
                                        ?: PermissionAccount(
                                            actor = updatedActor,
                                            permission = ""
                                        )
                                list[index] = pair.copy(permission = updatedPermission)
                            }
                        },
                        onPermissionChange = { updatedPermission ->
                            accountPairs = accountPairs.toMutableList().also { list ->
                                val updatedPerm =
                                    pair.permission?.copy(permission = updatedPermission)
                                        ?: PermissionAccount(
                                            actor = "",
                                            permission = updatedPermission
                                        )
                                list[index] = pair.copy(permission = updatedPerm)
                            }
                        },
                        onWeightChange = { updatedWeight ->
                            accountPairs = accountPairs.toMutableList().also { list ->
                                list[index] = pair.copy(weight = updatedWeight)
                            }
                        },
                        onDelete = {
                            accountPairs =
                                accountPairs.toMutableList().also { it.removeAt(index) }
                        }
                    )
                }

                Spacer(modifier = Modifier)

                Button(onClick = {
                    // Thêm account mới vào danh sách
                    accountPairs += Account(
                        weight = 0,
                        PermissionAccount(actor = "", permission = "")
                    )
                }) {
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

                Button(onClick = { waitPairs = waitPairs + Wait(0, 0) }) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier)

            Button(
                onClick = {
                    println("Update permission on click")
                    screenModel.onSubmitUpdatePermission(
                        authorizationAccountName = authorizationName,
                        authorizationPermission = authorizationPermission,
                        permissionUpdated = permission.permName ?: "",
                        permissionParent = permissionParentSelected.toString(),
                        threshold = threshold!!,
                        keys = keyPairs,
                        accounts = accountPairs,
                        waits = waitPairs
                    )
                }, colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Blue, contentColor = Color.White
                )
            ) {
                Text("Update permission")
            }
        }
    }

    @Composable
    fun AccountKeyRow(
        accountKey: Key,
        onKeyChange: (String) -> Unit,
        onValueChange: (Short) -> Unit,
        onDelete: () -> Unit,
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            OutlinedTextField(
                value = accountKey.key!!,
                onValueChange = onKeyChange,
                label = { Text("Keys") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = accountKey.weight.toString(),
                onValueChange = { onValueChange(it.toShortOrNull() ?: 0) },
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
        accountKey: Account,
        onAccountChange: (String) -> Unit,
        onPermissionChange: (String) -> Unit,
        onWeightChange: (Long) -> Unit,
        onDelete: () -> Unit,
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            OutlinedTextField(
                value = accountKey.permission?.actor ?: "",
                onValueChange = onAccountChange,
                label = { Text("Account") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = accountKey.permission?.permission ?: "",
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
        accountAuthWait: Wait,
        onWaitSecChange: (Long) -> Unit,
        onWeightChange: (Long) -> Unit,
        onDelete: () -> Unit,
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            OutlinedTextField(
                value = accountAuthWait.waitSec.toString(),
                onValueChange = { onWaitSecChange(it.toLongOrNull() ?: 0) },
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