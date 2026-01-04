package com.mangala.wallet.features.addressbook.presentation.contact.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.ui.component.KeyboardDismissBox
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeftNavigation
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.presentation.tag.QuickTagCreationScreenModel
import com.mangala.wallet.features.addressbook.presentation.tag.QuickTagEvent
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

/**
 * Bottom sheet for creating a new tag
 * This is the Screen version maintained for backward compatibility
 */
class CreateTagBottomSheet(
    private val onTagCreated: (TagEntity) -> Unit,
    private val onDismiss: () -> Unit = {} // Capture onDismiss parameter but make it optional
) : BaseScreen<QuickTagCreationScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.CREATE_TAG
    override val screenClassName: String = CreateTagBottomSheet::class.simpleName.orEmpty()
    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): QuickTagCreationScreenModel {
        return koinInject()
    }

    @Composable
    override fun ScreenContent(screenModel: QuickTagCreationScreenModel) {
        CreateTagBottomSheetContent(
            onTagCreated = onTagCreated,
            onDismiss = onDismiss,
            screenModel = screenModel
        )
    }
}

/**
 * Bottom sheet for creating a new tag implemented as a Composable with callback support
 * This version is optimized for integration with TagSelectionChips to avoid race conditions
 */
@Composable
fun CreateTagBottomSheetContentWithCallback(
    isLoading: Boolean,
    errorMessage: String?,
    onTagCreate: (String, String) -> Unit,
    onCancel: () -> Unit
) {
    var tagName by remember { mutableStateOf("") }
    
    // Color selection states - using indices instead of Color objects for KMP compatibility
    var selectedBackgroundColorIndex by remember { 
        mutableStateOf(ColorsNew.colorToIndex(ColorsNew.tagTealBase))
    }
    var selectedTextColorIndex by remember { 
        mutableStateOf(getTextColorIndexForBackground(selectedBackgroundColorIndex))
    }

    KeyboardDismissBox(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.ime) // Use WindowInsets directly
            .background(MaterialTheme.mangalaColors.bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
        // Title bar with back button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .padding(horizontal = 16.dp)
        ) {
            // Back button on the left
            IconButton(
                onClick = onCancel,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = MangalaWalletPack.ArrowLeftNavigation,
                    contentDescription = "Back",
                    tint = MaterialTheme.mangalaColors.iconPrimary
                )
            }
            
            // Title in center
            Text(
                text = "Create tag",
                style = MangalaTypography.Size14Medium(),
                color = MaterialTheme.mangalaColors.textPrimary,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Tag name input
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.mangalaColors.bgInnerCard)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            BasicTextField(
                value = tagName,
                onValueChange = { 
                    // Enforce max length of 20 characters
                    if (it.length <= 20) {
                        tagName = it
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.mangalaColors.textPrimary
                ),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.mangalaColors.textLink),
                enabled = !isLoading,
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (tagName.isEmpty()) {
                            Text(
                                text = "Enter tag name",
                                color = MaterialTheme.mangalaColors.textSecondary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
        
        // Character count if approaching limit
        if (tagName.length > 15) {
            Text(
                text = "${tagName.length}/20",
                color = if (tagName.length > 18) Color.Red else MaterialTheme.mangalaColors.textSecondary,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 4.dp),
                fontWeight = FontWeight.Normal
            )
        }
        
        // Error message if any
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Background color selection card
        BackgroundColorSelectionCard(
            selectedColorIndex = selectedBackgroundColorIndex,
            onColorSelected = { colorIndex -> 
                selectedBackgroundColorIndex = colorIndex
                // If text color wasn't manually selected, update it automatically
                selectedTextColorIndex = getTextColorIndexForBackground(colorIndex)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Text color selection card
        TextColorSelectionCard(
            selectedColorIndex = selectedTextColorIndex,
            onColorSelected = { colorIndex -> 
                selectedTextColorIndex = colorIndex
            }
        )

        Spacer(modifier = Modifier.height(60.dp))

        // Create button at bottom
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding() // Ensure button is above navigation bar
                .padding(
                    horizontal = Dimensions.Padding.default,
                    vertical = Dimensions.Padding.small
                ),
            color = MaterialTheme.mangalaColors.bg
        ) {
            MangalaGradientButton(
                label = "Create tag",
                onClick = {
                    if (tagName.isNotBlank()) {
                        // Store both colors as string indices instead of hex
                        val backgroundColorStr = selectedBackgroundColorIndex.toString()

                        onTagCreate(tagName.trim(), backgroundColorStr)
                    }
                },
                enabled = !isLoading && tagName.isNotBlank(),
                size = MangalaButtonSize.Medium,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
    }
}

/**
 * Bottom sheet for creating a new tag implemented as a Composable
 * This allows easier integration with different UI contexts
 */
@Composable
fun CreateTagBottomSheetContent(
    onTagCreated: (TagEntity) -> Unit,
    onDismiss: () -> Unit = {},
    screenModel: QuickTagCreationScreenModel = koinInject()
) {
    val state by screenModel.state.collectAsStateMultiplatform()
    
    var tagName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Color selection states - using indices instead of Color objects for KMP compatibility
    var selectedBackgroundColorIndex by remember { 
        mutableStateOf(getColorIndexFromString(state.selectedColor))
    }
    var selectedTextColorIndex by remember { 
        mutableStateOf(getTextColorIndexForBackground(selectedBackgroundColorIndex))
    }

    // Get actual Color objects for UI from indices
    val selectedBackgroundColor = ColorsNew.indexToColor(selectedBackgroundColorIndex)
    val selectedTextColor = ColorsNew.indexToColor(selectedTextColorIndex)

    // Update selected color indices when state changes
    LaunchedEffect(state.selectedColor) {
        selectedBackgroundColorIndex = getColorIndexFromString(state.selectedColor)
        // We don't update text color here because we want text color to be independently selectable
    }

    // Clean up resources when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            // Any cleanup needed when dismissing the bottom sheet
        }
    }

    // Collect events from the viewModel
    LaunchedEffect(screenModel) {
        screenModel.events.collectLatest { event ->
            when (event) {
                is QuickTagEvent.TagCreated -> {
                    // Tag created successfully, notify the caller and dismiss
                    onTagCreated(event.createdTag)
                    onDismiss() // Call onDismiss after successfully creating tag
                }
                is QuickTagEvent.Error -> {
                    errorMessage = event.message
                }
                is QuickTagEvent.ValidationError -> {
                    errorMessage = event.message
                }
                is QuickTagEvent.PremiumRequired -> {
                    errorMessage = event.message
                }
            }
        }
    }

    KeyboardDismissBox(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.ime) // Use WindowInsets directly
            .background(MaterialTheme.mangalaColors.bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
        // Title bar with back button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .padding(horizontal = 16.dp)
        ) {
            // Back button on the left
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = MangalaWalletPack.ArrowLeftNavigation,
                    contentDescription = "Back",
                    tint = MaterialTheme.mangalaColors.iconPrimary
                )
            }
            
            // Title in center
            Text(
                text = "Create tag",
                style = MangalaTypography.Size14Medium(),
                color = MaterialTheme.mangalaColors.textPrimary,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Tag name input
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.mangalaColors.bgInnerCard)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            BasicTextField(
                value = tagName,
                onValueChange = { 
                    // Enforce max length of 20 characters
                    if (it.length <= 20) {
                        tagName = it
                        errorMessage = null // Clear error when input changes
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.mangalaColors.textPrimary
                ),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.mangalaColors.textLink),
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (tagName.isEmpty()) {
                            Text(
                                text = "Enter tag name",
                                color = MaterialTheme.mangalaColors.textSecondary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
        
        // Character count if approaching limit
        if (tagName.length > 15) {
            Text(
                text = "${tagName.length}/20",
                color = if (tagName.length > 18) Color.Red else MaterialTheme.mangalaColors.textSecondary,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 4.dp),
                fontWeight = FontWeight.Normal
            )
        }
        
        // Error message if any
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
        
        // Show subscription limit information if relevant
        val showSubscriptionWarning = !state.isPremium && state.currentTagCount >= 15 // Show warning as approaching limit
        if (showSubscriptionWarning) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ColorsNew.warning_100
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Free users can create up to 20 tags. Upgrade to premium for unlimited tags.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorsNew.warning_800,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Background color selection card
        BackgroundColorSelectionCard(
            selectedColorIndex = selectedBackgroundColorIndex,
            onColorSelected = { colorIndex -> 
                selectedBackgroundColorIndex = colorIndex
                // If text color wasn't manually selected, update it automatically
                selectedTextColorIndex = getTextColorIndexForBackground(colorIndex)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Text color selection card
        TextColorSelectionCard(
            selectedColorIndex = selectedTextColorIndex,
            onColorSelected = { colorIndex -> 
                selectedTextColorIndex = colorIndex
            }
        )

        Spacer(modifier = Modifier.height(60.dp))

        // Create button at bottom
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            color = MaterialTheme.mangalaColors.bgInnerCard,
        ) {
            MangalaGradientButton(
                label = "Create tag",
                onClick = {
                    if (tagName.isBlank()) {
                        errorMessage = "Tag name cannot be empty"
                        return@MangalaGradientButton
                    }

                    // Store both colors as string indices instead of hex
                    val backgroundColorStr = selectedBackgroundColorIndex.toString()
                    val textColorStr = selectedTextColorIndex.toString()

                    // Pass both background and text colors to create tag
                    screenModel.createTag(
                        name = tagName.trim(),
                        color = backgroundColorStr,
                        textColor = textColorStr
                    )
                },
                enabled = !state.isCreating,
                size = MangalaButtonSize.Medium,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
    }
}

/**
 * Card for text color selection
 */
@Composable
private fun TextColorSelectionCard(
    selectedColorIndex: Int,
    onColorSelected: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.mangalaColors.bgInnerCard
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Choose text color",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.mangalaColors.textPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(getTextColorIndices()) { colorIndex ->
                    ColorButton(
                        colorIndex = colorIndex,
                        isSelected = selectedColorIndex == colorIndex,
                        onClick = { onColorSelected(colorIndex) }
                    )
                }
            }
        }
    }
}

/**
 * Card for background color selection
 */
@Composable
private fun BackgroundColorSelectionCard(
    selectedColorIndex: Int,
    onColorSelected: (Int) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.mangalaColors.bgInnerCard
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Choose background color",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.mangalaColors.textPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(getBackgroundColorIndices()) { colorIndex ->
                    ColorButton(
                        colorIndex = colorIndex,
                        isSelected = selectedColorIndex == colorIndex,
                        onClick = { onColorSelected(colorIndex) }
                    )
                }
            }
        }
    }
}

