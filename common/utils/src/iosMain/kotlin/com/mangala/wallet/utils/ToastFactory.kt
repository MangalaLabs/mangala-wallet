package com.mangala.wallet.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.UIKit.*
import platform.darwin.dispatch_get_main_queue

actual class ToastFactory : IToast, KoinComponent {

    actual override fun show(text: String) {
        MainScope().launch(Dispatchers.Main) {
            val activity = UIApplication.sharedApplication.keyWindow?.rootViewController
            val toast = UIAlertController.alertControllerWithTitle(
                title = null,
                message = text,
                preferredStyle = UIAlertControllerStyleAlert
            )
            toast.addAction(UIAlertAction.actionWithTitle("OK", style = UIAlertActionStyleDefault, handler = null))
            activity?.presentViewController(toast, true, null)
        }
    }

}