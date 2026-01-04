package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.KeyDelete: ImageVector
    get() {
        if (_keydelete != null) {
            return _keydelete!!
        }
        _keydelete = Builder(name = "Keydelete", defaultWidth = 24.0.dp, defaultHeight = 18.0.dp,
                viewportWidth = 24.0f, viewportHeight = 18.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(18.0f, 6.0f)
                lineTo(12.0f, 12.0f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(18.0f, 12.0f)
                lineTo(12.0f, 6.0f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(6.25f, 2.0f)
                curveTo(6.7221f, 1.3705f, 7.4631f, 1.0f, 8.25f, 1.0f)
                horizontalLineTo(20.5f)
                curveTo(21.8807f, 1.0f, 23.0f, 2.1193f, 23.0f, 3.5f)
                verticalLineTo(14.5f)
                curveTo(23.0f, 15.8807f, 21.8807f, 17.0f, 20.5f, 17.0f)
                horizontalLineTo(8.25f)
                curveTo(7.4631f, 17.0f, 6.7221f, 16.6295f, 6.25f, 16.0f)
                lineTo(2.125f, 10.5f)
                curveTo(1.4583f, 9.6111f, 1.4583f, 8.3889f, 2.125f, 7.5f)
                lineTo(6.25f, 2.0f)
                close()
            }
        }
        .build()
        return _keydelete!!
    }

private var _keydelete: ImageVector? = null
