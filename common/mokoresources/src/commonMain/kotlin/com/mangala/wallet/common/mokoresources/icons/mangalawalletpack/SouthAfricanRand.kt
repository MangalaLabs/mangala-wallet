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

public val MangalaWalletPack.SouthAfricanRand: ImageVector
    get() {
        if (south_african_rand != null) {
            return south_african_rand!!
        }
        south_african_rand = Builder(name = "South african rand", defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            group {
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(12.0f, 1.0f)
                    lineTo(12.0f, 1.0f)
                    arcTo(11.0f, 11.0f, 0.0f, false, true, 23.0f, 12.0f)
                    lineTo(23.0f, 12.0f)
                    arcTo(11.0f, 11.0f, 0.0f, false, true, 12.0f, 23.0f)
                    lineTo(12.0f, 23.0f)
                    arcTo(11.0f, 11.0f, 0.0f, false, true, 1.0f, 12.0f)
                    lineTo(1.0f, 12.0f)
                    arcTo(11.0f, 11.0f, 0.0f, false, true, 12.0f, 1.0f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF16AF80)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(12.0126f, 11.5819f)
                    lineTo(9.1131f, 11.5829f)
                    verticalLineTo(6.7733f)
                    horizontalLineTo(11.9445f)
                    curveTo(13.2705f, 6.7733f, 14.3492f, 7.8521f, 14.3492f, 9.1781f)
                    curveTo(14.3492f, 10.4686f, 13.3009f, 11.5463f, 12.0126f, 11.5819f)
                    close()
                    moveTo(24.0f, 12.0f)
                    curveTo(24.0f, 18.6168f, 18.6168f, 24.0f, 12.0f, 24.0f)
                    curveTo(5.3832f, 24.0f, 0.0f, 18.6168f, 0.0f, 12.0f)
                    curveTo(0.0f, 5.3832f, 5.3832f, 0.0f, 12.0f, 0.0f)
                    curveTo(18.6168f, 0.0f, 24.0f, 5.3832f, 24.0f, 12.0f)
                    close()
                    moveTo(16.2383f, 17.5286f)
                    lineTo(12.8467f, 12.9776f)
                    curveTo(13.5588f, 12.8085f, 14.2119f, 12.4399f, 14.7367f, 11.9022f)
                    curveTo(15.4541f, 11.167f, 15.8492f, 10.1996f, 15.8492f, 9.1781f)
                    curveTo(15.8492f, 7.025f, 14.0976f, 5.2733f, 11.9445f, 5.2733f)
                    horizontalLineTo(8.3631f)
                    curveTo(7.9489f, 5.2733f, 7.6131f, 5.6091f, 7.6131f, 6.0233f)
                    verticalLineTo(17.9767f)
                    curveTo(7.6131f, 18.391f, 7.9489f, 18.7267f, 8.3631f, 18.7267f)
                    curveTo(8.7773f, 18.7267f, 9.1131f, 18.391f, 9.1131f, 17.9767f)
                    verticalLineTo(13.0829f)
                    horizontalLineTo(11.0543f)
                    lineTo(15.0356f, 18.4249f)
                    curveTo(15.1828f, 18.6225f, 15.4087f, 18.7268f, 15.6375f, 18.7268f)
                    curveTo(15.7934f, 18.7268f, 15.9506f, 18.6784f, 16.0851f, 18.5781f)
                    curveTo(16.4172f, 18.3306f, 16.4858f, 17.8607f, 16.2383f, 17.5286f)
                    close()
                }
            }
        }
        .build()
        return south_african_rand!!
    }

private var south_african_rand: ImageVector? = null
