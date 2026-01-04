package com.mangala.wallet.features.addressbook.icon.contacticon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.features.addressbook.icon.ContactIcon

public val ContactIcon.OutlineStar: ImageVector
    get() {
        if (_outlinestar != null) {
            return _outlinestar!!
        }
        _outlinestar = Builder(name = "Outlinestar", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            group {
                path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF262626)),
                        strokeLineWidth = 1.5f, strokeLineCap = Butt, strokeLineJoin = Miter,
                        strokeLineMiter = 4.0f, pathFillType = NonZero) {
                    moveTo(12.0f, 1.265f)
                    curveTo(12.184f, 1.265f, 12.352f, 1.36f, 12.447f, 1.515f)
                    lineTo(12.484f, 1.585f)
                    lineTo(15.223f, 7.994f)
                    lineTo(15.398f, 8.406f)
                    lineTo(15.845f, 8.447f)
                    lineTo(22.771f, 9.076f)
                    horizontalLineTo(22.772f)
                    curveTo(22.956f, 9.093f, 23.114f, 9.203f, 23.194f, 9.364f)
                    lineTo(23.224f, 9.437f)
                    verticalLineTo(9.438f)
                    curveTo(23.281f, 9.613f, 23.242f, 9.803f, 23.125f, 9.941f)
                    lineTo(23.069f, 9.997f)
                    verticalLineTo(9.998f)
                    lineTo(17.833f, 14.589f)
                    lineTo(17.497f, 14.884f)
                    lineTo(17.596f, 15.319f)
                    lineTo(19.14f, 22.12f)
                    curveTo(19.181f, 22.301f, 19.125f, 22.486f, 18.995f, 22.613f)
                    lineTo(18.935f, 22.664f)
                    curveTo(18.786f, 22.773f, 18.594f, 22.793f, 18.429f, 22.725f)
                    lineTo(18.359f, 22.69f)
                    lineTo(18.358f, 22.688f)
                    lineTo(12.385f, 19.117f)
                    lineTo(12.0f, 18.888f)
                    lineTo(11.615f, 19.117f)
                    lineTo(5.64f, 22.688f)
                    curveTo(5.554f, 22.74f, 5.462f, 22.765f, 5.371f, 22.765f)
                    curveTo(5.291f, 22.765f, 5.211f, 22.746f, 5.136f, 22.708f)
                    lineTo(5.063f, 22.663f)
                    curveTo(4.893f, 22.54f, 4.812f, 22.329f, 4.859f, 22.12f)
                    lineTo(6.403f, 15.319f)
                    lineTo(6.502f, 14.884f)
                    lineTo(6.166f, 14.589f)
                    lineTo(0.931f, 9.997f)
                    lineTo(0.929f, 9.996f)
                    lineTo(0.874f, 9.94f)
                    curveTo(0.758f, 9.804f, 0.719f, 9.614f, 0.776f, 9.438f)
                    curveTo(0.842f, 9.236f, 1.018f, 9.096f, 1.229f, 9.076f)
                    lineTo(1.228f, 9.075f)
                    lineTo(8.156f, 8.447f)
                    lineTo(8.602f, 8.406f)
                    lineTo(8.777f, 7.994f)
                    lineTo(11.517f, 1.585f)
                    curveTo(11.601f, 1.389f, 11.79f, 1.265f, 12.0f, 1.265f)
                    close()
                }
            }
        }
        .build()
        return _outlinestar!!
    }

private var _outlinestar: ImageVector? = null
