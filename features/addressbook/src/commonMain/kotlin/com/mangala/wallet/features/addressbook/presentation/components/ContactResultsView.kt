package com.mangala.wallet.features.addressbook.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangala.wallet.features.addressbook.domain.model.ContactInfo
import com.mangala.wallet.features.addressbook.presentation.components.ContactCard
import com.mangala.wallet.features.addressbook.presentation.components.ContactCardStyles
import com.mangala.wallet.features.addressbook.presentation.message.ContactResultsMessage

@Composable
fun ContactResultsView(
    message: ContactResultsMessage,
    onContactClick: (ContactInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Found ${message.totalCount} contacts for \"${message.query}\"",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(
            modifier = Modifier.heightIn(max = 300.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(message.contacts) { contact ->
                ContactCard(
                    contact = contact,
                    onClick = { onContactClick(contact) },
                    style = ContactCardStyles.conversationUi(),
                    blockchainAddress = contact.addresses.firstOrNull()?.address,
                    blockchainNetwork = contact.addresses.firstOrNull()?.network,
                    onCopyAddress = { /* Handle copy address action */ },
                )
            }
        }
    }
}
