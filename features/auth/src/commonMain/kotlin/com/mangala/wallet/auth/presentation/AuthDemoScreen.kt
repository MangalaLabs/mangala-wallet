package com.mangala.wallet.auth.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.core.registry.ScreenRegistry
import com.mangala.wallet.core.auth.domain.model.AuthState
import com.mangala.wallet.biometry.BiometryAuthenticator
import com.mangala.wallet.biometry.effect.BindBiometryAuthenticatorEffect
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.LocalGlobalNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class AuthDemoScreen : Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val globalNavigator = LocalGlobalNavigator.current
        val screenModel = getScreenModel<AuthDemoScreenModel>()
        val authState by screenModel.authState.collectAsState(AuthState.Initial)
        val scope = rememberCoroutineScope()
        val biometryAuthenticator = koinInject<BiometryAuthenticator>()
        
        // Bind the biometry authenticator to the lifecycle
        BindBiometryAuthenticatorEffect(biometryAuthenticator)
        
        var userId by remember { mutableStateOf("ethan25@gmail.com") }
        var showRegistration by remember { mutableStateOf(false) }
        var hasExistingSession by remember { mutableStateOf(false) }
        
        // Check for existing session
        LaunchedEffect(Unit) {
            val session = screenModel.sessionManager.loadSession()
            hasExistingSession = session != null && screenModel.sessionManager.isSessionValid()
        }
        
        // Handle successful authentication and navigation
        LaunchedEffect(authState) {
            if (authState is AuthState.Authenticated) {
                println("Auth successful, attempting navigation to ConversationUiScreen")
                delay(1500) // Show success state briefly
                
                try {
                    // Get the ConversationUiScreen from the registry and navigate
                    val conversationScreen = ScreenRegistry.get(SharedScreen.ConversationUiScreen(null))
                    println("Successfully got ConversationUiScreen from registry, navigating...")
                    navigator.push(conversationScreen)
                    println("Navigation to ConversationUiScreen completed")
                } catch (e: Exception) {
                    println("Error navigating to ConversationUiScreen: ${e.message}")
                    e.printStackTrace()
                    // Fallback: just call the navigation handler to log the event
                    screenModel.navigateToConversationUi()
                }
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Passkey Auth Demo",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Auth State Display
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = when (authState) {
                        is AuthState.Authenticated -> MaterialTheme.colorScheme.primaryContainer
                        is AuthState.Error -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Text(
                    text = getAuthStateText(authState),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // User ID Input
            OutlinedTextField(
                value = userId,
                onValueChange = { userId = it },
                label = { Text("User ID") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Primary Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Register Button
                Button(
                    onClick = { showRegistration = true },
                    modifier = Modifier.weight(1f),
                    enabled = authState !is AuthState.Loading
                ) {
                    Text("Register")
                }
                
                // Auto Login Button
                Button(
                    onClick = {
                        scope.launch {
                            screenModel.authenticate(userId)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = authState !is AuthState.Loading
                ) {
                    Text("Auto Login")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Manual Authentication Options
            Text(
                text = "Or authenticate manually:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Manual Auth Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Passkey Button
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            screenModel.authenticateWithPasskey(userId)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = authState !is AuthState.Loading
                ) {
                    Text("Passkey")
                }
                
                // Biometric Button
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            screenModel.authenticateWithBiometric()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = authState !is AuthState.Loading && biometryAuthenticator.isBiometricAvailable() && hasExistingSession
                ) {
                    Text(if (!hasExistingSession) "Biometric (Login first)" else "Biometric")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    scope.launch {
                        screenModel.logout()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Logout")
            }

            // Test Buttons
            if (authState is AuthState.Authenticated) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Test Session Features",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Extend Session Button
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            screenModel.extendSession()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Extend Session (Rolling)")
                }
                
                // Validate Token Button
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            screenModel.validateToken()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Validate Token")
                }
                
                // Decode JWT Button
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            screenModel.decodeJwt()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Decode JWT")
                }
            }
        }
        
        // Registration Dialog
        if (showRegistration) {
            var userName by remember { mutableStateOf("ethan25") }
            var isRegistering by remember { mutableStateOf(false) }
            
            AlertDialog(
                onDismissRequest = { 
                    if (!isRegistering) showRegistration = false 
                },
                title = { Text("Register New Account") },
                text = {
                    Column {
                        Text("Enter your username to register with passkey:")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = userName,
                            onValueChange = { userName = it },
                            label = { Text("Username") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        if (isRegistering) {
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                isRegistering = true
                                val success = screenModel.registerPasskey(
                                    userId = userId,
                                    userName = userName.ifEmpty { userId }
                                )
                                isRegistering = false
                                if (success) {
                                    showRegistration = false
                                }
                            }
                        },
                        enabled = !isRegistering && userName.isNotEmpty()
                    ) {
                        Text("Register")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showRegistration = false },
                        enabled = !isRegistering
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        // Loading indicator
        if (authState is AuthState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
    
    private fun getAuthStateText(state: AuthState): String {
        return when (state) {
            is AuthState.Initial -> "Not authenticated. Enter user ID and tap Login."
            is AuthState.Loading -> "Authenticating..."
            is AuthState.Authenticated.WithPasskey -> "✅ Authenticated with Passkey\nUser: ${state.userId}"
            is AuthState.Authenticated.WithBiometric -> "✅ Authenticated with Biometric\nUser: ${state.userId}"
            is AuthState.Authenticated.WithPin -> "✅ Authenticated with PIN\nUser: ${state.userId}"
            is AuthState.Error.PasskeyError -> "❌ Passkey Error: ${state.message}"
            is AuthState.Error.BiometricError -> "❌ Biometric Error: ${state.message}"
            is AuthState.Error.PinError -> "❌ PIN Error: ${state.message}"
            is AuthState.Error.NetworkError -> "❌ Network Error: ${state.message}"
            is AuthState.Error.UnknownError -> "❌ Error: ${state.message}"
            is AuthState.NotAuthenticated -> "Authentication cancelled or failed"
        }
    }
}