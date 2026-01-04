package com.mangala.wallet.features.addressbook.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthBox
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.mangalaColors

/**
 * Skeleton loading for ContactDetailScreen
 */
@Composable
fun ContactDetailSkeleton(
    modifier: Modifier = Modifier
) {
    MaxSizeColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimensions.Padding.default)
    ) {
        // Contact Header Skeleton
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimensions.Padding.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar skeleton
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .mangalaWalletPlaceholder(
                        visible = true,
                        shape = CircleShape,
                        color = MaterialTheme.mangalaColors.skeletonBase,
                        highlightColor = MaterialTheme.mangalaColors.skeletonShimmer
                    )
            )
            
            Spacer(modifier = Modifier.height(Spacing.SMALL))
            
            // Name skeleton
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(24.dp)
                    .mangalaWalletPlaceholder(
                        visible = true,
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.mangalaColors.skeletonBase,
                        highlightColor = MaterialTheme.mangalaColors.skeletonShimmer
                    )
            )
            
            Spacer(modifier = Modifier.height(Spacing.TINY))
            
            // Tags skeleton
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.TINY)
            ) {
                repeat(2) {
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(24.dp)
                            .mangalaWalletPlaceholder(
                                visible = true,
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.mangalaColors.skeletonBase,
                                highlightColor = MaterialTheme.mangalaColors.skeletonShimmer
                            )
                    )
                }
            }
        }
        
        // Quick Actions Skeleton
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CornerRadius.Small),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.mangalaColors.bgInnerCard
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.Padding.default),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(4) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .mangalaWalletPlaceholder(
                                    visible = true,
                                    shape = CircleShape,
                                    color = MaterialTheme.mangalaColors.skeletonBase,
                                    highlightColor = MaterialTheme.mangalaColors.skeletonShimmer
                                )
                        )
                        Spacer(modifier = Modifier.height(Spacing.XTINY))
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(12.dp)
                                .mangalaWalletPlaceholder(
                                    visible = true,
                                    shape = RoundedCornerShape(2.dp),
                                    color = MaterialTheme.mangalaColors.skeletonBase,
                                    highlightColor = MaterialTheme.mangalaColors.skeletonShimmer
                                )
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.BASE))
        
        // Wallet Addresses Section Skeleton
        ContactSectionSkeleton(
            title = "Wallet Addresses",
            itemCount = 2
        )
        
        Spacer(modifier = Modifier.height(Spacing.BASE))
        
        // Contact Info Sections Skeleton
        ContactSectionSkeleton(
            title = "Contact Information",
            itemCount = 3
        )
    }
}

/**
 * Skeleton loading for a contact section
 */
@Composable
private fun ContactSectionSkeleton(
    title: String,
    itemCount: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Section title
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(16.dp)
                .mangalaWalletPlaceholder(
                    visible = true,
                    shape = RoundedCornerShape(2.dp),
                    color = MaterialTheme.mangalaColors.skeletonBase,
                    highlightColor = MaterialTheme.mangalaColors.skeletonShimmer
                )
        )
        
        Spacer(modifier = Modifier.height(Spacing.XSMALL))
        
        // Section content
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CornerRadius.Small),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.mangalaColors.bgInnerCard
            )
        ) {
            Column(
                modifier = Modifier.padding(Dimensions.Padding.default)
            ) {
                repeat(itemCount) { index ->
                    if (index > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .padding(vertical = Spacing.XSMALL)
                                .background(MaterialTheme.mangalaColors.bgButton)
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            // Label
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(12.dp)
                                    .mangalaWalletPlaceholder(
                                        visible = true,
                                        shape = RoundedCornerShape(2.dp),
                                        color = MaterialTheme.mangalaColors.skeletonBase,
                                        highlightColor = MaterialTheme.mangalaColors.skeletonShimmer
                                    )
                            )
                            Spacer(modifier = Modifier.height(Spacing.XTINY))
                            // Value
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(16.dp)
                                    .mangalaWalletPlaceholder(
                                        visible = true,
                                        shape = RoundedCornerShape(2.dp),
                                        color = MaterialTheme.mangalaColors.skeletonBase,
                                        highlightColor = MaterialTheme.mangalaColors.skeletonShimmer
                                    )
                            )
                        }
                        
                        // Copy button skeleton
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .mangalaWalletPlaceholder(
                                    visible = true,
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.mangalaColors.skeletonBase,
                                    highlightColor = MaterialTheme.mangalaColors.skeletonShimmer
                                )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Skeleton loading for ContactList items
 */
@Composable
fun ContactListItemSkeleton(
    modifier: Modifier = Modifier
) {
    MaxWidthRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = Dimensions.Padding.default,
                vertical = Dimensions.Padding.small
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar skeleton
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .mangalaWalletPlaceholder(
                    visible = true,
                    shape = CircleShape,
                    color = MaterialTheme.mangalaColors.skeletonBase,
                    highlightColor = MaterialTheme.mangalaColors.skeletonShimmer
                )
        )
        
        Spacer(modifier = Modifier.width(Spacing.SMALL))
        
        // Contact info skeleton
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Name
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(16.dp)
                    .mangalaWalletPlaceholder(
                        visible = true,
                        shape = RoundedCornerShape(2.dp),
                        color = MaterialTheme.mangalaColors.skeletonBase,
                        highlightColor = MaterialTheme.mangalaColors.skeletonShimmer
                    )
            )
            
            Spacer(modifier = Modifier.height(Spacing.XTINY))
            
            // Address
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(14.dp)
                    .mangalaWalletPlaceholder(
                        visible = true,
                        shape = RoundedCornerShape(2.dp),
                        color = MaterialTheme.mangalaColors.skeletonBase,
                        highlightColor = MaterialTheme.mangalaColors.skeletonShimmer
                    )
            )
        }
        
        // Action button skeleton
        Box(
            modifier = Modifier
                .size(24.dp)
                .mangalaWalletPlaceholder(
                    visible = true,
                    shape = CircleShape,
                    color = MaterialTheme.mangalaColors.skeletonBase,
                    highlightColor = MaterialTheme.mangalaColors.skeletonShimmer
                )
        )
    }
}

