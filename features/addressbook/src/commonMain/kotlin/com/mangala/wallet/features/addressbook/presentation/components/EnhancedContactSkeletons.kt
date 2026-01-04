package com.mangala.wallet.features.addressbook.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.mangalaColors
import kotlinx.coroutines.delay
import kotlin.math.*

/**
 * Enhanced skeleton with pulse effect
 */
@Composable
fun Modifier.enhancedSkeleton(
    visible: Boolean,
    shape: Shape = RoundedCornerShape(4.dp),
    color: Color = MaterialTheme.mangalaColors.skeletonBase,
    highlightColor: Color = MaterialTheme.mangalaColors.skeletonShimmer,
    enablePulse: Boolean = true
): Modifier {
    return if (visible) {
        val infiniteTransition = rememberInfiniteTransition()
        
        val alphaValue = if (enablePulse) {
            infiniteTransition.animateFloat(
                initialValue = 0.7f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            ).value
        } else {
            1f
        }
        
        this
            .alpha(alphaValue)
            .mangalaWalletPlaceholder(
                visible = true,
                shape = shape,
                color = color,
                highlightColor = highlightColor
            )
    } else {
        this
    }
}

/**
 * Loading dots animation component
 */
@Composable
fun LoadingDotsAnimation(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.mangalaColors.textPrimary,
    dotSize: Float = 8f,
    spacing: Float = 4f
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    val dot1Alpha = infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    val dot2Alpha = infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    val dot3Alpha = infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Canvas(
        modifier = modifier.size(width = (dotSize * 3 + spacing * 2).dp, height = dotSize.dp)
    ) {
        val centerY = size.height / 2
        
        // Dot 1
        drawCircle(
            color = color.copy(alpha = dot1Alpha.value),
            radius = dotSize / 2,
            center = Offset(dotSize / 2, centerY)
        )
        
        // Dot 2
        drawCircle(
            color = color.copy(alpha = dot2Alpha.value),
            radius = dotSize / 2,
            center = Offset(dotSize * 1.5f + spacing, centerY)
        )
        
        // Dot 3
        drawCircle(
            color = color.copy(alpha = dot3Alpha.value),
            radius = dotSize / 2,
            center = Offset(dotSize * 2.5f + spacing * 2, centerY)
        )
    }
}

/**
 * Wave loading animation
 */
@Composable
fun WaveLoadingAnimation(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.mangalaColors.textPrimary.copy(alpha = 0.1f),
    waveHeight: Float = 20f,
    waveWidth: Float = 200f
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    val waveShift = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        )
    )
    
    Canvas(modifier = modifier.fillMaxWidth().height(waveHeight.dp)) {
        val path = Path()
        val amplitude = size.height / 2
        val frequency = 2 * PI / waveWidth
        
        path.moveTo(0f, size.height / 2)
        
        for (x in 0..size.width.toInt()) {
            val y = amplitude * sin(frequency * x + waveShift.value * PI / 180) + size.height / 2
            path.lineTo(x.toFloat(), y.toFloat())
        }
        
        path.lineTo(size.width, size.height)
        path.lineTo(0f, size.height)
        path.close()
        
        drawPath(
            path = path,
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Fill
        )
    }
}

/**
 * Circular progress skeleton for avatars
 */
@Composable
fun CircularProgressSkeleton(
    modifier: Modifier = Modifier,
    size: Int = 96,
    strokeWidth: Float = 4f,
    color: Color = MaterialTheme.mangalaColors.textPrimary.copy(alpha = 0.3f)
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    val rotation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing)
        )
    )
    
    Box(
        modifier = modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background skeleton
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .enhancedSkeleton(visible = true, shape = CircleShape)
        )
        
        // Rotating progress
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sweepAngle = 90f
            
            rotate(rotation.value) {
                drawArc(
                    color = color,
                    startAngle = 0f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }
}

/**
 * Enhanced Contact Detail Skeleton with meaningful animations
 */
