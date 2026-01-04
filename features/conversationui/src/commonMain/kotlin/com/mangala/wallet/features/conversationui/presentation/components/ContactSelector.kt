package com.mangala.wallet.features.conversationui.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.conversationui.presentation.ContactSelectorState
import com.mangala.wallet.ui.component.MangalaButtonStyle
import com.mangala.wallet.ui.component.MangalaGradientButton

@Composable
fun ContactSelector(
    state: ContactSelectorState,
    onSelectContact: (String) -> Unit,
    onCancel: () -> Unit,
    onManualAddressInput: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!state.isVisible) return

    var searchQuery by remember { mutableStateOf("") }
    
    val filteredContacts = remember(searchQuery, state.contacts) {
        if (searchQuery.isBlank()) {
            state.contacts
        } else {
            state.contacts.filter { contact ->
                contact.contactName.contains(searchQuery, ignoreCase = true) ||
                contact.walletAddress.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (state.subtitle.isNotEmpty()) {
                        Text(
                            text = state.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                
                // Cancel button
                TextButton(onClick = onCancel) {
                    Text("Cancel")
                }
            }

            // Search field
            if (state.enableSearch && state.contacts.size > 3) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search contacts") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Contact list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = (state.maxVisible * 80).dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(
                    items = filteredContacts.take(state.maxVisible),
                    key = { it.contactId }
                ) { contact ->
                    ContactSelectorItem(
                        contact = contact,
                        showAddressCount = state.showAddressCount,
                        onClick = { onSelectContact(contact.contactId) }
                    )
                }
                
                // Show "more contacts" message if needed
                if (filteredContacts.size > state.maxVisible) {
                    item {
                        Text(
                            text = "Showing ${state.maxVisible} of ${filteredContacts.size} contacts",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
            
            if (state.allowManualAddressInput) {
                Spacer(modifier = Modifier.height(12.dp))
                
                MangalaGradientButton(
                    onClick = onManualAddressInput,
                    modifier = Modifier.fillMaxWidth(),
                    buttonStyle = MangalaButtonStyle.SOLID_GRAY
                ) {
                    Text("Enter Address Manually")
                }
            }
        }
    }
}

@Composable
private fun ContactSelectorItem(
    contact: ContactModel,
    showAddressCount: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.contactName.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.contactName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (contact.walletAddress.length <= 12) {
                        "${contact.walletAddress} (${contact.blockchainName})"
                    } else {
                        "${contact.walletAddress.take(6)}...${contact.walletAddress.takeLast(4)} (${contact.blockchainName})"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Address count badge (placeholder - would need to be fetched)
            if (showAddressCount) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Text("1")
                }
            }
        }
    }
}