/**
 * Skeleton loading for ContactForm (Edit/New)
 */
@Composable
fun ContactFormSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimensions.Padding.default)
    ) {
        // Avatar section skeleton
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .align(Alignment.CenterHorizontally)
                .mangalaWalletPlaceholder(
                    visible = true,
                    shape = CircleShape,
                    color = MaterialTheme.mangalaColors.skeletonBase,
                    highlightColor = MaterialTheme.mangalaColors.skeletonShimmer
                )
        )
        
        Spacer(modifier = Modifier.height(Spacing.BASE))
        
        // Name field skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .mangalaWalletPlaceholder(
                    visible = true,
                    shape = RoundedCornerShape(CornerRadius.Small),
                    color = MaterialTheme.mangalaColors.skeletonBase,
                    highlightColor = MaterialTheme.mangalaColors.skeletonShimmer
                )
        )
        
        Spacer(modifier = Modifier.height(Spacing.BASE))
        
        // Wallet addresses section skeleton
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CornerRadius.Small),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.mangalaColors.bgInnerCard
            )
        ) {
            Column(
                modifier = Modifier.padding(Dimensions.Padding.default)
            ) {
                repeat(2) { index ->
                    if (index > 0) {
                        Spacer(modifier = Modifier.height(Spacing.SMALL))
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.XSMALL)
                    ) {
                        // Network selector skeleton
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(48.dp)
                                .mangalaWalletPlaceholder(
                                    visible = true,
                                    shape = RoundedCornerShape(CornerRadius.Small),
                                    color = MaterialTheme.mangalaColors.skeletonBase,
                                    highlightColor = MaterialTheme.mangalaColors.skeletonShimmer
                                )
                        )
                        
                        // Address field skeleton
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .mangalaWalletPlaceholder(
                                    visible = true,
                                    shape = RoundedCornerShape(CornerRadius.Small),
                                    color = MaterialTheme.mangalaColors.skeletonBase,
                                    highlightColor = MaterialTheme.mangalaColors.skeletonShimmer
                                )
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.BASE))
        
        // Tags section skeleton
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CornerRadius.Small),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.mangalaColors.bgInnerCard
            )
        ) {
            Column(
                modifier = Modifier.padding(Dimensions.Padding.default)
            ) {
                // Title skeleton
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(16.dp)
                        .mangalaWalletPlaceholder(
                            visible = true,
                            shape = RoundedCornerShape(2.dp),
                            color = MaterialTheme.mangalaColors.skeletonBase,
                            highlightColor = MaterialTheme.mangalaColors.skeletonShimmer
                        )
                )
                
                Spacer(modifier = Modifier.height(Spacing.XSMALL))
                
                // Tag chips skeleton
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.XSMALL)
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .width(70.dp)
                                .height(32.dp)
                                .mangalaWalletPlaceholder(
                                    visible = true,
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.mangalaColors.skeletonBase,
                                    highlightColor = MaterialTheme.mangalaColors.skeletonShimmer
                                )
                        )
                    }
                }
            }
        }
    }
}