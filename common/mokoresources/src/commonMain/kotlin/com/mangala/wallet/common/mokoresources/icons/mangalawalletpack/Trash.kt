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

public val MangalaWalletPack.Trash: ImageVector
    get() {
        if (_trash != null) {
            return _trash!!
        }
        _trash = Builder(name = "Trash", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFFF5A5F)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(21.0f, 5.98f)
                curveTo(17.67f, 5.65f, 14.32f, 5.48f, 10.98f, 5.48f)
                curveTo(9.0f, 5.48f, 7.02f, 5.58f, 5.04f, 5.78f)
                lineTo(3.0f, 5.98f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFFF5A5F)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(8.5f, 4.97f)
                lineTo(8.72f, 3.66f)
                curveTo(8.88f, 2.71f, 9.0f, 2.0f, 10.69f, 2.0f)
                horizontalLineTo(13.31f)
                curveTo(15.0f, 2.0f, 15.13f, 2.75f, 15.28f, 3.67f)
                lineTo(15.5f, 4.97f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFFF5A5F)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(18.8499f, 9.1399f)
                lineTo(18.1999f, 19.2099f)
                curveTo(18.0899f, 20.7799f, 17.9999f, 21.9999f, 15.2099f, 21.9999f)
                horizontalLineTo(8.7899f)
                curveTo(5.9999f, 21.9999f, 5.9099f, 20.7799f, 5.7999f, 19.2099f)
                lineTo(5.1499f, 9.1399f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFFF5A5F)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(10.3301f, 16.5f)
                horizontalLineTo(13.6601f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFFF5A5F)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(9.5f, 12.5f)
                horizontalLineTo(14.5f)
            }
        }
        .build()
        return _trash!!
    }

private var _trash: ImageVector? = null