/**
 * Color selection button with selection indicator
 */
@Composable
private fun ColorButton(
    colorIndex: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = ColorsNew.indexToColor(colorIndex)
    
    Box(
        modifier = Modifier
            .size(32.dp) // Size according to Figma design
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.mangalaColors.iconSecondary else MaterialTheme.mangalaColors.border,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = if (color.luminance() > 0.5f) Color.Black else Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
    }


/**
 * Returns a list of color indices for text options from ColorsNew
 */
private fun getTextColorIndices(): List<Int> = listOf(
    ColorsNew.colorToIndex(ColorsNew.textWhite),          // White (91)
    ColorsNew.colorToIndex(ColorsNew.textBlack),          // Black (92)
    ColorsNew.colorToIndex(ColorsNew.textBlue),           // Blue (93)
    ColorsNew.colorToIndex(ColorsNew.textTeal),           // Teal (94)
    ColorsNew.colorToIndex(ColorsNew.textYellow),         // Yellow (95)
    ColorsNew.colorToIndex(ColorsNew.textRed),            // Red (96)
    ColorsNew.colorToIndex(ColorsNew.textPurple),         // Purple (97)
    ColorsNew.colorToIndex(ColorsNew.textIndigo)          // Indigo (98)
)

/**
 * Returns a list of color indices for background options from ColorsNew
 */
private fun getBackgroundColorIndices(): List<Int> = listOf(
    ColorsNew.colorToIndex(ColorsNew.tagTealBase),        // Teal base (99)
    ColorsNew.colorToIndex(ColorsNew.tagTealVar1),        // Teal variation 1 (100)
    ColorsNew.colorToIndex(ColorsNew.tagTealVar2),        // Teal variation 2 (101)
    ColorsNew.colorToIndex(ColorsNew.tagTealVar3),        // Teal variation 3 (102)
    ColorsNew.colorToIndex(ColorsNew.tagTealVar4),        // Teal variation 4 (103)
    ColorsNew.colorToIndex(ColorsNew.tagTealVar5),        // Teal variation 5 (104)
    ColorsNew.colorToIndex(ColorsNew.tagTealVar6),        // Teal variation 6 (105)
    ColorsNew.colorToIndex(ColorsNew.tagTealVar7),        // Teal variation 7 (106)
    ColorsNew.colorToIndex(ColorsNew.blueActionButton),   // Blue (76)
    ColorsNew.colorToIndex(ColorsNew.primary_500),        // Gray (9)
    ColorsNew.colorToIndex(ColorsNew.success_500),        // Green (20)
    ColorsNew.colorToIndex(ColorsNew.error_600),          // Red (43)
    ColorsNew.colorToIndex(ColorsNew.warning_500)         // Orange (31)
)

/**
 * Get color index from string - handles both index strings and hex colors
 */
private fun getColorIndexFromString(colorString: String): Int {
    return try {
        if (colorString.startsWith("#")) {
            // If it's a hex string, find the closest matching color in ColorsNew
            // This is best effort - in practice, we should have saved colors as indices
            // For demonstration, we'll default to teal
            ColorsNew.colorToIndex(ColorsNew.tagTealBase) // Use default index (99) for teal
        } else {
            // It should be an index - try to parse it
            colorString.toInt()
        }
    } catch (e: Exception) {
        // Default to teal if we can't parse
        ColorsNew.colorToIndex(ColorsNew.tagTealBase) // (99)
    }
}

/**
 * Get appropriate text color index based on background brightness
 */
private fun getTextColorIndexForBackground(backgroundColorIndex: Int): Int {
    val backgroundColor = ColorsNew.indexToColor(backgroundColorIndex)
    
    // Choose white or black text based on background brightness
    return if (backgroundColor.luminance() > 0.5f) {
        ColorsNew.colorToIndex(ColorsNew.textBlack) // (92)
    } else {
        ColorsNew.colorToIndex(ColorsNew.textWhite) // (91)
    }
}