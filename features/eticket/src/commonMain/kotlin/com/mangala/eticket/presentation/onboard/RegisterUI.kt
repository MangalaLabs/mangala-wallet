package com.mangala.eticket.presentation.onboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun Register(
    userFullName: String,
    onUserFullNameChange: (String) -> Unit,
    onClickCreateUser:() -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        OutlinedTextField(
            value = userFullName,
            onValueChange = onUserFullNameChange,
            placeholder = { Text("Your full name") },
            label = { Text("Full name") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                placeholderColor = Color.Gray,
                disabledLabelColor = Color.Black,
                focusedLabelColor = Color.Black,
                errorBorderColor = Color.Red,
                errorLabelColor = Color.Red
            )
        )
        Button(
            onClick = onClickCreateUser,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Blue,
                contentColor = Color.White
            )
        ) {
            Text("Register")
        }
    }
}