package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.ArrowRight: ImageVector
    get() {
        if (_arrowRight != null) {
            return _arrowRight!!
        }
        _arrowRight = ImageVector.Builder(
            name = "ArrowRight", defaultWidth = 18.0.dp, defaultHeight = 18.0.dp,
            viewportWidth = 18.0f, viewportHeight = 18.0f
        ).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF767676)),
                strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(6.75f, 13.5f)
                lineTo(11.25f, 9.0f)
                lineTo(6.75f, 4.5f)
            }
        }
            .build()
        return _arrowRight!!
    }

private var _arrowRight: ImageVector? = null