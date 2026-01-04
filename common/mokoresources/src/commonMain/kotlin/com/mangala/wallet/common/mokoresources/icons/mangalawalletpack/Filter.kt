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

public val MangalaWalletPack.Filter: ImageVector
    get() {
        if (_filter != null) {
            return _filter!!
        }
        _filter = Builder(name = "Filter", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(3.3498f, 2.0f)
                horizontalLineTo(12.2499f)
                curveTo(12.9899f, 2.0f, 13.5999f, 2.61f, 13.5999f, 3.35f)
                verticalLineTo(4.83f)
                curveTo(13.5999f, 5.37f, 13.2599f, 6.04f, 12.9299f, 6.38f)
                lineTo(10.0299f, 8.94f)
                curveTo(9.6299f, 9.28f, 9.3599f, 9.95f, 9.3599f, 10.49f)
                verticalLineTo(13.39f)
                curveTo(9.3599f, 13.79f, 9.0899f, 14.33f, 8.7499f, 14.54f)
                lineTo(7.8099f, 15.15f)
                curveTo(6.9299f, 15.69f, 5.7199f, 15.08f, 5.7199f, 14.0f)
                verticalLineTo(10.43f)
                curveTo(5.7199f, 9.96f, 5.4499f, 9.35f, 5.1799f, 9.01f)
                lineTo(2.6199f, 6.31f)
                curveTo(2.2799f, 5.97f, 2.0099f, 5.37f, 2.0099f, 4.96f)
                verticalLineTo(3.41f)
                curveTo(1.9999f, 2.61f, 2.6098f, 2.0f, 3.3498f, 2.0f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(2.0f, 12.0002f)
                verticalLineTo(15.0002f)
                curveTo(2.0f, 20.0002f, 4.0f, 22.0002f, 9.0f, 22.0002f)
                horizontalLineTo(15.0f)
                curveTo(20.0f, 22.0002f, 22.0f, 20.0002f, 22.0f, 15.0002f)
                verticalLineTo(9.0002f)
                curveTo(22.0f, 5.8802f, 21.22f, 3.9202f, 19.41f, 2.9002f)
                curveTo(18.9f, 2.6102f, 17.88f, 2.3902f, 16.95f, 2.2402f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(13.0f, 13.0f)
                horizontalLineTo(18.0f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(11.0f, 17.0f)
                horizontalLineTo(18.0f)
            }
        }
        .build()
        return _filter!!
    }

private var _filter: ImageVector? = null
