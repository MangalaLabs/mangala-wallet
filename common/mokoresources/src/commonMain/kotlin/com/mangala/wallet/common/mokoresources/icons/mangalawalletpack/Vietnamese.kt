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

public val MangalaWalletPack.Vietnamese: ImageVector
    get() {
        if (vietnamese != null) {
            return vietnamese!!
        }
        vietnamese = Builder(name = "Vietnamese", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            group {
                path(fill = SolidColor(Color(0xFFD80027)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(12.0f, 24.0f)
                    curveTo(18.6274f, 24.0f, 24.0f, 18.6274f, 24.0f, 12.0f)
                    curveTo(24.0f, 5.3726f, 18.6274f, 0.0f, 12.0f, 0.0f)
                    curveTo(5.3726f, 0.0f, 0.0f, 5.3726f, 0.0f, 12.0f)
                    curveTo(0.0f, 18.6274f, 5.3726f, 24.0f, 12.0f, 24.0f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFFDA44)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(12.0f, 6.2609f)
                    lineTo(13.2951f, 10.2466f)
                    horizontalLineTo(17.486f)
                    lineTo(14.0954f, 12.7099f)
                    lineTo(15.3905f, 16.6957f)
                    lineTo(12.0f, 14.2323f)
                    lineTo(8.6096f, 16.6957f)
                    lineTo(9.9046f, 12.7099f)
                    lineTo(6.5142f, 10.2466f)
                    horizontalLineTo(10.705f)
                    lineTo(12.0f, 6.2609f)
                    close()
                }
            }
        }
        .build()
        return vietnamese!!
    }

private var vietnamese: ImageVector? = null
