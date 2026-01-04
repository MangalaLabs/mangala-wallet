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

public val MangalaWalletPack.ArrowCircleDown: ImageVector
    get() {
        if (arrowCircleDown != null) {
            return arrowCircleDown!!
        }
        arrowCircleDown = Builder(name = "Arrow-down", defaultWidth = 18.0.dp, defaultHeight =
                18.0.dp, viewportWidth = 18.0f, viewportHeight = 18.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF0DA65C)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(9.0f, 16.5f)
                curveTo(13.1421f, 16.5f, 16.5f, 13.1421f, 16.5f, 9.0f)
                curveTo(16.5f, 4.8579f, 13.1421f, 1.5f, 9.0f, 1.5f)
                curveTo(4.8579f, 1.5f, 1.5f, 4.8579f, 1.5f, 9.0f)
                curveTo(1.5f, 13.1421f, 4.8579f, 16.5f, 9.0f, 16.5f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF0DA65C)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(9.0f, 6.375f)
                verticalLineTo(10.875f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF0DA65C)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(6.75f, 9.375f)
                lineTo(9.0f, 11.625f)
                lineTo(11.25f, 9.375f)
            }
        }
        .build()
        return arrowCircleDown!!
    }

private var arrowCircleDown: ImageVector? = null
