package com.mangala.wallet.features.addressbook.presentation.tag.add

import androidx.compose.ui.graphics.Color
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.features.addressbook.presentation.tag.ContactWithAddresses

data class CreateTagUiState(
    val tagName: String = "",
    val selectedBackgroundColor: Color = ColorsNew.tagTeal,
    val selectedTextColor: Color = ColorsNew.textWhite,
    val isCustomTextColorSelected: Boolean = false, // Track if user manually selected text color
    val icon: String? = null,
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val selectedContactIds: List<String> = emptyList(), // Stores contact IDs for tag association
    val availableContacts: List<ContactWithAddresses> = emptyList(),
    val filteredContacts: List<ContactWithAddresses> = emptyList(),
    val addressSearchQuery: String = "",
    val saveComplete: Boolean = false,
    val createdTagId: String? = null,
    val contactFavoriteStatus: Map<String, Boolean> = emptyMap() // Maps contact ID to favorite status
)