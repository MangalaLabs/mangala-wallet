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

public val MangalaWalletPack.Paste: ImageVector
    get() {
        if (_paste != null) {
            return _paste!!
        }
        _paste = Builder(name = "Paste", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF383838)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(20.0f, 8.25f)
                verticalLineTo(18.0f)
                curveTo(20.0f, 21.0f, 18.21f, 22.0f, 16.0f, 22.0f)
                horizontalLineTo(8.0f)
                curveTo(5.79f, 22.0f, 4.0f, 21.0f, 4.0f, 18.0f)
                verticalLineTo(8.25f)
                curveTo(4.0f, 5.0f, 5.79f, 4.25f, 8.0f, 4.25f)
                curveTo(8.0f, 4.87f, 8.25f, 5.43f, 8.66f, 5.84f)
                curveTo(9.07f, 6.25f, 9.63f, 6.5f, 10.25f, 6.5f)
                horizontalLineTo(13.75f)
                curveTo(14.99f, 6.5f, 16.0f, 5.49f, 16.0f, 4.25f)
                curveTo(18.21f, 4.25f, 20.0f, 5.0f, 20.0f, 8.25f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF383838)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(16.0f, 4.25f)
                curveTo(16.0f, 5.49f, 14.99f, 6.5f, 13.75f, 6.5f)
                horizontalLineTo(10.25f)
                curveTo(9.63f, 6.5f, 9.07f, 6.25f, 8.66f, 5.84f)
                curveTo(8.25f, 5.43f, 8.0f, 4.87f, 8.0f, 4.25f)
                curveTo(8.0f, 3.01f, 9.01f, 2.0f, 10.25f, 2.0f)
                horizontalLineTo(13.75f)
                curveTo(14.37f, 2.0f, 14.93f, 2.25f, 15.34f, 2.66f)
                curveTo(15.75f, 3.07f, 16.0f, 3.63f, 16.0f, 4.25f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF383838)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(8.0f, 13.0f)
                horizontalLineTo(12.0f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF383838)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(8.0f, 17.0f)
                horizontalLineTo(16.0f)
            }
        }
        .build()
        return _paste!!
    }

private var _paste: ImageVector? = null
