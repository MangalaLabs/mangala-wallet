package com.mangala.wallet.ui.imageloader

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource

@Composable
fun MultiImage(
    imageSource: ImageSource,
    modifier: Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    when (imageSource) {
        is ImageSource.Vector -> {
            Image(
                imageVector = imageSource.imageVector,
                contentDescription = null,
                modifier = modifier,
                contentScale = contentScale
            )
        }

        is ImageSource.Url -> {
            RemoteImage(
                url = imageSource.url,
                modifier = modifier,
                contentScale = contentScale
            )
        }

        is ImageSource.Resource -> {
            LocalImage(
                imageResource = imageSource.resource,
                modifier = modifier,
                contentScale = contentScale
            )
        }

    }
}

sealed interface ImageSource {
    data class Vector(val imageVector: ImageVector) : ImageSource
    data class Url(val url: String) : ImageSource
    data class Resource(val resource: ImageResource?) :ImageSource
}