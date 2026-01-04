package com.mangala.wallet.features.addressbook.presentation.privacy

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * UI Tests for Privacy Mode Toggle Components
 * 
 * Test Coverage:
 * - Component rendering in different states
 * - Click interactions and callbacks
 * - Accessibility features
 * - Loading states
 * - Visual state changes
 */
class PrivacyModeToggleTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun `PrivacyModeToggle renders correctly when disabled`() {
        composeTestRule.setContent {
            MaterialTheme {
                PrivacyModeToggle(
                    isEnabled = false,
                    onToggle = { }
                )
            }
        }
        
        composeTestRule
            .onNodeWithContentDescription("Privacy mode is disabled. Tap to enable.")
            .assertExists()
            .assertIsEnabled()
            .assertHasClickAction()
    }
    
    @Test
    fun `PrivacyModeToggle renders correctly when enabled`() {
        composeTestRule.setContent {
            MaterialTheme {
                PrivacyModeToggle(
                    isEnabled = true,
                    onToggle = { }
                )
            }
        }
        
        composeTestRule
            .onNodeWithContentDescription("Privacy mode is enabled. Tap to disable.")
            .assertExists()
            .assertIsEnabled()
            .assertHasClickAction()
    }
    
    @Test
    fun `PrivacyModeToggle handles click correctly`() {
        var clicked = false
        var isEnabled = false
        
        composeTestRule.setContent {
            MaterialTheme {
                PrivacyModeToggle(
                    isEnabled = isEnabled,
                    onToggle = { 
                        clicked = true
                        isEnabled = !isEnabled
                    }
                )
            }
        }
        
        composeTestRule
            .onNodeWithContentDescription("Privacy mode is disabled. Tap to enable.")
            .performClick()
        
        assertTrue(clicked)
        assertTrue(isEnabled)
    }
    
    @Test
    fun `PrivacyModeToggle is disabled when loading`() {
        composeTestRule.setContent {
            MaterialTheme {
                PrivacyModeToggle(
                    isEnabled = false,
                    onToggle = { },
                    isLoading = true
                )
            }
        }
        
        composeTestRule
            .onNodeWithContentDescription("Privacy mode is disabled. Tap to enable.")
            .assertIsNotEnabled()
    }
    
    @Test
    fun `PrivacyModeToggle uses custom content description`() {
        val customDescription = "Custom privacy toggle description"
        
        composeTestRule.setContent {
            MaterialTheme {
                PrivacyModeToggle(
                    isEnabled = false,
                    onToggle = { },
                    contentDescription = customDescription
                )
            }
        }
        
        composeTestRule
            .onNodeWithContentDescription(customDescription)
            .assertExists()
    }
    
    @Test
    fun `CompactPrivacyModeToggle renders and works correctly`() {
        var toggleCount = 0
        
        composeTestRule.setContent {
            MaterialTheme {
                CompactPrivacyModeToggle(
                    isEnabled = false,
                    onToggle = { toggleCount++ }
                )
            }
        }
        
        composeTestRule
            .onNodeWithContentDescription("Enable privacy mode")
            .assertExists()
            .assertIsEnabled()
            .performClick()
        
        assertEquals(1, toggleCount)
    }
    
    @Test
    fun `CompactPrivacyModeToggle shows correct description for enabled state`() {
        composeTestRule.setContent {
            MaterialTheme {
                CompactPrivacyModeToggle(
                    isEnabled = true,
                    onToggle = { }
                )
            }
        }
        
        composeTestRule
            .onNodeWithContentDescription("Disable privacy mode")
            .assertExists()
    }
    
    @Test
    fun `PrivacyModeIndicator renders correctly for disabled state`() {
        composeTestRule.setContent {
            MaterialTheme {
                PrivacyModeIndicator(isEnabled = false)
            }
        }
        
        composeTestRule
            .onNodeWithContentDescription("Privacy mode is disabled")
            .assertExists()
    }
    
    @Test
    fun `PrivacyModeIndicator renders correctly for enabled state`() {
        composeTestRule.setContent {
            MaterialTheme {
                PrivacyModeIndicator(isEnabled = true)
            }
        }
        
        composeTestRule
            .onNodeWithContentDescription("Privacy mode is enabled")
            .assertExists()
    }
    
    @Test
    fun `Interactive toggle state management works correctly`() {
        var isPrivacyEnabled by mutableStateOf(false)
        
        composeTestRule.setContent {
            MaterialTheme {
                PrivacyModeToggle(
                    isEnabled = isPrivacyEnabled,
                    onToggle = { isPrivacyEnabled = !isPrivacyEnabled }
                )
            }
        }
        
        // Initial state - disabled
        assertFalse(isPrivacyEnabled)
        composeTestRule
            .onNodeWithContentDescription("Privacy mode is disabled. Tap to enable.")
            .assertExists()
        
        // Click to enable
        composeTestRule
            .onNodeWithContentDescription("Privacy mode is disabled. Tap to enable.")
            .performClick()
        
        // State should change
        assertTrue(isPrivacyEnabled)
        composeTestRule
            .onNodeWithContentDescription("Privacy mode is enabled. Tap to disable.")
            .assertExists()
        
        // Click to disable
        composeTestRule
            .onNodeWithContentDescription("Privacy mode is enabled. Tap to disable.")
            .performClick()
        
        // State should change back
        assertFalse(isPrivacyEnabled)
        composeTestRule
            .onNodeWithContentDescription("Privacy mode is disabled. Tap to enable.")
            .assertExists()
    }
}