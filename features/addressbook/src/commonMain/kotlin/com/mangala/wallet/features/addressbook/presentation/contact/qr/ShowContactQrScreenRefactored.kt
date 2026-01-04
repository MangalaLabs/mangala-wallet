package com.mangala.wallet.features.addressbook.presentation.contact.qr

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.mangala.wallet.features.addressbook.domain.qr.QrDataType
import com.mangala.wallet.features.addressbook.presentation.qr.ShowQrScreen

/**
 * Refactored ShowContactQrScreen using new QR architecture
 * This replaces the old complex ShowContactQrScreenModel
 */
class ShowContactQrScreenRefactored(
    private val contactId: String,
    private val onBackClick: () -> Unit = {}
) : Screen {
    
    @Composable
    override fun Content() {
        com.mangala.wallet.ui.LocalBottomNavigationVisibility.current.value = false
        
        // Delegate to unified QR screen
        ShowQrScreen(
            dataType = QrDataType.Contact(contactId),
            onBackClick = onBackClick
        ).Content()
    }
}

/**
 * Refactored ShowGroupQrScreen
 */
class ShowGroupQrScreenRefactored(
    private val groupId: String,
    private val onBackClick: () -> Unit = {}
) : Screen {
    
    @Composable
    override fun Content() {
        com.mangala.wallet.ui.LocalBottomNavigationVisibility.current.value = false
        
        ShowQrScreen(
            dataType = QrDataType.Group(groupId),
            onBackClick = onBackClick
        ).Content()
    }
}

/**
 * Refactored ShowTagQrScreen  
 */
class ShowTagQrScreenRefactored(
    private val tagId: String,
    private val onBackClick: () -> Unit = {}
) : Screen {
    
    @Composable
    override fun Content() {
        com.mangala.wallet.ui.LocalBottomNavigationVisibility.current.value = false
        
        ShowQrScreen(
            dataType = QrDataType.Tag(tagId),
            onBackClick = onBackClick
        ).Content()
    }
}

/**
 * Refactored ShowAddressQrScreen
 */
class ShowAddressQrScreenRefactored(
    private val addressId: String,
    private val onBackClick: () -> Unit = {}
) : Screen {
    
    @Composable
    override fun Content() {
        ShowQrScreen(
            dataType = QrDataType.Address(addressId),
            onBackClick = onBackClick
        ).Content()
    }
}