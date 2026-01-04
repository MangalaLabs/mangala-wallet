package com.mangala.wallet.ui.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseBottomSheet(
    modalSheetState: SheetState,
    isShowBottomSheet: Boolean,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (isShowBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier
                .wrapContentHeight(),
            sheetState = modalSheetState,
            onDismissRequest = onDismiss,
            containerColor = Color.Transparent,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
            ),
            contentWindowInsets = {
                WindowInsets.navigationBars.union(WindowInsets.ime)
            },
            scrimColor = Color.Gray.copy(alpha = 0.5f),
            dragHandle = null,
            content = content
        )
    }
}