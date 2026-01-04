package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import kotlin.Unit

public val MangalaWalletPack.IcSave: ImageVector
    get() {
        if (_icSave != null) {
            return _icSave!!
        }
        _icSave = Builder(name = "IcSave", defaultWidth = 17.0.dp, defaultHeight = 16.0.dp,
                viewportWidth = 17.0f, viewportHeight = 16.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF262626)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(11.46f, 5.934f)
                curveTo(13.86f, 6.14f, 14.84f, 7.374f, 14.84f, 10.074f)
                verticalLineTo(10.16f)
                curveTo(14.84f, 13.14f, 13.647f, 14.334f, 10.667f, 14.334f)
                horizontalLineTo(6.327f)
                curveTo(3.347f, 14.334f, 2.153f, 13.14f, 2.153f, 10.16f)
                verticalLineTo(10.074f)
                curveTo(2.153f, 7.394f, 3.12f, 6.16f, 5.48f, 5.94f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF262626)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(8.5f, 1.333f)
                verticalLineTo(9.92f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF262626)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(10.733f, 8.434f)
                lineTo(8.5f, 10.667f)
                lineTo(6.267f, 8.434f)
            }
        }
        .build()
        return _icSave!!
    }

private var _icSave: ImageVector? = null
