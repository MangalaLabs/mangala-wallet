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

public val MangalaWalletPack.MalaysianRinggit: ImageVector
    get() {
        if (malaysian_ringgit != null) {
            return malaysian_ringgit!!
        }
        malaysian_ringgit = Builder(name = "Malaysian ringgit", defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF16AF80)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(12.0f, 0.0f)
                lineTo(12.0f, 0.0f)
                arcTo(12.0f, 12.0f, 0.0f, false, true, 24.0f, 12.0f)
                lineTo(24.0f, 12.0f)
                arcTo(12.0f, 12.0f, 0.0f, false, true, 12.0f, 24.0f)
                lineTo(12.0f, 24.0f)
                arcTo(12.0f, 12.0f, 0.0f, false, true, 0.0f, 12.0f)
                lineTo(0.0f, 12.0f)
                arcTo(12.0f, 12.0f, 0.0f, false, true, 12.0f, 0.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(1.799f, 8.7273f)
                horizontalLineTo(3.1307f)
                lineTo(5.446f, 14.3807f)
                horizontalLineTo(5.5313f)
                lineTo(7.8466f, 8.7273f)
                horizontalLineTo(9.1783f)
                verticalLineTo(16.0f)
                horizontalLineTo(8.1342f)
                verticalLineTo(10.7372f)
                horizontalLineTo(8.0668f)
                lineTo(5.9219f, 15.9893f)
                horizontalLineTo(5.0554f)
                lineTo(2.9105f, 10.7337f)
                horizontalLineTo(2.843f)
                verticalLineTo(16.0f)
                horizontalLineTo(1.799f)
                verticalLineTo(8.7273f)
                close()
                moveTo(9.8125f, 8.7273f)
                horizontalLineTo(11.059f)
                lineTo(12.9588f, 12.0334f)
                horizontalLineTo(13.037f)
                lineTo(14.9368f, 8.7273f)
                horizontalLineTo(16.1833f)
                lineTo(13.5448f, 13.1449f)
                verticalLineTo(16.0f)
                horizontalLineTo(12.451f)
                verticalLineTo(13.1449f)
                lineTo(9.8125f, 8.7273f)
                close()
                moveTo(16.8193f, 16.0f)
                verticalLineTo(8.7273f)
                horizontalLineTo(19.4116f)
                curveTo(19.9751f, 8.7273f, 20.4427f, 8.8243f, 20.8143f, 9.0185f)
                curveTo(21.1884f, 9.2126f, 21.4678f, 9.4813f, 21.6524f, 9.8246f)
                curveTo(21.8371f, 10.1655f, 21.9294f, 10.5597f, 21.9294f, 11.0071f)
                curveTo(21.9294f, 11.4522f, 21.8359f, 11.844f, 21.6489f, 12.1825f)
                curveTo(21.4642f, 12.5187f, 21.1848f, 12.7803f, 20.8108f, 12.9673f)
                curveTo(20.4391f, 13.1544f, 19.9715f, 13.2479f, 19.4081f, 13.2479f)
                horizontalLineTo(17.4443f)
                verticalLineTo(12.3033f)
                horizontalLineTo(19.3087f)
                curveTo(19.6638f, 12.3033f, 19.9526f, 12.2524f, 20.1751f, 12.1506f)
                curveTo(20.4f, 12.0488f, 20.5646f, 11.9008f, 20.6687f, 11.7067f)
                curveTo(20.7729f, 11.5125f, 20.825f, 11.2794f, 20.825f, 11.0071f)
                curveTo(20.825f, 10.7325f, 20.7717f, 10.4946f, 20.6652f, 10.2933f)
                curveTo(20.561f, 10.0921f, 20.3965f, 9.9382f, 20.1716f, 9.8317f)
                curveTo(19.9491f, 9.7228f, 19.6567f, 9.6683f, 19.2945f, 9.6683f)
                horizontalLineTo(17.9166f)
                verticalLineTo(16.0f)
                horizontalLineTo(16.8193f)
                close()
                moveTo(20.4095f, 12.7188f)
                lineTo(22.2064f, 16.0f)
                horizontalLineTo(20.9564f)
                lineTo(19.195f, 12.7188f)
                horizontalLineTo(20.4095f)
                close()
            }
        }
        .build()
        return malaysian_ringgit!!
    }

private var malaysian_ringgit: ImageVector? = null
