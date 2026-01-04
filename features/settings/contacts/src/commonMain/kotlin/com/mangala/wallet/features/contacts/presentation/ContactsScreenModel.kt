package com.mangala.wallet.features.contacts.presentation

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.contacts.domain.usecases.DeleteContactUseCase
import com.mangala.wallet.features.contacts.domain.usecases.GetAllContactUseCase
import com.mangala.wallet.model.contact.ContactEntity
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactsScreenModel(
    private val blockchainUid: String?,
    private val getAllContactsUseCase: GetAllContactUseCase,
    private val deleteContactUseCase: DeleteContactUseCase,
) : BaseScreenModel() {

    private var contacts = listOf<ContactEntity>()

    private val _uiState = MutableStateFlow<ContactsScreenUiState>(
        ContactsScreenUiState.Data(
            emptyList()
        )
    )
    val uiState: StateFlow<ContactsScreenUiState> get() = _uiState

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    init {
        getAllContacts()
    }

    fun onSearchTextChanged(text: String) {
        _searchText.value = text
        _uiState.value = if (text.isNotBlank()){
            ContactsScreenUiState.Data(contacts.filter { it.name.contains(text, true) }
                .sortedBy { it.name })
        } else ContactsScreenUiState.Data(contacts.sortedBy { it.name })
    }

    fun deleteContact(id: Long) {
        screenModelScope.launch {
            deleteContactUseCase(id)
        }
    }

    private fun getAllContacts() {
        screenModelScope.launch {
            getAllContactsUseCase.invokeFlow(blockchainUid).collectLatest {
                contacts = it
                if (it.isEmpty()) {
                    _uiState.update { ContactsScreenUiState.Empty }
                } else {
                    _uiState.update { ContactsScreenUiState.Data(contacts) }
                }
            }
        }
    }
}