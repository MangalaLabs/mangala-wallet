package com.mangala.wallet.qrcode

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.scanqr.QRCodeGenerator
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class ComposeUIWrapper: KoinComponent {
    private val qrCodeGenerator: QRCodeGenerator by inject()

    actual fun saveTempBitmap(address: String): Any? {
        return qrCodeGenerator.saveTempBitmap(address)
    }

    actual fun saveBitmapToFile(address: String): Boolean {
        return qrCodeGenerator.saveBitmapToFile(address)
    }

    @Composable
    actual fun QRCodeImage(address: String, modifier: Modifier){
        if (address.isBlank())
            Box(
                modifier = Modifier
                    .mangalaWalletPlaceholder(
                        visible = true,
                        color = ColorsNew.white
                    )
                    .then(modifier)
            )
        else {
            val imageBitmap = remember(address) { qrCodeGenerator.generateQRCode(address) as? ImageBitmap }
            imageBitmap?.let {
                Image(imageBitmap, modifier = modifier, contentDescription = "QRCode")
            } ?: run {
                Box(
                    modifier = Modifier
                        .mangalaWalletPlaceholder(
                            visible = true,
                            color = ColorsNew.white
                        )
                        .then(modifier)
                )
            }
        }
    }
}