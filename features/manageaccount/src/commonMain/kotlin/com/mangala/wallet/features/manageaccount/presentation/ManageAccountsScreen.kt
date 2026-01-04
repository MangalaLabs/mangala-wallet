package com.mangala.wallet.features.manageaccount.presentation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.features.manageaccount.presentation.accountdetail.AccountDetailsScreen
import com.mangala.wallet.model.account.domain.AccountModel
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Add
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowRight
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.MoveUp
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Reorder
import com.mangala.wallet.ui.MangalaTextButton
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextTiny
import com.mangala.wallet.ui.TextTopBar
import com.mangala.wallet.ui.component.AccountAddressImage
import com.mangala.wallet.ui.modifier.roundedCornerItemShape
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import com.mangala.wallet.ui.reorderable.ReorderableItem
import com.mangala.wallet.ui.reorderable.detectReorder
import com.mangala.wallet.ui.reorderable.rememberReorderableLazyListState
import com.mangala.wallet.ui.reorderable.reorderable
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics

class ManageAccountsScreen : BaseScreen<ManageAccountsScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.MANAGE_ACCOUNTS
    override val screenClassName: String = ManageAccountsScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): ManageAccountsScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: ManageAccountsScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        val evmCreateAccountScreen = rememberScreen(SharedScreen.EvmCreateAccountScreen())

        val uiModel = screenModel.uiModel.collectAsStateMultiplatform().value

        ManageAccountsScreen(
            uiModel = uiModel,
            onBackClicked = { navigator.pop() },
            onClickCreateAccount = { navigator.push(evmCreateAccountScreen) },
            onClickAccount = {
                val accountDetailsScreen = AccountDetailsScreen(it.id)
                navigator.push(accountDetailsScreen)
            },
            onClickSave = {
                screenModel.onClickSave()
            },
            onRearrange = { fromIndex, toIndex ->
                screenModel.onRearrangeItem(fromIndex, toIndex)
            }
        )
    }

    @Composable
    private fun ManageAccountsScreen(
        uiModel: ManageAccountsScreenUiModel,
        onBackClicked: () -> Unit,
        onClickCreateAccount: () -> Unit,
        onClickAccount: (AccountModel) -> Unit,
        onClickSave: () -> Unit,
        onRearrange: (fromIndex: Int, toIndex: Int) -> Unit
    ) {
        var isInEditMode by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
                    modifier = Modifier.background(Colors.cloudGray).fillMaxWidth().padding(end = Spacing.SMALL),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.TINY),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(onClick = {
                            onBackClicked()
                        }) {
                            Icon(
                                imageVector = MangalaWalletPack.ArrowLeft,
                                contentDescription = "Back",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        TextTopBar(
                            text = MR.strings.all_manage_accounts.desc().localized(),
                        )
                    }
                    MangalaTextButton(
                        if (isInEditMode) MR.strings.all_save.desc().localized() else MR.strings.all_edit.desc().localized(),
                        fontWeight = FontWeight.Normal
                    ) {
                        if (isInEditMode) {
                            onClickSave()
                        }
                        isInEditMode = isInEditMode.not()
                    }
                }
            },
            modifier = Modifier.background(Colors.cloudGray).windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            AccountsList(
                uiModel.accounts,
                isEditMode = isInEditMode,
                onClickCreateAccount = onClickCreateAccount,
                onClickAccount = onClickAccount,
                onRearrange = onRearrange
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun AccountsList(
        accounts: List<AccountItemUiModel>,
        isEditMode: Boolean,
        onClickCreateAccount: () -> Unit,
        onClickAccount: (AccountModel) -> Unit,
        onRearrange: (fromIndex: Int, toIndex: Int) -> Unit
    ) {
        val state = rememberReorderableLazyListState(
            onMove = { from, to ->
                onRearrange(from.index - 1, to.index - 1) // No idea why lib is not returning zero-based index ¯\_(ツ)_/¯
            }
        )

        LazyColumn(
            state = state.listState,
            modifier = Modifier
                .fillMaxSize()
                .background(Colors.cloudGray)
                .padding(horizontal = Dimensions.Padding.default)
                .reorderable(state),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            contentPadding = PaddingValues(top = Spacing.XTINY)
        ) {
            stickyHeader {
                Column(Modifier
                    .fillMaxWidth()
                    .background(Colors.cloudGray)
                    .padding(vertical = Dimensions.Padding.small)
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Colors.gray)) {
                                append(MR.strings.message_manage_accounts_accounts_count.desc().localized())
                            }
                            withStyle(style = SpanStyle(color = Colors.darkGray)) {
                                append(" (${accounts.size})")
                            }
                        })
                        IconButton(
                            onClick = onClickCreateAccount,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = MangalaWalletPack.Add,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
            itemsIndexed(accounts, key = { _: Int, item: AccountItemUiModel -> item.account.account.id }) { index, item ->
                ReorderableItem(state, key = item.account.account.id) { isDragging ->
                    val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp)
                    AccountItem(
                        item,
                        isEditMode,
                        shape = roundedCornerItemShape(accounts, index),
                        Modifier
                            .shadow(elevation.value)
                            .background(MaterialTheme.colors.surface),
                        rearrangeModifier = Modifier.detectReorder(state),
                        onMoveUp = {
                            onRearrange(index, index - 1)
                        }
                    ) {
                        onClickAccount(item.account.account)
                    }
                }
            }
        }
    }

    @Composable
    private fun AccountItem(
        accountBlockchainModel: AccountItemUiModel,
        isEditMode: Boolean,
        shape: Shape,
        modifier: Modifier = Modifier,
        rearrangeModifier: Modifier = Modifier,
        onMoveUp: () -> Unit,
        onClick: () -> Unit
    ) {
        val accountModel = accountBlockchainModel.account
        val isHiddenAccount = accountModel.account.isHidden
        val hiddenAccountAlphaModifier = if (isHiddenAccount) Modifier.alpha(0.5f) else Modifier

        Row(
            Modifier
                .clip(shape)
                .then(modifier)
                .background(MaterialTheme.colors.surface)
                .clickable(onClick = onClick)
                .padding(
                    horizontal = Dimensions.Padding.default,
                    vertical = Dimensions.Padding.small
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f).then(hiddenAccountAlphaModifier)
            ) {
                AccountAddressImage(accountBlockchainModel.account.bip44Address)
                Spacer(Modifier.width(Spacing.TINY))
                Column(modifier = Modifier.fillMaxWidth()) {
                    TextNormal(
                        text = accountModel.account.name,
                        color = Colors.darkGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(Spacing.XTINY))
                    TextDescription2(
                        text = accountBlockchainModel.nativeCoinBalance,
                        color = Colors.darkGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Row {
                if (isEditMode) {
                    Icon(
                        imageVector = MangalaWalletPack.MoveUp,
                        contentDescription = null,
                        Modifier.size(20.dp).clickable { onMoveUp() }
                    )
                    Spacer(Modifier.width(Spacing.XSMALL))
                    Column(Modifier.fillMaxHeight().then(rearrangeModifier)) {
                        // Column to expand touch target
                        Icon(
                            imageVector = MangalaWalletPack.Reorder,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isHiddenAccount) {
                            TextTiny(
                                text = MR.strings.all_hidden.desc().localized(),
                                color = Colors.darkGray,
                                modifier = Modifier.background(Colors.cloudGray, RoundedCornerShape(
                                    CornerRadius.Medium)).padding(horizontal = Dimensions.Padding.half, vertical = Dimensions.Padding.quarter),
                            )
                            Spacer(Modifier.width(Spacing.TINY))
                        }
                        Icon(
                            imageVector = MangalaWalletPack.ArrowRight,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
