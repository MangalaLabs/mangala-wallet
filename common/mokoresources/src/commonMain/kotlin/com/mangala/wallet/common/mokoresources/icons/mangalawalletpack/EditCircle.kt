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

public val MangalaWalletPack.EditCircle: ImageVector
    get() {
        if (_editCircle != null) {
            return _editCircle!!
        }
        _editCircle = Builder(name = "EditCircle", defaultWidth = 16.0.dp, defaultHeight = 16.0.dp,
                viewportWidth = 16.0f, viewportHeight = 16.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF767676)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(7.3335f, 1.3333f)
                horizontalLineTo(6.0002f)
                curveTo(2.6668f, 1.3333f, 1.3335f, 2.6666f, 1.3335f, 5.9999f)
                verticalLineTo(9.9999f)
                curveTo(1.3335f, 13.3333f, 2.6668f, 14.6666f, 6.0002f, 14.6666f)
                horizontalLineTo(10.0002f)
                curveTo(13.3335f, 14.6666f, 14.6668f, 13.3333f, 14.6668f, 9.9999f)
                verticalLineTo(8.6666f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF767676)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(10.6933f, 2.0133f)
                lineTo(5.4399f, 7.2666f)
                curveTo(5.2399f, 7.4666f, 5.0399f, 7.8599f, 4.9999f, 8.1466f)
                lineTo(4.7133f, 10.1533f)
                curveTo(4.6066f, 10.8799f, 5.1199f, 11.3866f, 5.8466f, 11.2866f)
                lineTo(7.8533f, 10.9999f)
                curveTo(8.1333f, 10.9599f, 8.5266f, 10.7599f, 8.7332f, 10.5599f)
                lineTo(13.9866f, 5.3066f)
                curveTo(14.8933f, 4.3999f, 15.3199f, 3.3466f, 13.9866f, 2.0133f)
                curveTo(12.6533f, 0.6799f, 11.5999f, 1.1066f, 10.6933f, 2.0133f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF767676)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(9.9399f, 2.7666f)
                curveTo(10.3866f, 4.3599f, 11.6333f, 5.6066f, 13.2333f, 6.0599f)
            }
        }
        .build()
        return _editCircle!!
    }

private var _editCircle: ImageVector? = null
