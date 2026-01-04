package com.mangala.wallet.features.contacts.presentation.contactdetail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.contacts.domain.usecases.GetContactByIdUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ContactDetailScreenModel(
    private val contactId: Long,
    private val getContactByIdUseCase: GetContactByIdUseCase
) : BaseScreenModel() {

    private val _uiState = MutableStateFlow<ContactDetailScreenUiState>(ContactDetailScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadContactData()
    }

    private fun loadContactData() {
        screenModelScope.launch {
            getContactByIdUseCase.invokeFlow(contactId).collectLatest { contact ->
                contact?.let {
                    _uiState.update {
                        ContactDetailScreenUiState.Success(contact)
                    }
                }
            }
        }
    }
}