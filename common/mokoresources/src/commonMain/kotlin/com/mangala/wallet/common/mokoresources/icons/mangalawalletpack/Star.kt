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

public val MangalaWalletPack.Star: ImageVector
    get() {
        if (_star != null) {
            return _star!!
        }
        _star = Builder(name = "Star", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            group {
                path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFffffff)),
                        strokeLineWidth = 1.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                        strokeLineMiter = 4.0f, pathFillType = NonZero) {
                    moveTo(23.4617f, 9.3601f)
                    lineTo(23.4619f, 9.3607f)
                    curveTo(23.5581f, 9.6572f, 23.4688f, 9.9806f, 23.2341f, 10.186f)
                    lineTo(23.2337f, 10.1863f)
                    lineTo(17.9977f, 14.7774f)
                    lineTo(17.7738f, 14.9737f)
                    lineTo(17.8397f, 15.2641f)
                    lineTo(19.3836f, 22.0651f)
                    curveTo(19.4526f, 22.3694f, 19.3345f, 22.6836f, 19.0818f, 22.8667f)
                    lineTo(19.0818f, 22.8668f)
                    curveTo(18.8297f, 23.0495f, 18.4957f, 23.0636f, 18.2309f, 22.9043f)
                    lineTo(18.2298f, 22.9036f)
                    lineTo(12.2567f, 19.3324f)
                    lineTo(12.0002f, 19.179f)
                    lineTo(11.7436f, 19.3324f)
                    lineTo(5.7684f, 22.9036f)
                    lineTo(5.7681f, 22.9037f)
                    curveTo(5.6436f, 22.9782f, 5.5072f, 23.0148f, 5.371f, 23.0148f)
                    curveTo(5.2125f, 23.0148f, 5.0541f, 22.9655f, 4.9167f, 22.8663f)
                    curveTo(4.6651f, 22.6844f, 4.5465f, 22.3708f, 4.6155f, 22.0651f)
                    curveTo(4.6155f, 22.065f, 4.6155f, 22.0649f, 4.6156f, 22.0648f)
                    lineTo(6.1594f, 15.2641f)
                    lineTo(6.2253f, 14.9738f)
                    lineTo(6.0015f, 14.7775f)
                    lineTo(0.7655f, 10.1854f)
                    lineTo(0.7645f, 10.1846f)
                    curveTo(0.5312f, 9.981f, 0.4419f, 9.6574f, 0.5383f, 9.361f)
                    curveTo(0.6346f, 9.0648f, 0.8956f, 8.856f, 1.2054f, 8.8272f)
                    curveTo(1.2055f, 8.8272f, 1.2057f, 8.8271f, 1.2058f, 8.8271f)
                    lineTo(8.1332f, 8.1982f)
                    lineTo(8.4305f, 8.1712f)
                    lineTo(8.5478f, 7.8967f)
                    lineTo(11.2866f, 1.4872f)
                    curveTo(11.2867f, 1.4871f, 11.2867f, 1.487f, 11.2868f, 1.4868f)
                    curveTo(11.4104f, 1.199f, 11.6901f, 1.015f, 12.0001f, 1.015f)
                    curveTo(12.3103f, 1.015f, 12.59f, 1.1991f, 12.7135f, 1.4859f)
                    curveTo(12.7136f, 1.4861f, 12.7137f, 1.4863f, 12.7138f, 1.4865f)
                    lineTo(15.4525f, 7.8967f)
                    lineTo(15.5698f, 8.1712f)
                    lineTo(15.8671f, 8.1982f)
                    lineTo(22.7941f, 8.8272f)
                    lineTo(22.7944f, 8.8272f)
                    curveTo(23.1045f, 8.8552f, 23.3662f, 9.0646f, 23.4617f, 9.3601f)
                    close()
                }
            }
        }
        .build()
        return _star!!
    }

private var _star: ImageVector? = null
