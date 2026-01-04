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

public val MangalaWalletPack.German: ImageVector
    get() {
        if (german != null) {
            return german!!
        }
        german = Builder(name = "German", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            group {
                path(fill = SolidColor(Color(0xFFFFDA44)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(0.7466f, 16.1733f)
                    curveTo(2.4419f, 20.7433f, 6.8399f, 24.0f, 11.9999f, 24.0f)
                    curveTo(17.1599f, 24.0f, 21.5579f, 20.7433f, 23.2533f, 16.1733f)
                    lineTo(11.9999f, 15.1307f)
                    lineTo(0.7466f, 16.1733f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF3D3D3D)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(11.9999f, 0.0f)
                    curveTo(6.8399f, 0.0f, 2.4419f, 3.2567f, 0.7466f, 7.8267f)
                    lineTo(11.9999f, 8.8693f)
                    lineTo(23.2533f, 7.826f)
                    curveTo(21.5579f, 3.2567f, 17.1599f, 0.0f, 11.9999f, 0.0f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFD80027)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(0.7467f, 7.8267f)
                    curveTo(0.2515f, 9.1623f, -0.0013f, 10.5755f, 0.0f, 12.0f)
                    curveTo(-0.0014f, 13.4247f, 0.2514f, 14.8382f, 0.7467f, 16.174f)
                    horizontalLineTo(23.2533f)
                    curveTo(23.7486f, 14.8382f, 24.0014f, 13.4247f, 24.0f, 12.0f)
                    curveTo(24.0014f, 10.5753f, 23.7486f, 9.1619f, 23.2533f, 7.826f)
                    lineTo(0.7467f, 7.8267f)
                    close()
                }
            }
        }
        .build()
        return german!!
    }

private var german: ImageVector? = null
