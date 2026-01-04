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

public val MangalaWalletPack.HomeSend: ImageVector
    get() {
        if (_homesend != null) {
            return _homesend!!
        }
        _homesend = Builder(name = "Homesend", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF767676)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                moveTo(16.334f, 2.75f)
                horizontalLineTo(7.665f)
                curveTo(4.645f, 2.75f, 2.75f, 4.889f, 2.75f, 7.916f)
                verticalLineTo(16.084f)
                curveTo(2.75f, 19.111f, 4.635f, 21.25f, 7.665f, 21.25f)
                horizontalLineTo(16.334f)
                curveTo(19.364f, 21.25f, 21.25f, 19.111f, 21.25f, 16.084f)
                verticalLineTo(7.916f)
                curveTo(21.25f, 4.889f, 19.364f, 2.75f, 16.334f, 2.75f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(12.0f, 7.9139f)
                lineTo(12.0f, 16.0859f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(8.2521f, 11.6777f)
                lineTo(12.0f, 7.9137f)
                lineTo(15.748f, 11.6777f)
            }
        }
        .build()
        return _homesend!!
    }

private var _homesend: ImageVector? = null
