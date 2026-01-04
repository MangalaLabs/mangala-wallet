package com.mangala.wallet.features.contacts.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.model.contact.ContactEntity
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Add
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowRight
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.MangalaCommonDialog
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicatorFullScreen
import com.mangala.wallet.ui.component.MangalaWalletIconButton
import com.mangala.wallet.ui.component.MangalaWalletSearchBar
import com.mangala.wallet.ui.component.MangalaWalletSwipeToReveal
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.modifier.roundedCornerItemShape
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

class ContactsScreen(
    private val blockchainUid: String?,
    @Transient private val onSelectContact: ((Long) -> Unit)? = null,
) : BaseScreen<ContactsScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.CONTACTS
    override val screenClassName: String = ContactsScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible = false

    @Composable
    override fun createScreenModel(): ContactsScreenModel = getScreenModel<ContactsScreenModel>(
        parameters = {
            parametersOf(
                blockchainUid
            )
        }
    )

    @Composable
    override fun ScreenContent(screenModel: ContactsScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        val addContactScreen = rememberScreen(SharedScreen.AddContactScreen())

        val uiState = screenModel.uiState.collectAsStateMultiplatform().value

        ContactsScreen(
            uiState = uiState,
            screenModel = screenModel,
            onBackClicked = { navigator.pop() },
            onClickAddContact = { navigator.push(addContactScreen) },
            onClickContactItem = { contact ->
                onSelectContact?.let { onSelectContact ->
                    onSelectContact(contact.id)
                } ?: run {
                    val screen = ScreenRegistry.get(SharedScreen.ContactDetailScreen(contact.id))
                    navigator.push(screen)
                }
            }
        )
    }

    @Composable
    fun ContactsScreen(
        uiState: ContactsScreenUiState,
        screenModel: ContactsScreenModel,
        onBackClicked: () -> Unit,
        onClickAddContact: () -> Unit,
        onClickContactItem: (ContactEntity) -> Unit
    ) {
        Column(
            Modifier.fillMaxSize().background(Colors.cloudGray)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            MangalaWalletTopBar(
                text = MR.strings.all_contacts.desc().localized(),
                onBackClicked = onBackClicked,
                trailingButton = {
                    if (onSelectContact == null) {
                        // Hide the add button when we navigate to this screen just to pick contact (e.g from send flow)
                        MangalaWalletIconButton(
                            icon = MangalaWalletPack.Add,
                            onClick = onClickAddContact
                        )
                    }
                }
            )
            when (uiState) {

                is ContactsScreenUiState.Data -> {
                    ContactsList(
                        contacts = uiState.contacts, screenModel, onClickContactItem
                    )
                }

                ContactsScreenUiState.Empty -> {
                    Box(Modifier.fillMaxSize()) {
                        Column(
                            Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TextDescription2(
                                text = MR.strings.message_contacts_no_contacts.desc().localized(),
                                fontStyle = FontStyle.Italic
                            )
                            Spacer(Modifier.height(Spacing.BASE))
                            if (onSelectContact == null) {
                                ButtonNormal( // TODO: To confirm with Son for final design of this button
                                    text = MR.strings.all_add_contact.desc().localized(),
                                    onClick = onClickAddContact,
                                    buttonModifier = Modifier.height(44.dp).width(200.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ContactsList(
        contacts: List<ContactEntity>,
        screenModel: ContactsScreenModel,
        onClickContactItem: (ContactEntity) -> Unit
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(Spacing.SMALL)) {
            val searchText = screenModel.searchText.collectAsStateMultiplatform()

            MangalaWalletSearchBar(
                searchText = searchText,
                placeholder = MR.strings.message_contacts_search_contact.desc().localized(),
                onValueChange = screenModel::onSearchTextChanged
            )

            if (contacts.isEmpty() && searchText.value.isNotBlank()) {
                TextDescription2(
                    text = MR.strings.message_contacts_no_search_result.desc().localized(),
                    color = Colors.gray,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentHeight(align = Alignment.CenterVertically)
                )
            }

            LazyColumn {
                contacts.groupBy { it.name.first().uppercase() }
                    .forEach { (letter, contactsByLetter) ->
                        item {
                            Spacer(modifier = Modifier.height(Spacing.SMALL))
                            TextNormal(
                                text = letter,
                                color = Colors.gray,
                                modifier = Modifier.padding(Spacing.TINY)
                            )
                            Spacer(modifier = Modifier.height(Spacing.TINY))
                        }

                        items(contactsByLetter.size) { index ->
                            val shape = roundedCornerItemShape(contactsByLetter, index)
                            val currentItem = contactsByLetter[index]
                            val isOpenConfirmDeleteDialog = remember { mutableStateOf(false) }
                            if (index != 0) Spacer(modifier = Modifier.height(1.dp))
                            MangalaWalletSwipeToReveal(
                                shape = shape,
                                revealedBackgroundColor = Colors.coral,
                                text = MR.strings.all_delete.desc().localized(),
                                onClickRevealed = { isOpenConfirmDeleteDialog.value = true }
                            ){
                                if (isOpenConfirmDeleteDialog.value){
                                    MangalaCommonDialog(
                                        title = MR.strings.title_delete_contact.desc().localized(),
                                        message = MR.strings.message_delete_contact.desc().localized(),
                                        positiveButtonText = MR.strings.all_delete.desc().localized().uppercase(),
                                        negativeButtonText = MR.strings.all_cancel.desc().localized().uppercase(),
                                        onNegativeClick = { isOpenConfirmDeleteDialog.value = false },
                                        onPositiveClick = { screenModel.deleteContact(currentItem.id) }
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .clip(shape)
                                        .clickable { onClickContactItem(currentItem) }
                                        .background(
                                            color = Color.White
                                        )
                                        .padding(
                                            horizontal = Spacing.SMALL,
                                            vertical = Spacing.XSMALL
                                        )
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    TextNormal(
                                        text = currentItem.name,
                                        color = Colors.darkGray,
                                    )

                                    MangalaWalletIconButton(
                                        icon = MangalaWalletPack.ArrowRight,
                                        onClick = { onClickContactItem(currentItem) }
                                    )
                                }
                            }
                        }
                    }
            }
        }
    }
}