package com.mangala.wallet.features.addressbook.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Close

/**
 * Privacy Mode Indicator Badge
 *
 * A visual indicator that shows when privacy mode is enabled.
 * Displays as a small badge with lock icon and "Privacy Mode" text.
 */
@Composable
fun PrivacyModeIndicator(
    isPrivacyModeEnabled: Boolean,
    modifier: Modifier = Modifier,
    showText: Boolean = true,
    size: PrivacyIndicatorSize = PrivacyIndicatorSize.MEDIUM,
) {
    AnimatedVisibility(
        visible = isPrivacyModeEnabled,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier,
            shape = RoundedCornerShape(size.cornerRadius),
            color = ColorsNew.primary_100,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = ColorsNew.primary_300
            )
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = size.horizontalPadding,
                    vertical = size.verticalPadding
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(size.iconTextSpacing)
            ) {
                Icon(
                    imageVector = Icons.Outlined.VisibilityOff,
                    contentDescription = "Privacy Mode Enabled",
                    modifier = Modifier.size(size.iconSize),
                    tint = ColorsNew.primary_600
                )

                if (showText) {
                    Text(
                        text = "Privacy Mode",
                        fontSize = size.textSize,
                        fontWeight = FontWeight.Medium,
                        color = ColorsNew.primary_600
                    )
                }
            }
        }
    }
}

/**
 * Compact Privacy Mode Badge
 *
 * A smaller version of the privacy indicator, suitable for tight spaces.
 */
@Composable
fun PrivacyModeBadge(
    isPrivacyModeEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = isPrivacyModeEnabled,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(
                    color = ColorsNew.primary_500,
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.VisibilityOff,
                contentDescription = "Privacy Mode Active",
                modifier = Modifier.size(12.dp),
                tint = Color.White
            )
        }
    }
}

/**
 * Privacy Status Banner
 *
 * A full-width banner that shows privacy mode status with additional info.
 */
@Composable
fun PrivacyStatusBanner(
    isPrivacyModeEnabled: Boolean,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
) {
    AnimatedVisibility(
        visible = isPrivacyModeEnabled,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = ColorsNew.primary_50,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = ColorsNew.primary_200
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.VisibilityOff,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = ColorsNew.primary_600
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Privacy Mode Active",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorsNew.primary_900
                    )
                    Text(
                        text = "Sensitive addresses are hidden. Tap addresses to reveal.",
                        fontSize = 12.sp,
                        color = ColorsNew.primary_600
                    )
                }

                if (onDismiss != null) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = MangalaWalletPack.Close,
                            contentDescription = "Dismiss",
                            modifier = Modifier.size(16.dp),
                            tint = ColorsNew.primary_600
                        )
                    }
                }
            }
        }
    }
}

/**
 * Privacy Mode Floating Indicator
 *
 * A small floating indicator that can be positioned anywhere on screen.
 */
@Composable
fun PrivacyModeFloatingIndicator(
    isPrivacyModeEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = isPrivacyModeEnabled,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier,
            shape = RoundedCornerShape(16.dp),
            color = ColorsNew.primary_600,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.VisibilityOff,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = Color.White
                )

                Text(
                    text = "PRIVATE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Size configurations for Privacy Mode Indicator
 */
enum class PrivacyIndicatorSize(
    val iconSize: androidx.compose.ui.unit.Dp,
    val textSize: androidx.compose.ui.unit.TextUnit,
    val horizontalPadding: androidx.compose.ui.unit.Dp,
    val verticalPadding: androidx.compose.ui.unit.Dp,
    val cornerRadius: androidx.compose.ui.unit.Dp,
    val iconTextSpacing: androidx.compose.ui.unit.Dp,
) {
    SMALL(
        iconSize = 12.dp,
        textSize = 10.sp,
        horizontalPadding = 6.dp,
        verticalPadding = 3.dp,
        cornerRadius = 6.dp,
        iconTextSpacing = 3.dp
    ),
    MEDIUM(
        iconSize = 14.dp,
        textSize = 12.sp,
        horizontalPadding = 8.dp,
        verticalPadding = 4.dp,
        cornerRadius = 8.dp,
        iconTextSpacing = 4.dp
    ),
    LARGE(
        iconSize = 16.dp,
        textSize = 14.sp,
        horizontalPadding = 12.dp,
        verticalPadding = 6.dp,
        cornerRadius = 10.dp,
        iconTextSpacing = 6.dp
    )
}