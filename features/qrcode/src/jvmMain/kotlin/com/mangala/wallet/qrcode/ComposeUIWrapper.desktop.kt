package com.mangala.wallet.qrcode

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.Density
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.scanqr.QRCodeGenerator
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.xml.sax.InputSource
import java.io.File
import java.io.IOException
import java.net.URL

actual class ComposeUIWrapper : KoinComponent {
    private val qrCodeGenerator: QRCodeGenerator by inject()

    actual fun saveBitmapToFile(address: String): Boolean {
        return qrCodeGenerator.saveBitmapToFile(address)
    }

    @Composable
    actual fun QRCodeImage(address: String, modifier: Modifier) {
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
            val fileBitmap =
                remember(address) { qrCodeGenerator.generateQRCode(address) as? String }
            fileBitmap?.let {
                AsyncImage(
                    load = { loadImageBitmap(File(fileBitmap)) },
                    painterFor = { remember { BitmapPainter(it) } },
                    contentDescription = "Idea logo",
                    contentScale = ContentScale.FillWidth,
                    modifier = modifier
                )
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

    /**
     * Save the bitmap to a temp file and return the file path
     */
    actual fun saveTempBitmap(address: String): Any? {
        TODO("Not yet implemented")
    }
}

@Composable
fun <T> AsyncImage(
    load: suspend () -> T,
    painterFor: @Composable (T) -> Painter,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val image: T? by produceState<T?>(null) {
        value = withContext(Dispatchers.IO) {
            try {
                load()
            } catch (e: IOException) {
                // instead of printing to console, you can also write this to log,
                // or show some error placeholder
                e.printStackTrace()
                null
            }
        }
    }

    if (image != null) {
        Image(
            painter = painterFor(image!!),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    }
}

/* Loading from file with java.io API */

fun loadImageBitmap(file: File): ImageBitmap =
    file.inputStream().buffered().use(::loadImageBitmap)

fun loadSvgPainter(file: File, density: Density): Painter =
    file.inputStream().buffered().use { androidx.compose.ui.res.loadSvgPainter(it, density) }

fun loadXmlImageVector(file: File, density: Density): ImageVector =
    file.inputStream().buffered().use {
        androidx.compose.ui.res.loadXmlImageVector(
            InputSource(it),
            density
        )
    }

/* Loading from network with java.net API */

fun loadImageBitmap(url: String): ImageBitmap =
    URL(url).openStream().buffered().use(::loadImageBitmap)

fun loadSvgPainter(url: String, density: Density): Painter =
    URL(url).openStream().buffered().use { androidx.compose.ui.res.loadSvgPainter(it, density) }

fun loadXmlImageVector(url: String, density: Density): ImageVector =
    URL(url).openStream().buffered().use {
        androidx.compose.ui.res.loadXmlImageVector(
            InputSource(it),
            density
        )
    }

/* Loading from network with Ktor client API (https://ktor.io/docs/client.html). */

/*

suspend fun loadImageBitmap(url: String): ImageBitmap =
    urlStream(url).use(::loadImageBitmap)

suspend fun loadSvgPainter(url: String, density: Density): Painter =
    urlStream(url).use { loadSvgPainter(it, density) }

suspend fun loadXmlImageVector(url: String, density: Density): ImageVector =
    urlStream(url).use { loadXmlImageVector(InputSource(it), density) }

@OptIn(KtorExperimentalAPI::class)
private suspend fun urlStream(url: String) = HttpClient(CIO).use {
    ByteArrayInputStream(it.get(url))
}

 */