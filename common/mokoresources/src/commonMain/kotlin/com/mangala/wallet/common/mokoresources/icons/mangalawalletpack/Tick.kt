package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.Tick: ImageVector
    get() {
        if (_tick != null) {
            return _tick!!
        }
        _tick = Builder(name = "Tick", defaultWidth = 10.0.dp, defaultHeight = 10.0.dp,
                viewportWidth = 10.0f, viewportHeight = 10.0f).apply {
            path(fill = SolidColor(Color(0xFF00A699)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(5.0f, 0.0f)
                lineTo(5.0f, 0.0f)
                arcTo(5.0f, 5.0f, 0.0f, false, true, 10.0f, 5.0f)
                lineTo(10.0f, 5.0f)
                arcTo(5.0f, 5.0f, 0.0f, false, true, 5.0f, 10.0f)
                lineTo(5.0f, 10.0f)
                arcTo(5.0f, 5.0f, 0.0f, false, true, 0.0f, 5.0f)
                lineTo(0.0f, 5.0f)
                arcTo(5.0f, 5.0f, 0.0f, false, true, 5.0f, 0.0f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFffffff)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(2.5f, 4.9936f)
                lineTo(4.0865f, 6.25f)
                lineTo(7.5f, 3.75f)
            }
        }
        .build()
        return _tick!!
    }

private var _tick: ImageVector? = null
