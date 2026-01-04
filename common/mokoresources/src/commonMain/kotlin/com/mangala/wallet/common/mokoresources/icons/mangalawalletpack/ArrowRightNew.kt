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

public val MangalaWalletPack.ArrowRightNew: ImageVector
    get() {
        if (_arrowRightNew != null) {
            return _arrowRightNew!!
        }
        _arrowRightNew = Builder(name = "ArrowRightNew", defaultWidth = 20.0.dp, defaultHeight =
                20.0.dp, viewportWidth = 20.0f, viewportHeight = 20.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color.Unspecified),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(7.425f, 16.6f)
                lineTo(12.858f, 11.167f)
                curveTo(13.5f, 10.525f, 13.5f, 9.475f, 12.858f, 8.834f)
                lineTo(7.425f, 3.4f)
            }
        }
        .build()
        return _arrowRightNew!!
    }

private var _arrowRightNew: ImageVector? = null