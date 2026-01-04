package com.mangala.wallet.ui.imageloader

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.rememberImagePainter

@Composable
fun RemoteImage(
    modifier: Modifier = Modifier,
    url: String,
    isLoading: Boolean = false,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderModifier: Modifier = modifier
) {
    val request = remember(url) {
        ImageRequest {
            data(url)
            addInterceptor(NullDataInterceptor)
        }
    }
    val painter = rememberImagePainter(request)
    Box {
        if (isLoading) {
            // Need to use a box for loading placeholder because setting a modifier on the image itself will cause the size of the image to change after load finishes
            Box(Modifier.mangalaWalletPlaceholder(true, modifier = placeholderModifier))
        } else {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = contentScale,
                modifier = modifier,
            )
        }
    }
}