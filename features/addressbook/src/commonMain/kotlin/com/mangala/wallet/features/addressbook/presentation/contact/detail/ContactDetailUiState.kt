package com.mangala.wallet.features.addressbook.presentation.contact.detail

import androidx.compose.ui.graphics.vector.ImageVector
import com.mangala.wallet.features.addressbook.data.model.ContactDetailModel
import com.mangala.wallet.features.addressbook.data.model.enum.AuthRequirement
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode

data class ContactDetailUiState(
    val isLoading: Boolean = false,
    val contactDetail: ContactDetailModel? = null,
    val isFavorite: Boolean = false,
    val isHighSecurity: Boolean = false,
    val isAuthenticated: Boolean = false,
    val requiresAuth: Boolean = false,
    val isFullInfoVisible: Boolean = true,
    val error: String? = null,
    val needsAuthentication: Boolean = false
)
data class AuthState(
    val isAuthenticated: Boolean = false,
    val requiresAuth: Boolean = false,
    val authMethod: AuthRequirement = AuthRequirement.NONE
)

data class ContactInfoItem(
    val icon: ImageVector,
    val value: String,
    val label: String
)

