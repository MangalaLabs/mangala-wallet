package com.mangala.wallet.features.addressbook.presentation.contact.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.features.addressbook.data.model.enum.AuthRequirement

/**
 * Save button component for the edit contact screen
 */
@Composable
fun ContactEditSaveButton(
    onSaveClick: () -> Unit,
    isEnabled: Boolean
) {
    Button(
        onClick = onSaveClick,
        enabled = isEnabled,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Save,
            contentDescription = "Save",
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Save")
    }
}

/**
 * Authentication required UI component
 */
@Composable
fun AuthenticationRequiredUI(
    authMethod: AuthRequirement,
    onAuthenticate: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (authMethod) {
                        AuthRequirement.BIOMETRIC, AuthRequirement.BIOMETRIC_PIN -> Icons.Default.Fingerprint
                        else -> Icons.Default.Lock
                    },
                    contentDescription = "Authentication Required",
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Authentication Required",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = when (authMethod) {
                    AuthRequirement.BIOMETRIC -> "This contact requires biometric authentication to edit."
                    AuthRequirement.BIOMETRIC_PIN -> "This contact requires biometric authentication or PIN to edit."
                    else -> "This contact requires authentication to edit."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Button(
                onClick = onAuthenticate,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = when (authMethod) {
                        AuthRequirement.BIOMETRIC, AuthRequirement.BIOMETRIC_PIN -> Icons.Default.Fingerprint
                        else -> Icons.Default.Lock
                    },
                    contentDescription = "Authenticate"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (authMethod) {
                        AuthRequirement.BIOMETRIC -> "Authenticate with Biometrics"
                        AuthRequirement.BIOMETRIC_PIN -> "Authenticate"
                        else -> "Authenticate"
                    }
                )
            }
        }
    }
}

/**
 * Favorite toggle component
 */
@Composable
fun FavoriteToggle(
    isFavorite: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Favorite Contact",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Switch(
                checked = isFavorite,
                onCheckedChange = { onToggle() }
            )
        }
    }
}