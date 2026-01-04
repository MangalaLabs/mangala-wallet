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

public val MangalaWalletPack.Background: ImageVector
    get() {
        if (_background != null) {
            return _background!!
        }
        _background = Builder(name = "Background", defaultWidth = 51.0.dp, defaultHeight = 32.0.dp,
                viewportWidth = 51.0f, viewportHeight = 32.0f).apply {
            path(fill = SolidColor(Color(0xFF00A699)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(15.857f, 0.5922f)
                curveTo(17.597f, 0.4922f, 19.335f, 0.5002f, 21.075f, 0.5002f)
                curveTo(21.087f, 0.5002f, 29.892f, 0.5002f, 29.892f, 0.5002f)
                curveTo(31.666f, 0.5002f, 33.404f, 0.4922f, 35.143f, 0.5922f)
                curveTo(36.724f, 0.6822f, 38.264f, 0.8742f, 39.797f, 1.3032f)
                curveTo(43.024f, 2.2051f, 45.842f, 4.0891f, 47.879f, 6.7601f)
                curveTo(49.904f, 9.4142f, 51.0f, 12.6632f, 51.0f, 15.9992f)
                curveTo(51.0f, 19.3392f, 49.904f, 22.5862f, 47.879f, 25.2402f)
                curveTo(45.842f, 27.9102f, 43.024f, 29.7952f, 39.797f, 30.6972f)
                curveTo(38.264f, 31.1262f, 36.724f, 31.3172f, 35.143f, 31.4082f)
                curveTo(33.404f, 31.5082f, 31.666f, 31.4992f, 29.926f, 31.4992f)
                curveTo(29.914f, 31.4992f, 21.107f, 31.5002f, 21.107f, 31.5002f)
                curveTo(19.335f, 31.4992f, 17.597f, 31.5082f, 15.857f, 31.4082f)
                curveTo(14.277f, 31.3172f, 12.737f, 31.1262f, 11.204f, 30.6972f)
                curveTo(7.977f, 29.7952f, 5.159f, 27.9102f, 3.122f, 25.2402f)
                curveTo(1.097f, 22.5862f, 0.0f, 19.3392f, 0.0f, 16.0002f)
                curveTo(0.0f, 12.6632f, 1.097f, 9.4142f, 3.122f, 6.7601f)
                curveTo(5.159f, 4.0891f, 7.977f, 2.2051f, 11.204f, 1.3032f)
                curveTo(12.737f, 0.8742f, 14.277f, 0.6822f, 15.857f, 0.5922f)
                close()
            }
        }
        .build()
        return _background!!
    }

private var _background: ImageVector? = null
