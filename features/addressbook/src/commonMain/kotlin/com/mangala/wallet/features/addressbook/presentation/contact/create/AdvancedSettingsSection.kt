package com.mangala.wallet.features.addressbook.presentation.contact.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mangala.wallet.ui.theme.mangalaColors
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.border
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowRightNew
import com.mangala.wallet.features.addressbook.data.model.group.GroupEntity
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.data.model.enum.PrivacyLevel
import com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.features.addressbook.icon.contacticon.QuestionMark
import com.mangala.wallet.ui.theme.MangalaTypography

/**
 * Advanced settings section for contact creation screen
 * Includes privacy display mode, security level settings, and group selection
 */
@Composable
fun AdvancedSettingsSection(
    privacyDisplayMode: DisplayMode,
    securityLevel: SecurityLevel,
    selectedGroupIds: List<String>,
    availableGroups: List<GroupEntity>,
    onPrivacyDisplayModeChange: (DisplayMode) -> Unit,
    onSecurityLevelChange: (SecurityLevel) -> Unit,
) {
    var expanded by remember { mutableStateOf(true) }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        color = MaterialTheme.mangalaColors.bgInnerCard,
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Privacy & Security",
                    style = MangalaTypography.Size14SemiBold(),
                    color = MaterialTheme.mangalaColors.textPrimary
                )

                Icon(
                    imageVector = MangalaWalletPack.ArrowRightNew,
                    contentDescription = null,
                    tint = MaterialTheme.mangalaColors.iconSecondary,
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Privacy level section
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Privacy level",
                                style = MangalaTypography.Size14Medium(),
                                color = MaterialTheme.mangalaColors.textPrimary
                            )

                            Spacer(modifier = Modifier.width(Spacing.XTINY))

                            Icon(
                                imageVector = ContactIcon.QuestionMark,
                                contentDescription = "Privacy level info",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.mangalaColors.textOnBadge
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SelectionChip(
                                text = "Public",
                                selected = privacyDisplayMode == DisplayMode.FULL,
                                onClick = { onPrivacyDisplayModeChange(DisplayMode.FULL) }
                            )

                            SelectionChip(
                                text = "Private",
                                selected = privacyDisplayMode == DisplayMode.HIDDEN,
                                onClick = { onPrivacyDisplayModeChange(DisplayMode.HIDDEN) }
                            )

                            SelectionChip(
                                text = "Secret",
                                selected = privacyDisplayMode == DisplayMode.SECRET,
                                onClick = { onPrivacyDisplayModeChange(DisplayMode.SECRET) }
                            )
                        }
                    }

                    // Security Level section
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Security Level",
                                style = MangalaTypography.Size14Medium(),
                                color = MaterialTheme.mangalaColors.textPrimary
                            )
                            Icon(
                                imageVector = ContactIcon.QuestionMark,
                                contentDescription = "Security level info",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.mangalaColors.textOnBadge
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SelectionChip(
                                text = "Normal",
                                selected = securityLevel == SecurityLevel.NORMAL,
                                onClick = { onSecurityLevelChange(SecurityLevel.NORMAL) }
                            )

                            SelectionChip(
                                text = "High",
                                selected = securityLevel == SecurityLevel.HIGH,
                                onClick = { onSecurityLevelChange(SecurityLevel.HIGH) }
                            )

                            SelectionChip(
                                text = "Maximum",
                                selected = securityLevel == SecurityLevel.MAXIMUM,
                                onClick = { onSecurityLevelChange(SecurityLevel.MAXIMUM) }
                            )
                        }
                    }

                }
            }
        }
    }
}

@Composable
private fun SelectionChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = if (selected) MaterialTheme.mangalaColors.bgTagLight else MaterialTheme.mangalaColors.bgInnerCard,
        border = if (!selected) BorderStroke(1.dp, MaterialTheme.mangalaColors.textSecondary) else null,
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicText(
                text = text,
                style = MangalaTypography.Size14Medium().copy(
                    color = if (selected) MaterialTheme.mangalaColors.textTag else MaterialTheme.mangalaColors.textSecondary
                ),
            )
        }
    }
}
