package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.HomeReceive: ImageVector
    get() {
        if (_homereceive != null) {
            return _homereceive!!
        }
        _homereceive = Builder(name = "Homereceive", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF767676)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                moveTo(7.666f, 21.25f)
                horizontalLineTo(16.335f)
                curveTo(19.355f, 21.25f, 21.25f, 19.111f, 21.25f, 16.084f)
                verticalLineTo(7.916f)
                curveTo(21.25f, 4.889f, 19.365f, 2.75f, 16.335f, 2.75f)
                horizontalLineTo(7.666f)
                curveTo(4.636f, 2.75f, 2.75f, 4.889f, 2.75f, 7.916f)
                verticalLineTo(16.084f)
                curveTo(2.75f, 19.111f, 4.636f, 21.25f, 7.666f, 21.25f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(12.0f, 16.0861f)
                verticalLineTo(7.9141f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(15.748f, 12.3223f)
                lineTo(12.0f, 16.0863f)
                lineTo(8.252f, 12.3223f)
            }
        }
        .build()
        return _homereceive!!
    }

private var _homereceive: ImageVector? = null
