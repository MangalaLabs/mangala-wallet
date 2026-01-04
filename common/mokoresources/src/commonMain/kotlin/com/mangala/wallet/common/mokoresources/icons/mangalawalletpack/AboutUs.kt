package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.AboutUs: ImageVector
    get() {
        if (_aboutUs != null) {
            return _aboutUs!!
        }
        _aboutUs = Builder(
            name = "AboutUs",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0x00000000)),
                stroke = SolidColor(Color(0xFF484848)),
                strokeLineWidth = 1.5f,
                strokeLineCap = Round,
                strokeLineJoin = StrokeJoin.Companion.Round,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(21.49f, 12.0f)
                curveTo(21.81f, 10.98f, 22.0f, 9.88f, 22.0f, 8.69f)
                curveTo(22.0f, 5.6f, 19.51f, 3.1f, 16.44f, 3.1f)
                curveTo(14.62f, 3.1f, 13.01f, 3.98f, 12.0f, 5.34f)
                curveTo(10.99f, 3.98f, 9.37f, 3.1f, 7.56f, 3.1f)
                curveTo(4.49f, 3.1f, 2.0f, 5.6f, 2.0f, 8.69f)
                curveTo(2.0f, 15.69f, 8.48f, 19.82f, 11.38f, 20.82f)
                curveTo(11.55f, 20.88f, 11.77f, 20.91f, 12.0f, 20.91f)
            }
            path(
                fill = SolidColor(Color(0x00000000)),
                stroke = SolidColor(Color(0xFF484848)),
                strokeLineWidth = 1.5f,
                strokeLineCap = Round,
                strokeLineJoin = StrokeJoin.Companion.Round,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(19.21f, 15.74f)
                lineTo(15.67f, 19.2801f)
                curveTo(15.53f, 19.4201f, 15.4f, 19.68f, 15.37f, 19.87f)
                lineTo(15.18f, 21.22f)
                curveTo(15.11f, 21.71f, 15.45f, 22.05f, 15.94f, 21.98f)
                lineTo(17.29f, 21.79f)
                curveTo(17.48f, 21.76f, 17.75f, 21.63f, 17.88f, 21.49f)
                lineTo(21.42f, 17.95f)
                curveTo(22.03f, 17.34f, 22.32f, 16.63f, 21.42f, 15.73f)
                curveTo(20.53f, 14.84f, 19.82f, 15.13f, 19.21f, 15.74f)
                close()
            }
            path(
                fill = SolidColor(Color(0x00000000)),
                stroke = SolidColor(Color(0xFF484848)),
                strokeLineWidth = 1.5f,
                strokeLineCap = Round,
                strokeLineJoin = StrokeJoin.Companion.Round,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(18.7f, 16.25f)
                curveTo(19.0f, 17.33f, 19.84f, 18.17f, 20.92f, 18.47f)
            }
        }.build()
        return _aboutUs!!
    }

private var _aboutUs: ImageVector? = null
