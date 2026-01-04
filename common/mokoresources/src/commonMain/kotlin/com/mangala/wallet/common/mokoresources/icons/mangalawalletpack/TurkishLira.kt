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

public val MangalaWalletPack.TurkishLira: ImageVector
    get() {
        if (turkish_lira != null) {
            return turkish_lira!!
        }
        turkish_lira = Builder(name = "Turkish lira", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            group {
                path(fill = SolidColor(Color(0xFFE94268)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(12.0f, 24.0f)
                    curveTo(18.6274f, 24.0f, 24.0f, 18.6274f, 24.0f, 12.0f)
                    curveTo(24.0f, 5.3726f, 18.6274f, 0.0f, 12.0f, 0.0f)
                    curveTo(5.3726f, 0.0f, 0.0f, 5.3726f, 0.0f, 12.0f)
                    curveTo(0.0f, 18.6274f, 5.3726f, 24.0f, 12.0f, 24.0f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(11.2955f, 18.5f)
                    horizontalLineTo(9.4546f)
                    verticalLineTo(5.0f)
                    horizontalLineTo(11.2955f)
                    verticalLineTo(16.6591f)
                    curveTo(13.3256f, 16.6591f, 14.9773f, 15.0075f, 14.9773f, 12.9773f)
                    horizontalLineTo(16.8182f)
                    curveTo(16.8182f, 16.0225f, 14.3408f, 18.5f, 11.2955f, 18.5f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(13.1364f, 8.0682f)
                    lineTo(7.0f, 9.2955f)
                    verticalLineTo(7.4545f)
                    lineTo(13.1364f, 6.2273f)
                    verticalLineTo(8.0682f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(13.1364f, 11.1363f)
                    lineTo(7.0f, 12.3636f)
                    verticalLineTo(10.5227f)
                    lineTo(13.1364f, 9.2954f)
                    verticalLineTo(11.1363f)
                    close()
                }
            }
        }
        .build()
        return turkish_lira!!
    }

private var turkish_lira: ImageVector? = null
