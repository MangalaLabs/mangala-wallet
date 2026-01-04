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

public val MangalaWalletPack.InfoCircle: ImageVector
    get() {
        if (infoCircle != null) {
            return infoCircle!!
        }
        infoCircle = Builder(name = "Info-circle", defaultWidth = 20.0.dp, defaultHeight =
                20.0.dp, viewportWidth = 20.0f, viewportHeight = 20.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF767676)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(10.0f, 18.3334f)
                curveTo(14.5833f, 18.3334f, 18.3333f, 14.5834f, 18.3333f, 10.0f)
                curveTo(18.3333f, 5.4167f, 14.5833f, 1.6667f, 10.0f, 1.6667f)
                curveTo(5.4166f, 1.6667f, 1.6666f, 5.4167f, 1.6666f, 10.0f)
                curveTo(1.6666f, 14.5834f, 5.4166f, 18.3334f, 10.0f, 18.3334f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF767676)),
                    strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(9.9954f, 6.6667f)
                horizontalLineTo(10.0028f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF767676)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(10.0f, 9.1667f)
                verticalLineTo(13.3334f)
            }
        }
        .build()
        return infoCircle!!
    }

private var infoCircle: ImageVector? = null