@Composable
fun EnhancedContactDetailSkeleton(
    modifier: Modifier = Modifier
) {
    var showContent by remember { mutableStateOf(false) }
    
    // Staggered animation effect
    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }
    
    MaxSizeColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimensions.Padding.default)
    ) {
        // Contact Header with circular progress
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimensions.Padding.large)
                .alpha(if (showContent) 1f else 0f)
                ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar with circular progress
            CircularProgressSkeleton(size = 96)
            
            Spacer(modifier = Modifier.height(Spacing.SMALL))
            
            // Name skeleton with pulse
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(24.dp)
                    .enhancedSkeleton(
                        visible = true,
                        shape = RoundedCornerShape(4.dp),
                        enablePulse = true
                    )
            )
            
            Spacer(modifier = Modifier.height(Spacing.TINY))
            
            // Loading dots for tags
            LoadingDotsAnimation(
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        
        // Wave animation separator
        WaveLoadingAnimation(
            modifier = Modifier.padding(vertical = Spacing.SMALL)
        )
        
        // Quick Actions with staggered appearance
        AnimatedQuickActionsSkeleton()
        
        Spacer(modifier = Modifier.height(Spacing.BASE))
        
        // Sections with enhanced animations
        EnhancedContactSectionSkeleton(
            title = "Wallet Addresses",
            itemCount = 2,
            showDataFlow = true
        )
        
        Spacer(modifier = Modifier.height(Spacing.BASE))
        
        EnhancedContactSectionSkeleton(
            title = "Contact Information",
            itemCount = 3,
            showDataFlow = false
        )
    }
}

/**
 * Animated quick actions skeleton
 */
@Composable
private fun AnimatedQuickActionsSkeleton() {
    val infiniteTransition = rememberInfiniteTransition()
    
    val scale = infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
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
            repeat(4) { index ->
                val delay = index * 100
                var visible by remember { mutableStateOf(false) }
                
                LaunchedEffect(Unit) {
                    delay(delay.toLong())
                    visible = true
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .alpha(if (visible) 1f else 0f)
                        .scale(if (index == 1) scale.value else 1f) // Animate second button
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .enhancedSkeleton(
                                visible = true,
                                shape = CircleShape,
                                enablePulse = index == 1 // Pulse on send button
                            )
                    )
                    Spacer(modifier = Modifier.height(Spacing.XTINY))
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(12.dp)
                            .enhancedSkeleton(
                                visible = true,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
        }
    }
}

/**
 * Enhanced section skeleton with data flow animation
 */
@Composable
private fun EnhancedContactSectionSkeleton(
    title: String,
    itemCount: Int,
    showDataFlow: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Section title with typing animation
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.XSMALL)
        ) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(16.dp)
                    .enhancedSkeleton(
                        visible = true,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            
            if (showDataFlow) {
                LoadingDotsAnimation(
                    modifier = Modifier.padding(start = Spacing.XSMALL),
                    dotSize = 4f,
                    color = MaterialTheme.mangalaColors.textPrimary.copy(alpha = 0.5f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.XSMALL))
        
        // Section content with data flow visualization
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
                    
                    EnhancedSectionItemSkeleton(
                        showDataFlow = showDataFlow && index == 0,
                        animationDelay = index * 150
                    )
                }
            }
        }
    }
}

/**
 * Enhanced section item with data flow visualization
 */
@Composable
private fun EnhancedSectionItemSkeleton(
    showDataFlow: Boolean,
    animationDelay: Int
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(animationDelay.toLong())
        visible = true
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (visible) 1f else 0f)
            .animateContentSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            // Label
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(12.dp)
                    .enhancedSkeleton(
                        visible = true,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            Spacer(modifier = Modifier.height(Spacing.XTINY))
            
            // Value with data flow effect
            if (showDataFlow) {
                DataFlowSkeleton(modifier = Modifier.fillMaxWidth(0.8f))
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(16.dp)
                        .enhancedSkeleton(
                            visible = true,
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }
        }
        
        // Copy button skeleton
        Box(
            modifier = Modifier
                .size(24.dp)
                .enhancedSkeleton(
                    visible = true,
                    shape = RoundedCornerShape(4.dp),
                    enablePulse = showDataFlow
                )
        )
    }
}

/**
 * Data flow skeleton animation
 */
@Composable
private fun DataFlowSkeleton(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    val progress = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        )
    )
    
    Box(
        modifier = modifier.height(16.dp)
    ) {
        // Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.mangalaColors.skeletonBase)
        )
        
        // Animated data flow
        val shimmerColors = listOf(
            Color.Transparent,
            MaterialTheme.mangalaColors.skeletonShimmer.copy(alpha = 0.3f),
            MaterialTheme.mangalaColors.skeletonShimmer.copy(alpha = 0.5f),
            MaterialTheme.mangalaColors.skeletonShimmer.copy(alpha = 0.3f),
            Color.Transparent
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(2.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = shimmerColors,
                        startX = -1000f + 2000f * progress.value,
                        endX = -500f + 2000f * progress.value
                    )
                )
        )
    }
}

