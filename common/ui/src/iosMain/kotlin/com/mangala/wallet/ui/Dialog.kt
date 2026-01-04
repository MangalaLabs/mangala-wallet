package com.mangala.wallet.ui

//import androidx.compose.runtime.Composable
//import platform.UIKit.UIAlertAction
//import platform.UIKit.UIAlertActionStyleCancel
//import platform.UIKit.UIAlertActionStyleDefault
//import platform.UIKit.UIAlertController
//import platform.UIKit.UIAlertControllerStyleAlert
//import platform.UIKit.UIApplication
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
//    val alertController = UIAlertController.alertControllerWithTitle(
//        title, message, UIAlertControllerStyleAlert
//    )
//
//    val positiveAction = UIAlertAction.actionWithTitle(
//        positiveButtonText, UIAlertActionStyleDefault
//    ) { onPositiveClick() }
//
//    val negativeAction = UIAlertAction.actionWithTitle(
//        negativeButtonText, UIAlertActionStyleCancel
//    ) { onNegativeClick() }
//
//    alertController.addAction(positiveAction)
//    alertController.addAction(negativeAction)
//
//    presentAlertController(alertController)
//}
//
//private fun presentAlertController(alertController: UIAlertController) {
//    val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
//    rootVC?.presentViewController(alertController, animated = true, completion = null)
//}
