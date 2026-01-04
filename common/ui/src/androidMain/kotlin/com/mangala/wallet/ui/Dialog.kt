package com.mangala.wallet.ui

//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material.AlertDialog
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import com.mangala.wallet.common.mokoresources.Colors
//import com.mangala.wallet.common.mokoresources.FontType
//
//@Composable
//actual fun MangalaWalletDialog(
//    title: String,
//    message: String,
//    positiveButtonText: String,
//    negativeButtonText: String,
//    onPositiveClick: () -> Unit,
//    onNegativeClick: () -> Unit
//) {
//    //TODO: To confirm with Son for final design of this dialog
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(color = Color.Gray.copy(alpha = 0.8f))
//    ) {
//        AlertDialog(
//            onDismissRequest = onNegativeClick,
//            title = {
//                TextTitle4(text = title, color = Colors.main1Text)
//            },
//            text = {
//                TextDescription1(text = message, color = Colors.main1Text)
//            },
//            confirmButton = {
//                ButtonNormal(
//                    text = positiveButtonText,
//                    fontSize = FontType.SMALL,
//                    onClick = onPositiveClick
//                )
//            },
//            dismissButton = {
//                ButtonNormal(
//                    text = negativeButtonText,
//                    fontSize = FontType.SMALL,
//                    onClick = onNegativeClick
//                )
//            }
//        )
//    }
//}