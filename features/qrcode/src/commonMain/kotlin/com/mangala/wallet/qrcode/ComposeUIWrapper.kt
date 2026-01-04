package com.mangala.wallet.qrcode

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mangala.wallet.common.mokoresources.Dimensions
import org.koin.core.component.KoinComponent

expect class ComposeUIWrapper(): KoinComponent {

    /**
     * Save the bitmap to a temp file and return the file path
     */
    fun saveTempBitmap(address: String): Any?

    /**
     * @return true if save success
     */
    fun saveBitmapToFile(address: String): Boolean

    @Composable
    fun QRCodeImage(address: String, modifier: Modifier = Modifier.size(Dimensions.QRCodeSizeSmall))
}