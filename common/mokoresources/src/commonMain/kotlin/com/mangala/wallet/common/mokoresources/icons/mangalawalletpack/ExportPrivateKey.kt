package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.ExportPrivateKey: ImageVector
    get() {
        if (_exportPrivateKey != null) {
            return _exportPrivateKey!!
        }
        _exportPrivateKey = Builder(name = "ExportPrivateKey", defaultWidth = 18.0.dp, defaultHeight
                = 18.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF383838)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(20.9099f, 11.1203f)
                curveTo(20.9099f, 16.0103f, 17.3599f, 20.5903f, 12.5099f, 21.9303f)
                curveTo(12.1799f, 22.0203f, 11.8198f, 22.0203f, 11.4898f, 21.9303f)
                curveTo(6.6398f, 20.5903f, 3.0898f, 16.0103f, 3.0898f, 11.1203f)
                verticalLineTo(6.7303f)
                curveTo(3.0898f, 5.9103f, 3.7099f, 4.9803f, 4.4799f, 4.6703f)
                lineTo(10.0498f, 2.3903f)
                curveTo(11.2998f, 1.8803f, 12.7098f, 1.8803f, 13.9598f, 2.3903f)
                lineTo(19.5298f, 4.6703f)
                curveTo(20.2898f, 4.9803f, 20.9199f, 5.9103f, 20.9199f, 6.7303f)
                lineTo(20.9099f, 11.1203f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF383838)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(12.0f, 12.5f)
                curveTo(13.1046f, 12.5f, 14.0f, 11.6046f, 14.0f, 10.5f)
                curveTo(14.0f, 9.3954f, 13.1046f, 8.5f, 12.0f, 8.5f)
                curveTo(10.8954f, 8.5f, 10.0f, 9.3954f, 10.0f, 10.5f)
                curveTo(10.0f, 11.6046f, 10.8954f, 12.5f, 12.0f, 12.5f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF383838)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(12.0f, 12.5f)
                verticalLineTo(15.5f)
            }
        }
        .build()
        return _exportPrivateKey!!
    }

private var _exportPrivateKey: ImageVector? = null
