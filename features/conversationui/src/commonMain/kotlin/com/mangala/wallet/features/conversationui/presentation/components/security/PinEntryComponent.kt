package com.mangala.wallet.features.conversationui.presentation.components.security

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * PIN entry component for secure transaction authentication
 */
@Composable
fun PinEntryComponent(
    pinLength: Int = 6,
    onPinComplete: (String) -> Unit,
    onPinChange: (String) -> Unit = {},
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var currentPin by remember { mutableStateOf("") }
    
    LaunchedEffect(currentPin) {
        onPinChange(currentPin)
        if (currentPin.length == pinLength) {
            onPinComplete(currentPin)
        }
    }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // PIN display dots
        PinDisplayDots(
            pinLength = pinLength,
            currentLength = currentPin.length,
            isError = isError,
            modifier = Modifier.padding(vertical = 24.dp)
        )
        
        // Error message
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // PIN keypad
        PinKeypad(
            onNumberClick = { number ->
                if (currentPin.length < pinLength) {
                    currentPin += number
                }
            },
            onBackspaceClick = {
                if (currentPin.isNotEmpty()) {
                    currentPin = currentPin.dropLast(1)
                }
            },
            onClearClick = {
                currentPin = ""
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PinDisplayDots(
    pinLength: Int,
    currentLength: Int,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pinLength) { index ->
            val isFilled = index < currentLength
            val dotColor = when {
                isError -> MaterialTheme.colorScheme.error
                isFilled -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.outline
            }
            
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(
                        if (isFilled) dotColor else Color.Transparent
                    )
                    .border(
                        width = 2.dp,
                        color = dotColor,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun PinKeypad(
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keypadNumbers = listOf(
        "1", "2", "3",
        "4", "5", "6", 
        "7", "8", "9",
        "C", "0", "⌫"
    )
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(keypadNumbers) { key ->
            PinKeypadButton(
                text = key,
                onClick = {
                    when (key) {
                        "C" -> onClearClick()
                        "⌫" -> onBackspaceClick()
                        else -> onNumberClick(key)
                    }
                }
            )
        }
    }
}

@Composable
private fun PinKeypadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSpecialKey = text == "C" || text == "⌫"
    
    Card(
        modifier = modifier
            .size(60.dp)
            .clickable { onClick() },
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = if (isSpecialKey) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = if (isSpecialKey) FontWeight.Normal else FontWeight.Medium,
                color = if (isSpecialKey) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Simplified PIN verification component for quick authentication
 */
@Composable
fun QuickPinVerification(
    expectedPin: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var attempts by remember { mutableStateOf(0) }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    PinEntryComponent(
        pinLength = expectedPin.length,
        onPinComplete = { enteredPin ->
            if (enteredPin == expectedPin) {
                onSuccess()
            } else {
                attempts++
                isError = true
                errorMessage = when {
                    attempts >= 3 -> "Too many failed attempts. Try again later."
                    else -> "Incorrect PIN. ${3 - attempts} attempts remaining."
                }
                onFailure(errorMessage ?: "Incorrect PIN")
            }
        },
        onPinChange = { 
            if (isError) {
                isError = false
                errorMessage = null
            }
        },
        isError = isError,
        errorMessage = errorMessage,
        modifier = modifier
    )
}