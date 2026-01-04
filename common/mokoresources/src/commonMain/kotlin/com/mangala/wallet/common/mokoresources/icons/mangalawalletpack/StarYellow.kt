package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

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
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.StarYellow: ImageVector
    get() {
        if (_starYellow != null) {
            return _starYellow!!
        }
        _starYellow = Builder(name = "StarYellow", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            group {
                path(fill = SolidColor(Color(0xFFFFC043)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(23.9374f, 9.2063f)
                    curveTo(23.7803f, 8.7204f, 23.3493f, 8.3752f, 22.8393f, 8.3292f)
                    lineTo(15.9123f, 7.7003f)
                    lineTo(13.1731f, 1.289f)
                    curveTo(12.9712f, 0.8192f, 12.5112f, 0.515f, 12.0001f, 0.515f)
                    curveTo(11.4891f, 0.515f, 11.0291f, 0.8192f, 10.8271f, 1.2901f)
                    lineTo(8.088f, 7.7003f)
                    lineTo(1.1598f, 8.3292f)
                    curveTo(0.6508f, 8.3763f, 0.2208f, 8.7204f, 0.0628f, 9.2063f)
                    curveTo(-0.0952f, 9.6923f, 0.0507f, 10.2253f, 0.4358f, 10.5614f)
                    lineTo(5.6718f, 15.1534f)
                    lineTo(4.1279f, 21.9547f)
                    curveTo(4.0149f, 22.4547f, 4.209f, 22.9716f, 4.6239f, 23.2716f)
                    curveTo(4.8469f, 23.4327f, 5.1079f, 23.5148f, 5.371f, 23.5148f)
                    curveTo(5.5979f, 23.5148f, 5.8229f, 23.4536f, 6.0249f, 23.3327f)
                    lineTo(12.0001f, 19.7615f)
                    lineTo(17.9732f, 23.3327f)
                    curveTo(18.4103f, 23.5957f, 18.9612f, 23.5717f, 19.3752f, 23.2716f)
                    curveTo(19.7904f, 22.9707f, 19.9843f, 22.4536f, 19.8713f, 21.9547f)
                    lineTo(18.3273f, 15.1534f)
                    lineTo(23.5633f, 10.5623f)
                    curveTo(23.9484f, 10.2253f, 24.0955f, 9.6932f, 23.9374f, 9.2063f)
                    close()
                }
            }
        }
        .build()
        return _starYellow!!
    }

private var _starYellow: ImageVector? = null
