package com.mangala.wallet.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.CornerRadius

@Composable
fun MangalaOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier,
    label: String
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(CornerRadius.Small),
        border = BorderStroke(1.dp, ColorsNew.black)
    ) {
        Text(
            label,
            color = ColorsNew.black
        )
    }
}