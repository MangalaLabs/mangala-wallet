package com.mangala.wallet.features.addressbook.domain.sharing

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Paths
import javax.imageio.ImageIO

actual class ImageSharingHelper {
    
    actual suspend fun shareQrImage(
        image: ImageBitmap,
        title: String,
        text: String?
    ): ShareResult = withContext(Dispatchers.IO) {
        try {
            // On desktop, copy to clipboard and optionally save to Downloads
            val bufferedImage = image.toAwtImage()
            
            // Copy to clipboard
            val selection = ImageSelection(bufferedImage)
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(selection, null)
            
            // Also save to Downloads folder
            val userHome = System.getProperty("user.home")
            val downloadsPath = Paths.get(userHome, "Downloads")
            val file = File(downloadsPath.toFile(), "qr_${System.currentTimeMillis()}.png")
            
            ImageIO.write(bufferedImage, "PNG", file)
            
            ShareResult.Success
        } catch (e: Exception) {
            ShareResult.Error(e.message ?: "Unknown error")
        }
    }
    
    actual suspend fun saveQrImageToGallery(
        image: ImageBitmap,
        filename: String
    ): SaveResult = withContext(Dispatchers.IO) {
        try {
            val bufferedImage = image.toAwtImage()
            
            // Save to Pictures folder
            val userHome = System.getProperty("user.home")
            val picturesPath = Paths.get(userHome, "Pictures", "Mangala QR Codes")
            picturesPath.toFile().mkdirs()
            
            val file = File(picturesPath.toFile(), "$filename.png")
            ImageIO.write(bufferedImage, "PNG", file)
            
            SaveResult.Success
        } catch (e: Exception) {
            SaveResult.Error(e.message ?: "Unknown error")
        }
    }
    
    private class ImageSelection(private val image: BufferedImage) : Transferable {
        override fun getTransferDataFlavors(): Array<DataFlavor> {
            return arrayOf(DataFlavor.imageFlavor)
        }
        
        override fun isDataFlavorSupported(flavor: DataFlavor): Boolean {
            return DataFlavor.imageFlavor.equals(flavor)
        }
        
        override fun getTransferData(flavor: DataFlavor): Any {
            if (!DataFlavor.imageFlavor.equals(flavor)) {
                throw UnsupportedOperationException("Unsupported flavor: $flavor")
            }
            return image
        }
    }
}