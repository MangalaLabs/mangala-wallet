package com.mangala.wallet.ui.imageloader


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource

@Composable
fun LocalImage(
    modifier: Modifier = Modifier,
    imageResource: ImageResource?,
    isLoading: Boolean = false,
    contentScale: ContentScale = ContentScale.Fit,
    placeholderModifier: Modifier = modifier
) {
    if (imageResource != null) {

        Box {
            if (isLoading) {
                Box(Modifier.mangalaWalletPlaceholder(true, modifier = placeholderModifier))
            } else {
                Image(
                    painter = painterResource(imageResource),
                    contentDescription = null,
                    contentScale = contentScale,
                    modifier = modifier,
                )
            }
        }
    }
}