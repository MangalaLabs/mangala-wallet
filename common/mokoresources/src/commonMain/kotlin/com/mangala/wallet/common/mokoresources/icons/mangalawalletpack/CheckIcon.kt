package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.CheckIcon: ImageVector
    get() {
        if (`check` != null) {
            return `check`!!
        }
        `check` = Builder(name = "Check Icon ", defaultWidth = 14.0.dp,
            defaultHeight = 14.0.dp, viewportWidth = 14.0f, viewportHeight = 14.0f).apply {
            path(fill = SolidColor(Color(0xFF262626)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero) {
                moveTo(7.0f, 0.0f)
                curveTo(5.616f, 0.0f, 4.262f, 0.411f, 3.111f, 1.18f)
                curveTo(1.96f, 1.949f, 1.063f, 3.042f, 0.533f, 4.321f)
                curveTo(0.003f, 5.6f, -0.136f, 7.008f, 0.135f, 8.366f)
                curveTo(0.405f, 9.724f, 1.071f, 10.971f, 2.05f, 11.95f)
                curveTo(3.029f, 12.929f, 4.277f, 13.595f, 5.634f, 13.866f)
                curveTo(6.992f, 14.136f, 8.4f, 13.997f, 9.679f, 13.467f)
                curveTo(10.958f, 12.937f, 12.051f, 12.04f, 12.82f, 10.889f)
                curveTo(13.59f, 9.738f, 14.0f, 8.384f, 14.0f, 7.0f)
                curveTo(13.998f, 5.144f, 13.26f, 3.365f, 11.948f, 2.052f)
                curveTo(10.635f, 0.74f, 8.856f, 0.002f, 7.0f, 0.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero) {
                moveTo(6.305f, 9.537f)
                lineTo(10.074f, 5.767f)
                curveTo(10.124f, 5.717f, 10.164f, 5.658f, 10.191f, 5.593f)
                curveTo(10.218f, 5.527f, 10.232f, 5.457f, 10.232f, 5.386f)
                curveTo(10.232f, 5.316f, 10.218f, 5.246f, 10.191f, 5.18f)
                curveTo(10.164f, 5.115f, 10.124f, 5.055f, 10.074f, 5.005f)
                curveTo(10.024f, 4.955f, 9.964f, 4.916f, 9.899f, 4.889f)
                curveTo(9.834f, 4.862f, 9.764f, 4.848f, 9.693f, 4.848f)
                curveTo(9.622f, 4.848f, 9.552f, 4.862f, 9.487f, 4.889f)
                curveTo(9.421f, 4.916f, 9.362f, 4.955f, 9.312f, 5.005f)
                lineTo(5.924f, 8.394f)
                lineTo(4.689f, 7.159f)
                curveTo(4.588f, 7.058f, 4.451f, 7.002f, 4.308f, 7.002f)
                curveTo(4.165f, 7.002f, 4.028f, 7.058f, 3.927f, 7.159f)
                curveTo(3.826f, 7.26f, 3.77f, 7.397f, 3.77f, 7.54f)
                curveTo(3.77f, 7.683f, 3.826f, 7.82f, 3.927f, 7.921f)
                lineTo(5.543f, 9.537f)
                curveTo(5.593f, 9.587f, 5.652f, 9.626f, 5.717f, 9.653f)
                curveTo(5.783f, 9.681f, 5.853f, 9.695f, 5.924f, 9.695f)
                curveTo(5.994f, 9.695f, 6.065f, 9.681f, 6.13f, 9.653f)
                curveTo(6.195f, 9.626f, 6.255f, 9.587f, 6.305f, 9.537f)
                close()
            }
        }
            .build()
        return `check`!!
    }

private var `check`: ImageVector? = null
