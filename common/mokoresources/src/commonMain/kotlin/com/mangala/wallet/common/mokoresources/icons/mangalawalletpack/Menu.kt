package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.Menu: ImageVector
    get() {
        if (_menu != null) {
            return _menu!!
        }
        _menu = Builder(name = "Menu", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                moveTo(16.2849f, 2.0f)
                horizontalLineTo(19.5515f)
                curveTo(20.903f, 2.0f, 21.9993f, 3.1059f, 21.9993f, 4.4702f)
                verticalLineTo(7.7641f)
                curveTo(21.9993f, 9.1273f, 20.903f, 10.2343f, 19.5515f, 10.2343f)
                horizontalLineTo(16.2849f)
                curveTo(14.9323f, 10.2343f, 13.8359f, 9.1273f, 13.8359f, 7.7641f)
                verticalLineTo(4.4702f)
                curveTo(13.8359f, 3.1059f, 14.9323f, 2.0f, 16.2849f, 2.0f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                moveTo(4.4489f, 2.0f)
                horizontalLineTo(7.7145f)
                curveTo(9.067f, 2.0f, 10.1634f, 3.1059f, 10.1634f, 4.4702f)
                verticalLineTo(7.7641f)
                curveTo(10.1634f, 9.1273f, 9.067f, 10.2343f, 7.7145f, 10.2343f)
                horizontalLineTo(4.4489f)
                curveTo(3.0964f, 10.2343f, 2.0f, 9.1273f, 2.0f, 7.7641f)
                verticalLineTo(4.4702f)
                curveTo(2.0f, 3.1059f, 3.0964f, 2.0f, 4.4489f, 2.0f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                moveTo(4.4489f, 13.7657f)
                horizontalLineTo(7.7145f)
                curveTo(9.067f, 13.7657f, 10.1634f, 14.8716f, 10.1634f, 16.2369f)
                verticalLineTo(19.5298f)
                curveTo(10.1634f, 20.8941f, 9.067f, 22.0f, 7.7145f, 22.0f)
                horizontalLineTo(4.4489f)
                curveTo(3.0964f, 22.0f, 2.0f, 20.8941f, 2.0f, 19.5298f)
                verticalLineTo(16.2369f)
                curveTo(2.0f, 14.8716f, 3.0964f, 13.7657f, 4.4489f, 13.7657f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                moveTo(16.2849f, 13.7657f)
                horizontalLineTo(19.5515f)
                curveTo(20.903f, 13.7657f, 21.9993f, 14.8716f, 21.9993f, 16.2369f)
                verticalLineTo(19.5298f)
                curveTo(21.9993f, 20.8941f, 20.903f, 22.0f, 19.5515f, 22.0f)
                horizontalLineTo(16.2849f)
                curveTo(14.9323f, 22.0f, 13.8359f, 20.8941f, 13.8359f, 19.5298f)
                verticalLineTo(16.2369f)
                curveTo(13.8359f, 14.8716f, 14.9323f, 13.7657f, 16.2849f, 13.7657f)
                close()
            }
        }
        .build()
        return _menu!!
    }

private var _menu: ImageVector? = null
