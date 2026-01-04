package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.Copy: ImageVector
    get() {
        if (_copy != null) {
            return _copy!!
        }
        _copy = Builder(name = "Copy", defaultWidth = 16.0.dp, defaultHeight = 16.0.dp,
            viewportWidth = 16.0f, viewportHeight = 16.0f).apply {
            group {
                path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType =
                    NonZero) {
                    moveTo(13.3333f, 6.0f)
                    horizontalLineTo(7.3333f)
                    curveTo(6.9797f, 6.0f, 6.6406f, 6.1405f, 6.3905f, 6.3905f)
                    curveTo(6.1405f, 6.6406f, 6.0f, 6.9797f, 6.0f, 7.3333f)
                    verticalLineTo(13.3333f)
                    curveTo(6.0f, 13.687f, 6.1405f, 14.0261f, 6.3905f, 14.2761f)
                    curveTo(6.6406f, 14.5262f, 6.9797f, 14.6667f, 7.3333f, 14.6667f)
                    horizontalLineTo(13.3333f)
                    curveTo(13.687f, 14.6667f, 14.0261f, 14.5262f, 14.2761f, 14.2761f)
                    curveTo(14.5262f, 14.0261f, 14.6667f, 13.687f, 14.6667f, 13.3333f)
                    verticalLineTo(7.3333f)
                    curveTo(14.6667f, 6.9797f, 14.5262f, 6.6406f, 14.2761f, 6.3905f)
                    curveTo(14.0261f, 6.1405f, 13.687f, 6.0f, 13.3333f, 6.0f)
                    close()
                }
                path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType =
                    NonZero) {
                    moveTo(3.3335f, 9.9999f)
                    horizontalLineTo(2.6668f)
                    curveTo(2.3136f, 9.9985f, 1.9753f, 9.8576f, 1.7256f, 9.6078f)
                    curveTo(1.4758f, 9.3581f, 1.3349f, 9.0198f, 1.3335f, 8.6666f)
                    verticalLineTo(2.6666f)
                    curveTo(1.3349f, 2.3134f, 1.4758f, 1.9751f, 1.7256f, 1.7253f)
                    curveTo(1.9753f, 1.4756f, 2.3136f, 1.3347f, 2.6668f, 1.3333f)
                    horizontalLineTo(8.6668f)
                    curveTo(9.02f, 1.3347f, 9.3583f, 1.4756f, 9.6081f, 1.7253f)
                    curveTo(9.8578f, 1.9751f, 9.9988f, 2.3134f, 10.0002f, 2.6666f)
                    verticalLineTo(3.3333f)
                }
            }
        }
            .build()
        return _copy!!
    }

private var _copy: ImageVector? = null
