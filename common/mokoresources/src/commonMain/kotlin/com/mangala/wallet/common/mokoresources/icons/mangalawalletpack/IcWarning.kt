package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import kotlin.Unit

public val MangalaWalletPack.IcWarning: ImageVector
    get() {
        if (_icWarning != null) {
            return _icWarning!!
        }
        _icWarning = Builder(name = "IcWarning", defaultWidth = 16.0.dp, defaultHeight = 16.0.dp,
                viewportWidth = 16.0f, viewportHeight = 16.0f).apply {
            group {
                path(fill = SolidColor(Color(0xFFF9A207)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(15.668f, 11.998f)
                    lineTo(10.016f, 1.608f)
                    curveTo(9.107f, 0.08f, 6.894f, 0.077f, 5.984f, 1.608f)
                    lineTo(0.333f, 11.998f)
                    curveTo(-0.596f, 13.56f, 0.528f, 15.538f, 2.348f, 15.538f)
                    horizontalLineTo(13.652f)
                    curveTo(15.47f, 15.538f, 16.596f, 13.562f, 15.668f, 11.998f)
                    close()
                    moveTo(8.0f, 13.663f)
                    curveTo(7.483f, 13.663f, 7.063f, 13.243f, 7.063f, 12.726f)
                    curveTo(7.063f, 12.209f, 7.483f, 11.788f, 8.0f, 11.788f)
                    curveTo(8.517f, 11.788f, 8.938f, 12.209f, 8.938f, 12.726f)
                    curveTo(8.938f, 13.243f, 8.517f, 13.663f, 8.0f, 13.663f)
                    close()
                    moveTo(8.938f, 9.913f)
                    curveTo(8.938f, 10.43f, 8.517f, 10.851f, 8.0f, 10.851f)
                    curveTo(7.483f, 10.851f, 7.063f, 10.43f, 7.063f, 9.913f)
                    verticalLineTo(5.226f)
                    curveTo(7.063f, 4.709f, 7.483f, 4.288f, 8.0f, 4.288f)
                    curveTo(8.517f, 4.288f, 8.938f, 4.709f, 8.938f, 5.226f)
                    verticalLineTo(9.913f)
                    close()
                }
            }
        }
        .build()
        return _icWarning!!
    }

private var _icWarning: ImageVector? = null
