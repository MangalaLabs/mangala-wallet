package com.mangala.wallet.ui.imageloader

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import com.mangala.wallet.common.mokoresources.Dimensions
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource


@Composable
fun DisplayImageForIcon(imageHolder: ImageHolder, modifier: Modifier = Modifier) {
    when (imageHolder) {
        is ImageHolder.Vector ->
            Image(
                imageVector = imageHolder.imageVector,
                contentDescription = null,
                modifier = modifier.width(Dimensions.IconButtonSize)
                    .height(Dimensions.IconButtonSize).clip(CircleShape)
            )
        is ImageHolder.Paint ->
            Image(
                painter = painterResource(imageHolder.painter),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier.width(Dimensions.IconButtonSize)
                    .height(Dimensions.IconButtonSize)
                    .clip(CircleShape),
            )
    }
}
sealed class ImageHolder {
    data class Vector(val imageVector: ImageVector) : ImageHolder()
    data class Paint(val painter: ImageResource) : ImageHolder()
}
