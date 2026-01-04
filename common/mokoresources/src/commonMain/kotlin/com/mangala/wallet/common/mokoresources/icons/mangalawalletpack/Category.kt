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

public val MangalaWalletPack.Category: ImageVector
    get() {
        if (_category != null) {
            return _category!!
        }
        _category = Builder(name = "Category", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                moveTo(16.2853f, 2.0f)
                horizontalLineTo(19.5519f)
                curveTo(20.9035f, 2.0f, 21.9998f, 3.1059f, 21.9998f, 4.4702f)
                verticalLineTo(7.7641f)
                curveTo(21.9998f, 9.1273f, 20.9035f, 10.2343f, 19.5519f, 10.2343f)
                horizontalLineTo(16.2853f)
                curveTo(14.9328f, 10.2343f, 13.8364f, 9.1273f, 13.8364f, 7.7641f)
                verticalLineTo(4.4702f)
                curveTo(13.8364f, 3.1059f, 14.9328f, 2.0f, 16.2853f, 2.0f)
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
                moveTo(4.4489f, 13.7656f)
                horizontalLineTo(7.7145f)
                curveTo(9.067f, 13.7656f, 10.1634f, 14.8715f, 10.1634f, 16.2368f)
                verticalLineTo(19.5297f)
                curveTo(10.1634f, 20.894f, 9.067f, 21.9999f, 7.7145f, 21.9999f)
                horizontalLineTo(4.4489f)
                curveTo(3.0964f, 21.9999f, 2.0f, 20.894f, 2.0f, 19.5297f)
                verticalLineTo(16.2368f)
                curveTo(2.0f, 14.8715f, 3.0964f, 13.7656f, 4.4489f, 13.7656f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                moveTo(16.2853f, 13.7656f)
                horizontalLineTo(19.5519f)
                curveTo(20.9035f, 13.7656f, 21.9998f, 14.8715f, 21.9998f, 16.2368f)
                verticalLineTo(19.5297f)
                curveTo(21.9998f, 20.894f, 20.9035f, 21.9999f, 19.5519f, 21.9999f)
                horizontalLineTo(16.2853f)
                curveTo(14.9328f, 21.9999f, 13.8364f, 20.894f, 13.8364f, 19.5297f)
                verticalLineTo(16.2368f)
                curveTo(13.8364f, 14.8715f, 14.9328f, 13.7656f, 16.2853f, 13.7656f)
                close()
            }
        }
        .build()
        return _category!!
    }

private var _category: ImageVector? = null
