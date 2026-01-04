package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.DropdownSolid: ImageVector
    get() {
        if (_down != null) {
            return _down!!
        }
        _down = Builder(name = "DropdownSolid", defaultWidth = 16.0.dp, defaultHeight = 16.0.dp,
                viewportWidth = 16.0f, viewportHeight = 16.0f).apply {
            group {
                path(fill = SolidColor(Color(0xFF767676)), stroke = null, fillAlpha = 0.9f,
                        strokeAlpha = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                        strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                    moveTo(15.228f, 3.6163f)
                    horizontalLineTo(0.7715f)
                    curveTo(0.0874f, 3.6163f, -0.2611f, 4.4424f, 0.2294f, 4.9329f)
                    lineTo(7.4577f, 12.1612f)
                    curveTo(7.7545f, 12.458f, 8.245f, 12.458f, 8.542f, 12.1612f)
                    lineTo(15.7703f, 4.9329f)
                    curveTo(16.2606f, 4.4424f, 15.9121f, 3.6163f, 15.228f, 3.6163f)
                    close()
                }
            }
        }
        .build()
        return _down!!
    }

private var _down: ImageVector? = null
