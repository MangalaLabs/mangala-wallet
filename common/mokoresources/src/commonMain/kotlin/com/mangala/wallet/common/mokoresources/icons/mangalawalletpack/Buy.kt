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

public val MangalaWalletPack.Buy: ImageVector
    get() {
        if (_buy != null) {
            return _buy!!
        }
        _buy = Builder(name = "Buy", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp, viewportWidth
                = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(17.0f, 9.0f)
                horizontalLineTo(13.2298f)
                curveTo(12.5163f, 9.0f, 11.5161f, 9.0f, 10.8026f, 9.0f)
                horizontalLineTo(7.0f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF767676)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                moveTo(6.8879f, 3.5f)
                horizontalLineTo(16.3158f)
                curveTo(17.6752f, 3.5152f, 18.969f, 4.0899f, 19.896f, 5.0902f)
                curveTo(20.823f, 6.0905f, 21.3022f, 7.429f, 21.222f, 8.7941f)
                verticalLineTo(15.322f)
                curveTo(21.3022f, 16.6871f, 20.823f, 18.0256f, 19.896f, 19.0259f)
                curveTo(18.969f, 20.0262f, 17.6752f, 20.6009f, 16.3158f, 20.6161f)
                horizontalLineTo(6.8879f)
                curveTo(3.968f, 20.6161f, 2.0f, 18.2407f, 2.0f, 15.322f)
                verticalLineTo(8.7941f)
                curveTo(2.0f, 5.8755f, 3.968f, 3.5f, 6.8879f, 3.5f)
                close()
            }
        }
        .build()
        return _buy!!
    }

private var _buy: ImageVector? = null
