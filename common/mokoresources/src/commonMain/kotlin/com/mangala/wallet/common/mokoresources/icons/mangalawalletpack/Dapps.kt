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

public val MangalaWalletPack.Dapps: ImageVector
    get() {
        if (_dapps != null) {
            return _dapps!!
        }
        _dapps = Builder(name = "Dapps", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                moveTo(14.737f, 2.762f)
                horizontalLineTo(8.084f)
                curveTo(6.025f, 2.762f, 4.25f, 4.431f, 4.25f, 6.491f)
                verticalLineTo(17.204f)
                curveTo(4.25f, 19.38f, 5.909f, 21.115f, 8.084f, 21.115f)
                horizontalLineTo(16.073f)
                curveTo(18.133f, 21.115f, 19.802f, 19.265f, 19.802f, 17.204f)
                verticalLineTo(8.038f)
                lineTo(14.737f, 2.762f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(14.4746f, 2.7502f)
                verticalLineTo(5.6592f)
                curveTo(14.4746f, 7.0792f, 15.6236f, 8.2312f, 17.0426f, 8.2342f)
                curveTo(18.3596f, 8.2372f, 19.7066f, 8.2382f, 19.7976f, 8.2322f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(14.2847f, 15.5579f)
                horizontalLineTo(8.8877f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(12.2427f, 10.6056f)
                horizontalLineTo(8.8867f)
            }
        }
        .build()
        return _dapps!!
    }

private var _dapps: ImageVector? = null
