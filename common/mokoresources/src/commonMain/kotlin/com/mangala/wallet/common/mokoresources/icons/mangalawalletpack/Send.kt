package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.Send: ImageVector
    get() {
        if (_send != null) {
            return _send!!
        }
        _send = Builder(name = "Send", defaultWidth = 18.0.dp, defaultHeight = 18.0.dp,
                viewportWidth = 18.0f, viewportHeight = 18.0f).apply {
            path(fill = SolidColor(Color(0xFF383838)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(16.8555f, 1.1484f)
                curveTo(16.4389f, 0.7226f, 15.8222f, 0.5651f, 15.2472f, 0.7318f)
                lineTo(1.8389f, 4.6059f)
                curveTo(1.2322f, 4.7743f, 0.8022f, 5.2551f, 0.6864f, 5.8651f)
                curveTo(0.568f, 6.4868f, 0.9814f, 7.2768f, 1.5214f, 7.6068f)
                lineTo(5.7139f, 10.1668f)
                curveTo(6.1439f, 10.4301f, 6.6989f, 10.3643f, 7.0547f, 10.0076f)
                lineTo(11.8555f, 5.2067f)
                curveTo(12.0972f, 4.9559f, 12.4972f, 4.9559f, 12.7389f, 5.2067f)
                curveTo(12.9805f, 5.4476f, 12.9805f, 5.8401f, 12.7389f, 6.0901f)
                lineTo(7.9297f, 10.8909f)
                curveTo(7.573f, 11.2476f, 7.5064f, 11.8009f, 7.7689f, 12.2318f)
                lineTo(10.3305f, 16.4401f)
                curveTo(10.6305f, 16.9393f, 11.1472f, 17.2234f, 11.7139f, 17.2234f)
                curveTo(11.7805f, 17.2234f, 11.8555f, 17.2234f, 11.9222f, 17.2143f)
                curveTo(12.5722f, 17.1318f, 13.0889f, 16.6893f, 13.2805f, 16.0643f)
                lineTo(17.2555f, 2.7568f)
                curveTo(17.4305f, 2.1901f, 17.2722f, 1.5734f, 16.8555f, 1.1484f)
                close()
            }
            path(fill = SolidColor(Color(0xFF383838)), stroke = null, fillAlpha = 0.4f, strokeAlpha
                    = 0.4f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                moveTo(1.5076f, 13.0062f)
                curveTo(1.3476f, 13.0062f, 1.1876f, 12.9453f, 1.0659f, 12.8228f)
                curveTo(0.8218f, 12.5787f, 0.8218f, 12.1837f, 1.0659f, 11.9395f)
                lineTo(2.2034f, 10.8012f)
                curveTo(2.4476f, 10.5578f, 2.8434f, 10.5578f, 3.0876f, 10.8012f)
                curveTo(3.3309f, 11.0453f, 3.3309f, 11.4412f, 3.0876f, 11.6853f)
                lineTo(1.9493f, 12.8228f)
                curveTo(1.8276f, 12.9453f, 1.6676f, 13.0062f, 1.5076f, 13.0062f)
                close()
                moveTo(4.6419f, 13.9998f)
                curveTo(4.4819f, 13.9998f, 4.3219f, 13.939f, 4.2003f, 13.8165f)
                curveTo(3.9561f, 13.5723f, 3.9561f, 13.1773f, 4.2003f, 12.9332f)
                lineTo(5.3378f, 11.7948f)
                curveTo(5.5819f, 11.5515f, 5.9778f, 11.5515f, 6.2219f, 11.7948f)
                curveTo(6.4653f, 12.039f, 6.4653f, 12.4348f, 6.2219f, 12.679f)
                lineTo(5.0836f, 13.8165f)
                curveTo(4.9619f, 13.939f, 4.8019f, 13.9998f, 4.6419f, 13.9998f)
                close()
                moveTo(4.8534f, 16.9732f)
                curveTo(4.975f, 17.0957f, 5.135f, 17.1565f, 5.295f, 17.1565f)
                curveTo(5.455f, 17.1565f, 5.615f, 17.0957f, 5.7367f, 16.9732f)
                lineTo(6.875f, 15.8357f)
                curveTo(7.1184f, 15.5915f, 7.1184f, 15.1957f, 6.875f, 14.9515f)
                curveTo(6.6308f, 14.7082f, 6.235f, 14.7082f, 5.9908f, 14.9515f)
                lineTo(4.8534f, 16.0898f)
                curveTo(4.6092f, 16.334f, 4.6092f, 16.729f, 4.8534f, 16.9732f)
                close()
            }
        }
        .build()
        return _send!!
    }

private var _send: ImageVector? = null
