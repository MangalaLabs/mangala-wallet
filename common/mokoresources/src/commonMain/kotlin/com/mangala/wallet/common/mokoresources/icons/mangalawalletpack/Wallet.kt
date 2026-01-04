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

public val MangalaWalletPack.Wallet: ImageVector
    get() {
        if (_wallet != null) {
            return _wallet!!
        }
        _wallet = Builder(name = "Wallet", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(21.6389f, 14.3957f)
                horizontalLineTo(17.5906f)
                curveTo(16.1042f, 14.3948f, 14.8993f, 13.1909f, 14.8984f, 11.7045f)
                curveTo(14.8984f, 10.218f, 16.1042f, 9.0141f, 17.5906f, 9.0132f)
                horizontalLineTo(21.6389f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(18.049f, 11.6429f)
                horizontalLineTo(17.7373f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                moveTo(7.7477f, 3.0f)
                horizontalLineTo(16.3911f)
                curveTo(19.2892f, 3.0f, 21.6388f, 5.3495f, 21.6388f, 8.2477f)
                verticalLineTo(15.4247f)
                curveTo(21.6388f, 18.3229f, 19.2892f, 20.6724f, 16.3911f, 20.6724f)
                horizontalLineTo(7.7477f)
                curveTo(4.8495f, 20.6724f, 2.5f, 18.3229f, 2.5f, 15.4247f)
                verticalLineTo(8.2477f)
                curveTo(2.5f, 5.3495f, 4.8495f, 3.0f, 7.7477f, 3.0f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(7.0352f, 7.5382f)
                horizontalLineTo(12.4341f)
            }
        }
        .build()
        return _wallet!!
    }

private var _wallet: ImageVector? = null
