package com.mangala.wallet.features.chains.antelope.presentation.permission.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.antelope.base.api.model.Account
import com.mangala.antelope.base.api.model.Key
import com.mangala.antelope.base.api.model.Permission
import com.mangala.antelope.base.api.model.Wait
import com.mangala.wallet.features.chains.antelope.presentation.permission.updatepermission.UpdatePermissionScreen
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import org.koin.core.parameter.parametersOf


class PermissionDetailScreen(
    private val permission: Permission,
    private val authorizationName: String,
    private val authorizationPermission: String,
    private val permissionParentsName: List<String>
) : BaseScreen<PermissionDetailScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_PERMISSION_DETAIL
    override val screenClassName: String = PermissionDetailScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): PermissionDetailScreenModel {
        return getScreenModel(parameters = {
            parametersOf(authorizationName)
        })
    }

    @Composable
    override fun ScreenContent(screenModel: PermissionDetailScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        InputPermission(permission = permission, navigator = navigator)
    }

    @Composable
    fun InputPermission(
        permission: Permission,
        navigator: Navigator,
    ) {
        var name by remember { mutableStateOf(permission.permName) }
        var threshold by remember { mutableStateOf(permission.requiredAuth!!.threshold) }
        var permissionParentExpanded by remember { mutableStateOf(false) }
        var permissionParentSelected by remember { mutableStateOf(permission.parent) }
        var keyPairs by remember { mutableStateOf(permission.requiredAuth!!.keys!!) }
        var accountPairs by remember { mutableStateOf(permission.requiredAuth!!.accounts!!.toTypedArray()) }
        var waitPairs by remember { mutableStateOf(permission.requiredAuth!!.waits!!) }

        Column {
            Text(
                text = "Permission detail",
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
                    readOnly = true,
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
                OutlinedTextField(
                    value = permissionParentSelected!!,
                    onValueChange = { },
                    label = { Text("Chose permission parent") },
                    readOnly = true
                )
            }

            Spacer(modifier = Modifier)

            threshold?.let {
                OutlinedTextField(
                    value = it.toString(),
                    onValueChange = { threshold = it.toInt() },
                    label = { Text("Threshold") },
                    readOnly = true,
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
                        }
                    )
                }

                Spacer(modifier = Modifier)
            }

            Spacer(modifier = Modifier)

            Column {
                Text("Accounts")
                accountPairs.forEachIndexed { index, pair ->
                    AccountAuthAccountRow(
                        accountKey = pair,
                    )
                }

                Spacer(modifier = Modifier)
            }


            Spacer(modifier = Modifier)

            Column {
                Text("Waits")
                waitPairs.forEachIndexed { _, pair ->
                    AccountAuthWaitRow(accountAuthWait = pair)
                }
                Spacer(modifier = Modifier)
            }

            Spacer(modifier = Modifier)

            Button(
                onClick = {
                    navigator.push(
                        UpdatePermissionScreen(
                            permission,
                            authorizationName,
                            authorizationPermission,
                            permissionParentsName
                        )
                    )
                }, colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Blue, contentColor = Color.White
                )
            ) {
                Text("Edit")
            }
        }
    }

    @Composable
    fun AccountKeyRow(
        accountKey: Key,
        onKeyChange: (String) -> Unit,
        onValueChange: (Short) -> Unit,
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
        }
    }

    @Composable
    fun AccountAuthAccountRow(
        accountKey: Account,
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            OutlinedTextField(
                value = accountKey.permission!!.actor!!,
                onValueChange = {},
                readOnly = true,
                label = { Text("Account") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = accountKey.permission!!.permission!!,
                onValueChange = {},
                readOnly = true,
                label = { Text("Permission") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = accountKey.weight.toString(),
                onValueChange = {},
                readOnly = true,
                label = { Text("Weight") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }
    }

    @Composable
    fun AccountAuthWaitRow(
        accountAuthWait: Wait,
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            OutlinedTextField(
                value = accountAuthWait.waitSec.toString(),
                onValueChange = { },
                readOnly = true,
                label = { Text("Wait sec") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = accountAuthWait.weight.toString(),
                onValueChange = {},
                readOnly = true,
                label = { Text("Weight") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }
    }


}