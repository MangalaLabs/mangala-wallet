package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.Check: ImageVector
    get() {
        if (_check != null) {
            return _check!!
        }
        _check = Builder(name = "Check", defaultWidth = 8.0.dp, defaultHeight = 7.0.dp,
            viewportWidth = 8.0f, viewportHeight = 7.0f).apply {
            path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = EvenOdd) {
                moveTo(2.78133f, 4.86259f)
                lineTo(7.31067f, 0.33325f)
                lineTo(8.0f, 1.02258f)
                lineTo(2.78133f, 6.24125f)
                lineTo(0.0f, 3.45925f)
                lineTo(0.68933f, 2.76992f)
                lineTo(2.78133f, 4.86259f)
                close()
            }
        }
            .build()
        return _check!!
    }

private var _check: ImageVector? = null
