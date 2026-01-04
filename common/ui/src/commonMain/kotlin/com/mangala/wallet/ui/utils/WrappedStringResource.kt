package com.mangala.wallet.ui.utils

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.format

// For handling resource in ScreenModel
sealed class WrappedStringResource {
    class StringRes(val stringResource: dev.icerock.moko.resources.StringResource, vararg val args: Any) : WrappedStringResource()
    class PlainString(val value: String) : WrappedStringResource()
}

@Composable
fun WrappedStringResource.resolve(): String {
    return when(this) {
        is WrappedStringResource.PlainString -> value
        is WrappedStringResource.StringRes -> {
            stringResource.format(*args).localized()
        }
    }
}