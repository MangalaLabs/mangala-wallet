package com.mangala.wallet.features.chains.antelope.presentation.permission.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.antelope.base.api.model.Permission
import com.mangala.wallet.features.chains.antelope.presentation.permission.detail.PermissionDetailScreen
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.ui.MangalaTextButton
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicator
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import org.koin.core.parameter.parametersOf

class PermissionListScreen(
    private val accountName: String,
    private val accountPermission: String,
    private val permissionParentsName: List<String>
) : BaseScreen<PermissionListScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_PERMISSIONS_LIST
    override val screenClassName: String = PermissionListScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): PermissionListScreenModel =
        getScreenModel<PermissionListScreenModel>(
            parameters = { parametersOf(accountName, accountPermission) }
        )

    @Composable
    override fun ScreenContent(screenModel: PermissionListScreenModel) {
        val uiState = screenModel.uiState.collectAsStateMultiplatform().value
        val navigator = LocalNavigator.currentOrThrow

        Column(Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) {
            MangalaTextButton(
                text = "Back",
                color = Color.Black,
                onClick = {
                    navigator.pop()
                },
                fontSize = FontType.REGULAR
            )

            MangalaTextButton(
                text = "Load account permission",
                color = Color.Blue,
                onClick = screenModel::onClickLoadAccount,
                fontSize = FontType.REGULAR
            )

            Spacer(modifier = Modifier)

            Text("Your account: $accountName@$accountPermission")

            Spacer(modifier = Modifier)

            when (uiState) {

                is PermissionListUiState.Loading -> {
                    MaxSizeBox(
                        contentAlignment = Alignment.Center
                    ) {
                        MangalaCircularProgressIndicator(color = Color.Red)
                    }
                }

                is PermissionListUiState.Success -> {
                    PermissionList(uiState.uiModel, navigator, screenModel::onDelete)
                }

                is PermissionListUiState.Error -> {
                    println("ui state error")
                }

                is PermissionListUiState.DeletePermissionFailed -> {
                    PermissionDeleteFailed(navigator, uiState.message)
                }

                is PermissionListUiState.DeletePermissionSuccess -> {
                    PermissionDeleteSuccessfully(navigator)
                }
            }
        }
    }

    @Composable
    fun PermissionList(
        uiModel: UiModel,
        navigator: Navigator,
        onDelete: (permissionName: String?) -> Unit,
    ) {
        uiModel.permissions?.let {
            LazyColumn(modifier = Modifier.padding(8.dp)) {
                items(uiModel.permissions.size) {
                    PermissionItem(
                        uiModel.permissions[it],
                        navigator,
                        uiModel.permissionValidForDeleteMap[uiModel.permissions[it].permName],
                        onDelete
                    )
                }
            }
        }
    }

    @Composable
    fun PermissionItem(
        permission: Permission,
        navigator: Navigator,
        canDelete: Boolean?,
        onDelete: (permissionName: String?) -> Unit,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            elevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                permission.permName?.let {
                    Text(
                        "Permission Name: ${permission.permName}",
                        style = MaterialTheme.typography.h6
                    )
                }
                permission.parent?.let {
                    Text(
                        "Permission parent Name: ${permission.parent}",
                        style = MaterialTheme.typography.h6
                    )
                } ?: run {

                }
                permission.requiredAuth?.let { requiredAuth ->
                    requiredAuth.threshold?.let {
                        Text(
                            "Permission Required auth threshold: ${requiredAuth.threshold}",
                            style = MaterialTheme.typography.h6
                        )
                    }
                }

                Row {
                    MangalaTextButton(
                        text = "detail",
                        color = Color.Blue,
                        onClick = {
                            navigator.push(
                                PermissionDetailScreen(
                                    permission,
                                    accountName,
                                    accountPermission,
                                    permissionParentsName
                                )
                            )
                        }
                    )
                    canDelete?.let {
                        MangalaTextButton(
                            text = "Delete",
                            color = Color.Red,
                            onClick = { onDelete(permission.permName) }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun PermissionDeleteSuccessfully(navigator: Navigator) {
        Column {
            Text("Your permission is deleted successfully.")
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
    fun PermissionDeleteFailed(navigator: Navigator, message: String) {
        Column {
            Text("Your permission is deleted failed.")
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