package com.mangala.wallet.features.addressbook.presentation.note

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.addressbook.domain.usecase.note.CreateNoteTemplateUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.DeleteNoteTemplateUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.GetAllNoteTemplatesUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.GetNoteTemplateByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.UpdateNoteTemplateUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteTemplateScreenModel(
    private val getAllNoteTemplatesUseCase: GetAllNoteTemplatesUseCase,
    private val getNoteTemplateByIdUseCase: GetNoteTemplateByIdUseCase,
    private val createNoteTemplateUseCase: CreateNoteTemplateUseCase,
    private val updateNoteTemplateUseCase: UpdateNoteTemplateUseCase,
    private val deleteNoteTemplateUseCase: DeleteNoteTemplateUseCase
) : BaseScreenModel() {

    private val _state = MutableStateFlow(NoteTemplateListState())
    val state: StateFlow<NoteTemplateListState> = _state.asStateFlow()

    private val _editState = MutableStateFlow(NoteTemplateEditState())
    val editState: StateFlow<NoteTemplateEditState> = _editState.asStateFlow()

    fun onEvent(event: NoteTemplateEvent) {
        when (event) {
            is NoteTemplateEvent.LoadTemplates -> loadTemplates()
            is NoteTemplateEvent.LoadTemplate -> loadTemplate(event.templateId)
            is NoteTemplateEvent.CreateTemplate -> createTemplate(event.name, event.content)
            is NoteTemplateEvent.UpdateTemplate -> updateTemplate(
                event.templateId, event.name, event.content
            )
            is NoteTemplateEvent.DeleteTemplate -> deleteTemplate(event.templateId)
            is NoteTemplateEvent.ClearError -> clearError()
            else -> {}
        }
    }

    private fun loadTemplates() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getAllNoteTemplatesUseCase().fold(
                onSuccess = { templates ->
                    _state.update { it.copy(templates = templates, isLoading = false) }
                },
                onFailure = { error ->
                    _state.update { it.copy(error = error.message, isLoading = false) }
                }
            )
        }
    }

    private fun loadTemplate(templateId: String) {
        screenModelScope.launch {
            _editState.update { it.copy(isLoading = true, error = null) }

            getNoteTemplateByIdUseCase(templateId).fold(
                onSuccess = { template ->
                    _editState.update {
                        it.copy(
                            templateId = template.id,
                            name = template.name,
                            content = template.content,
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _editState.update { it.copy(error = error.message, isLoading = false) }
                }
            )
        }
    }

    private fun createTemplate(name: String, content: String) {
        screenModelScope.launch {
            _editState.update { it.copy(isLoading = true, error = null, isSuccess = false) }

            createNoteTemplateUseCase(name, content).fold(
                onSuccess = { template ->
                    _editState.update {
                        it.copy(
                            templateId = template.id,
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                    // Refresh templates list
                    loadTemplates()
                },
                onFailure = { error ->
                    _editState.update { it.copy(error = error.message, isLoading = false) }
                }
            )
        }
    }

    private fun updateTemplate(templateId: String, name: String, content: String) {
        screenModelScope.launch {
            _editState.update { it.copy(isLoading = true, error = null, isSuccess = false) }

            updateNoteTemplateUseCase(templateId, name, content).fold(
                onSuccess = { template ->
                    _editState.update {
                        it.copy(
                            templateId = template.id,
                            name = template.name,
                            content = template.content,
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                    // Refresh templates list
                    loadTemplates()
                },
                onFailure = { error ->
                    _editState.update { it.copy(error = error.message, isLoading = false) }
                }
            )
        }
    }

    private fun deleteTemplate(templateId: String) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            deleteNoteTemplateUseCase(templateId).fold(
                onSuccess = {
                    // Refresh the list
                    loadTemplates()
                },
                onFailure = { error ->
                    _state.update { it.copy(error = error.message, isLoading = false) }
                }
            )
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
        _editState.update { it.copy(error = null) }
    }
}