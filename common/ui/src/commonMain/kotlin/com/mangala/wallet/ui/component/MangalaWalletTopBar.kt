package com.mangala.wallet.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Category
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcBack
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.TextTopBar
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.MangalaTypography
import dev.icerock.moko.resources.compose.fontFamilyResource
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun MangalaWalletTopBarCenteredTitle(
    title: String,
    textColor: Color = MaterialTheme.mangalaColors.textPrimary,
    backIconTint: Color = MaterialTheme.mangalaColors.iconPrimary,
    textStyle: TextStyle = MangalaTypography.Size17Medium(),
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit,
    trailingButton: (@Composable () -> Unit)? = null,
) {
    MangalaWalletTopBarCenteredTitle(
        title = title,
        textColor = textColor,
        textStyle = textStyle,
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = onBackClicked
            ) {
                Icon(
                    imageVector = MangalaWalletPack.IcBack,
                    contentDescription = "Back",
                    tint = backIconTint,
                )
            }
        },
        trailingButton = trailingButton
    )
}

@Composable
fun MangalaWalletTopBarCenteredTitle(
    title: String,
    textColor: Color = MaterialTheme.mangalaColors.textPrimary,
    textStyle: TextStyle = MangalaTypography.Size17Medium(),
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    trailingButton: (@Composable () -> Unit)? = null,
) {
    Layout(
        content = {
            // Navigation Icon Slot
            if (navigationIcon != null) {
                Box(Modifier.layoutId("navigationIcon")) {
                    navigationIcon()
                }
            }

            // Title Slot - Text composable is created here
            Box(Modifier.layoutId("title")) {
                Text(
                    text = title,
                    color = textColor,
                    style = textStyle,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Actions Slot
            if (trailingButton != null) {
                Box(Modifier.layoutId("actionIcons")) {
                    trailingButton()
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) { measurables, constraints ->
        // --- Measure Phase ---
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val internalSpacingPx = Spacing.TINY.toPx()

        val navigationIconPlaceable =
            measurables.find { it.layoutId == "navigationIcon" }?.measure(looseConstraints)
        val actionIconsPlaceable =
            measurables.find { it.layoutId == "actionIcons" }?.measure(looseConstraints)

        val navIconWidth = navigationIconPlaceable?.width ?: 0
        val actionsWidth = actionIconsPlaceable?.width ?: 0

        // Calculate the maximum space (width of the larger side button + spacing) to reserve on both sides
        val maxSideSpace = maxOf(
            navIconWidth + if (navIconWidth > 0) internalSpacingPx.toInt() else 0,
            actionsWidth + if (actionsWidth > 0) internalSpacingPx.toInt() else 0
        )

        // Calculate available width for title, accounting for symmetric spacing
        val titleMaxWidth = (constraints.maxWidth - 2 * maxSideSpace).coerceAtLeast(0)

        val titlePlaceable = measurables.find { it.layoutId == "title" }!!.measure(
            constraints.copy(minWidth = 0, maxWidth = titleMaxWidth)
        )

        // Determine the height of the layout by the tallest element
        val navIconHeight = navigationIconPlaceable?.height ?: 0
        val titleActualHeight = titlePlaceable.height
        val actionsHeight = actionIconsPlaceable?.height ?: 0
        val layoutHeight = maxOf(navIconHeight, titleActualHeight, actionsHeight)

        // --- Placement Phase ---
        layout(constraints.maxWidth, layoutHeight) {
            // Place Navigation Icon at the start
            navigationIconPlaceable?.let {
                it.placeRelative(
                    x = 0,
                    y = (layoutHeight - it.height) / 2
                )
            }

            // Center title relative to the entire screen width
            titlePlaceable.placeRelative(
                x = (constraints.maxWidth - titlePlaceable.width) / 2,
                y = (layoutHeight - titlePlaceable.height) / 2
            )

            // Place Action Icons at the end
            actionIconsPlaceable?.let {
                it.placeRelative(
                    x = constraints.maxWidth - it.width,
                    y = (layoutHeight - it.height) / 2
                )
            }
        }
    }
}

@Composable
fun MangalaWalletTopBar(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    text: String,
    color: Color = MaterialTheme.mangalaColors.textPrimary,
    fontSize: TextUnit = FontType.REGULAR,
    fontWeight: FontWeight? = null,
    trailingButton: @Composable () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
        modifier = Modifier.then(modifier).fillMaxWidth().padding(end = Spacing.SMALL),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // We don't need padding between these two items because IconButton has default padding to ensure clickability
            navigationIcon()
            TextTopBar(
                text = text,
                fontSize = fontSize,
                fontWeight = fontWeight,
                color = color
            )
        }
        trailingButton()
    }
}


@Composable
fun MangalaWalletTopBar(
    modifier: Modifier = Modifier,
    text: String,
    onBackClicked: () -> Unit,
    color: Color = MaterialTheme.mangalaColors.textPrimary,
    fontSize: TextUnit = FontType.REGULAR,
    fontWeight: FontWeight? = null,
    trailingButton: @Composable () -> Unit = {},
) {
    MangalaWalletTopBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = {
                onBackClicked()
            }) {
                Icon(
                    imageVector = MangalaWalletPack.ArrowLeft,
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        text = text,
        trailingButton = trailingButton,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WalletMainScreenTopBar(
    selectedNetwork: BlockchainNetworkData?,
    onClickMenuIcon: () -> Unit,
    rightIcon: @Composable () -> Unit = {},
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = Spacing.SMALL, vertical = Spacing.XSMALL),
            horizontalArrangement = Arrangement.spacedBy(Spacing.TINY),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                if (selectedNetwork != null) {
                    LocalImage(
                        imageResource = selectedNetwork.localImage,
                        modifier = Modifier.size(36.dp)
                    )
                } else {
                    MangalaWalletIconButton(
                        icon = MangalaWalletPack.Category,
                        onClick = onClickMenuIcon,
                        modifier = Modifier.size(32.dp)
                    )
                }
                HorizontalSpacer(Spacing.TINY)
                Text(
                    selectedNetwork?.name ?: MR.strings.title_wallet_main.desc().localized(),
                    color = MaterialTheme.mangalaColors.textPrimary,
                    fontSize = FontType.TITLE_3,
                    fontFamily = fontFamilyResource(MR.fonts.worksans_semibold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.basicMarquee()
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rightIcon()
            }
        }

        Spacer(modifier = Modifier.height(Spacing.XTINY))
    }
}

@Composable
fun MangalaTopBarTitleInMiddle(
    isLoading: Boolean = false,
    titleTopBar: String,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier.background(MaterialTheme.mangalaColors.bg),
) {
    Box(
        modifier = modifier
            .padding(Dimensions.Padding.default)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                modifier = Modifier.size(24.dp),
                onClick = { onBackClicked() }
            ) {
                Icon(
                    imageVector = MangalaWalletPack.ArrowLeft,
                    contentDescription = null,
                    tint = MaterialTheme.mangalaColors.iconPrimary,
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = titleTopBar,
                fontSize = FontType.REGULAR,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.mangalaColors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
                    .mangalaWalletPlaceholder(isLoading)
            )
        }
    }
}