package com.mangala.wallet.ui.component

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetDefaults
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigatorContent
import cafe.adriel.voyager.navigator.compositionUniqueId
import com.mangala.wallet.common.mokoresources.Colors

@OptIn(InternalVoyagerApi::class)
@ExperimentalMaterialApi
@Composable
fun MangalaBottomSheetNavigator(
    modifier: Modifier = Modifier,
    hideOnBackPress: Boolean = true,
    scrimColor: Color = Colors.darkGray.copy(alpha = 0.32f),
    sheetShape: Shape = MaterialTheme.shapes.large,
    sheetElevation: Dp = ModalBottomSheetDefaults.Elevation,
    sheetBackgroundColor: Color = MaterialTheme.colors.surface,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    sheetGesturesEnabled: Boolean = true,
    skipHalfExpanded: Boolean = true,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    key: String = compositionUniqueId(),
    sheetContent: BottomSheetNavigatorContent = { CurrentScreen() },
    content: BottomSheetNavigatorContent
) {
    BottomSheetNavigator(
        modifier,
        hideOnBackPress,
        scrimColor,
        sheetShape,
        sheetElevation,
        sheetBackgroundColor,
        sheetContentColor,
        sheetGesturesEnabled,
        skipHalfExpanded,
        animationSpec,
        key,
        sheetContent,
        content
    )
}