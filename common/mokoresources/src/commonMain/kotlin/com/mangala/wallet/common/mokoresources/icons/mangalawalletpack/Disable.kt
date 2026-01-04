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

public val MangalaWalletPack.Disable: ImageVector
    get() {
        if (disable != null) {
            return disable!!
        }
        disable = Builder(name = "Background (1)", defaultWidth = 51.0.dp, defaultHeight =
                31.0.dp, viewportWidth = 51.0f, viewportHeight = 31.0f).apply {
            path(fill = SolidColor(Color(0xFF787880)), stroke = null, fillAlpha = 0.16f,
                    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                moveTo(15.857f, 0.0922f)
                curveTo(17.597f, -0.0078f, 19.335f, 2.0E-4f, 21.075f, 2.0E-4f)
                curveTo(21.087f, 2.0E-4f, 29.892f, 2.0E-4f, 29.892f, 2.0E-4f)
                curveTo(31.666f, 2.0E-4f, 33.404f, -0.0078f, 35.143f, 0.0922f)
                curveTo(36.724f, 0.1822f, 38.264f, 0.3742f, 39.797f, 0.8032f)
                curveTo(43.024f, 1.7052f, 45.842f, 3.5891f, 47.879f, 6.2601f)
                curveTo(49.904f, 8.9142f, 51.0f, 12.1632f, 51.0f, 15.4992f)
                curveTo(51.0f, 18.8392f, 49.904f, 22.0862f, 47.879f, 24.7402f)
                curveTo(45.842f, 27.4102f, 43.024f, 29.2952f, 39.797f, 30.1972f)
                curveTo(38.264f, 30.6262f, 36.724f, 30.8172f, 35.143f, 30.9082f)
                curveTo(33.404f, 31.0082f, 31.666f, 30.9992f, 29.926f, 30.9992f)
                curveTo(29.914f, 30.9992f, 21.107f, 31.0002f, 21.107f, 31.0002f)
                curveTo(19.335f, 30.9992f, 17.597f, 31.0082f, 15.857f, 30.9082f)
                curveTo(14.277f, 30.8172f, 12.737f, 30.6262f, 11.204f, 30.1972f)
                curveTo(7.977f, 29.2952f, 5.159f, 27.4102f, 3.122f, 24.7402f)
                curveTo(1.097f, 22.0862f, 0.0f, 18.8392f, 0.0f, 15.5002f)
                curveTo(0.0f, 12.1632f, 1.097f, 8.9142f, 3.122f, 6.2601f)
                curveTo(5.159f, 3.5891f, 7.977f, 1.7052f, 11.204f, 0.8032f)
                curveTo(12.737f, 0.3742f, 14.277f, 0.1822f, 15.857f, 0.0922f)
                close()
            }
        }
        .build()
        return disable!!
    }

private var  disable: ImageVector? = null