/**
 * Enhanced contact list item skeleton
 */
@Composable
fun EnhancedContactListItemSkeleton(
    modifier: Modifier = Modifier,
    animationDelay: Int = 0
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(animationDelay.toLong())
        visible = true
    }
    
    MaxWidthRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = Dimensions.Padding.default,
                vertical = Dimensions.Padding.small
            )
            .alpha(if (visible) 1f else 0f)
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with subtle animation
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .enhancedSkeleton(
                    visible = true,
                    shape = CircleShape,
                    enablePulse = true
                )
        )
        
        Spacer(modifier = Modifier.width(Spacing.SMALL))
        
        // Contact info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Name
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(16.dp)
                    .enhancedSkeleton(
                        visible = true,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            
            Spacer(modifier = Modifier.height(Spacing.XTINY))
            
            // Address with typing effect
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.XTINY)
            ) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(14.dp)
                        .enhancedSkeleton(
                            visible = true,
                            shape = RoundedCornerShape(2.dp)
                        )
                )
                LoadingDotsAnimation(
                    dotSize = 3f,
                    color = MaterialTheme.mangalaColors.textSecondary.copy(alpha = 0.5f)
                )
            }
        }
        
        // Action button
        Box(
            modifier = Modifier
                .size(24.dp)
                .enhancedSkeleton(
                    visible = true,
                    shape = CircleShape
                )
        )
    }
}

/**
 * Enhanced skeleton loading for ContactForm (Edit/New)
 */
@Composable
fun EnhancedContactFormSkeleton(
    modifier: Modifier = Modifier
) {
    var revealProgress by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        for (i in 0..4) {
            delay(200)
            revealProgress = i
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimensions.Padding.default)
    ) {
        // Avatar section skeleton with scale animation
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .align(Alignment.CenterHorizontally)
                .alpha(if (revealProgress >= 0) 1f else 0f)
                .enhancedSkeleton(
                    visible = true,
                    shape = CircleShape,
                    enablePulse = true
                )
        )
        
        Spacer(modifier = Modifier.height(Spacing.BASE))
        
        // Name field skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .alpha(if (revealProgress >= 1) 1f else 0f)
                .enhancedSkeleton(
                    visible = true,
                    shape = RoundedCornerShape(CornerRadius.Small)
                )
        )
        
        Spacer(modifier = Modifier.height(Spacing.BASE))
        
        // Wallet addresses section skeleton with staggered fields
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (revealProgress >= 2) 1f else 0f),
            shape = RoundedCornerShape(CornerRadius.Small),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.mangalaColors.bgInnerCard
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(Dimensions.Padding.default)
            ) {
                repeat(2) { index ->
                    if (index > 0) {
                        Spacer(modifier = Modifier.height(Spacing.SMALL))
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(if (revealProgress >= 2 + index) 1f else 0f),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.XSMALL)
                    ) {
                        // Network selector skeleton
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(48.dp)
                                .enhancedSkeleton(
                                    visible = true,
                                    shape = RoundedCornerShape(CornerRadius.Small),
                                    color = MaterialTheme.mangalaColors.skeletonBase,
                                    highlightColor = MaterialTheme.mangalaColors.skeletonShimmer.copy(alpha = 0.5f)
                                )
                        )
                        
                        // Address field skeleton
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .enhancedSkeleton(
                                    visible = true,
                                    shape = RoundedCornerShape(CornerRadius.Small)
                                )
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.BASE))
        
        // Tags section skeleton with animated chips
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (revealProgress >= 4) 1f else 0f),
            shape = RoundedCornerShape(CornerRadius.Small),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.mangalaColors.bgInnerCard
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(Dimensions.Padding.default)
            ) {
                // Title skeleton
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(16.dp)
                        .enhancedSkeleton(
                            visible = true,
                            shape = RoundedCornerShape(2.dp)
                        )
                )
                
                Spacer(modifier = Modifier.height(Spacing.XSMALL))
                
                // Tag chips skeleton with staggered appearance
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.XSMALL)
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .width(70.dp)
                                .height(32.dp)
                                .alpha(if (revealProgress >= 4) 1f else 0f)
                                .enhancedSkeleton(
                                    visible = true,
                                    shape = RoundedCornerShape(16.dp),
                                    enablePulse = index == 0
                                )
                        )
                    }
                }
            }
        }
    }
}