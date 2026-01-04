package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.Edit: ImageVector
    get() {
        if (_edit != null) {
            return _edit!!
        }
        _edit = Builder(
            name = "Edit", defaultWidth = 14.0.dp, defaultHeight = 14.0.dp,
            viewportWidth = 14.0f, viewportHeight = 14.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF767676)), stroke = null, fillAlpha = 0.4f, strokeAlpha
                = 0.4f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(11.8011f, 10.8359f)
                horizontalLineTo(7.9299f)
                curveTo(7.6818f, 10.8359f, 7.4805f, 11.0351f, 7.4805f, 11.2806f)
                curveTo(7.4805f, 11.526f, 7.6818f, 11.7252f, 7.9299f, 11.7252f)
                horizontalLineTo(11.8011f)
                curveTo(12.0492f, 11.7252f, 12.2505f, 11.526f, 12.2505f, 11.2806f)
                curveTo(12.2505f, 11.0351f, 12.0492f, 10.8359f, 11.8011f, 10.8359f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF767676)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(9.8431f, 6.8253f)
                lineTo(5.4056f, 11.2099f)
                curveTo(5.1018f, 11.5099f, 4.6901f, 11.6794f, 4.2611f, 11.6794f)
                horizontalLineTo(2.1682f)
                curveTo(2.0568f, 11.6794f, 1.9507f, 11.6343f, 1.8668f, 11.5573f)
                curveTo(1.7889f, 11.4749f, 1.75f, 11.3699f, 1.75f, 11.2543f)
                lineTo(1.8057f, 9.1699f)
                curveTo(1.8117f, 8.7568f, 1.9843f, 8.3655f, 2.2797f, 8.0726f)
                lineTo(6.6944f, 3.7046f)
                curveTo(6.7675f, 3.6323f, 6.8861f, 3.6323f, 6.9598f, 3.7046f)
                lineTo(9.8431f, 6.5633f)
                curveTo(9.9167f, 6.6356f, 9.9162f, 6.753f, 9.8431f, 6.8253f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF767676)), stroke = null, fillAlpha = 0.4f, strokeAlpha
                = 0.4f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(11.4007f, 5.2629f)
                lineTo(10.7974f, 5.8652f)
                curveTo(10.7069f, 5.9553f, 10.5595f, 5.9559f, 10.4684f, 5.8658f)
                lineTo(7.6541f, 3.0813f)
                curveTo(7.5636f, 2.9918f, 7.563f, 2.8465f, 7.6535f, 2.7564f)
                lineTo(8.2503f, 2.1606f)
                curveTo(8.7979f, 1.6146f, 9.6895f, 1.6128f, 10.2395f, 2.1571f)
                lineTo(11.3971f, 3.3024f)
                curveTo(11.9436f, 3.8431f, 11.9454f, 4.7199f, 11.4007f, 5.2629f)
                close()
            }
        }
            .build()
        return _edit!!
    }

private var _edit: ImageVector? = null
