package com.mangala.wallet.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mangala.wallet.mokoresources.MR
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangalaDatePickerDialog(
    datePickerState: DatePickerState,
    onDismissRequest: () -> Unit,
    onConfirmClicked: () -> Unit,
    onDismissClicked: () -> Unit,
) {
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirmClicked) {
                Text(MR.strings.all_ok.desc().localized(), color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissClicked
            ) {
                Text(MR.strings.all_cancel.desc().localized(), color = Color.Black)
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            title = {
                Text(
                    text = MR.strings.title_date_picker.desc().localized(),
                    modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp)
                )
            },
            showModeToggle = false,
        )
    }
}