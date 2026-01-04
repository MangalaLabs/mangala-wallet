package com.mangala.wallet.features.addressbook.presentation.shared.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicator
import com.mangala.wallet.ui.theme.MangalaTypography

/**
 * Reusable loading overlay component for Address Book features
 * Provides consistent loading UI across all modules
 */
@Composable
fun AddressBookLoadingOverlay(
    loadingState: LoadingState,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Black.copy(alpha = 0.5f),
    showMessage: Boolean = true,
    content: @Composable () -> Unit = {}
) {
    if (loadingState.isLoading) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                MangalaCircularProgressIndicator()
                
                if (showMessage && loadingState.getMessage().isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = loadingState.getMessage(),
                        style = MangalaTypography.Size14Medium(),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Inline loading indicator for use within content
 * Does not block the entire screen
 */
@Composable
fun InlineLoadingIndicator(
    loadingState: LoadingState,
    modifier: Modifier = Modifier,
    showMessage: Boolean = true
) {
    if (loadingState.isLoading) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MangalaCircularProgressIndicator(
                    modifier = Modifier.size(24.dp)
                )
                
                if (showMessage && loadingState.getMessage().isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = loadingState.getMessage(),
                        style = MangalaTypography.Size14Medium(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Loading state wrapper that shows content or loading based on state
 * Useful for replacing early return pattern in Tag screens
 */
@Composable
fun LoadingStateWrapper(
    loadingState: LoadingState,
    modifier: Modifier = Modifier,
    showOverlay: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        content()
        
        if (showOverlay && loadingState.isLoading) {
            AddressBookLoadingOverlay(loadingState = loadingState)
        }
    }
}