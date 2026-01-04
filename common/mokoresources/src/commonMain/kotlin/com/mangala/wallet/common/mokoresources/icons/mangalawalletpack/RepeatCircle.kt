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

public val MangalaWalletPack.RepeatCircle: ImageVector
    get() {
        if ( repeatCircle != null) {
            return repeatCircle!!
        }
        repeatCircle = Builder(name = "Repeat-circle", defaultWidth = 18.0.dp, defaultHeight =
                18.0.dp, viewportWidth = 18.0f, viewportHeight = 18.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFFC642D)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(5.625f, 6.2554f)
                horizontalLineTo(11.175f)
                curveTo(11.8425f, 6.2554f, 12.375f, 6.7954f, 12.375f, 7.4553f)
                verticalLineTo(8.7829f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFFC642D)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(6.8925f, 4.9951f)
                lineTo(5.625f, 6.2552f)
                lineTo(6.8925f, 7.5227f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFFC642D)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(12.375f, 11.7453f)
                horizontalLineTo(6.825f)
                curveTo(6.1575f, 11.7453f, 5.625f, 11.2053f, 5.625f, 10.5453f)
                verticalLineTo(9.2178f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFFC642D)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(11.1074f, 13.0051f)
                lineTo(12.3749f, 11.745f)
                lineTo(11.1074f, 10.4775f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFFC642D)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(9.0f, 16.5f)
                curveTo(13.1421f, 16.5f, 16.5f, 13.1421f, 16.5f, 9.0f)
                curveTo(16.5f, 4.8579f, 13.1421f, 1.5f, 9.0f, 1.5f)
                curveTo(4.8579f, 1.5f, 1.5f, 4.8579f, 1.5f, 9.0f)
                curveTo(1.5f, 13.1421f, 4.8579f, 16.5f, 9.0f, 16.5f)
                close()
            }
        }
        .build()
        return repeatCircle!!
    }

private var repeatCircle: ImageVector? = null
