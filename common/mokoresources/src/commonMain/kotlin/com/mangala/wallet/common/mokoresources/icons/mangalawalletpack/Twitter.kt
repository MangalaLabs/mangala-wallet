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

public val MangalaWalletPack.Twitter: ImageVector
    get() {
        if (twitter != null) {
            return twitter!!
        }
        twitter = Builder(name = "Twitter 1", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            group {
                path(fill = SolidColor(Color(0xFF55ACEE)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(12.0001f, 23.9998f)
                    curveTo(18.6275f, 23.9998f, 24.0f, 18.6273f, 24.0f, 11.9999f)
                    curveTo(24.0f, 5.3725f, 18.6275f, 0.0f, 12.0001f, 0.0f)
                    curveTo(5.3728f, 0.0f, 2.0E-4f, 5.3725f, 2.0E-4f, 11.9999f)
                    curveTo(2.0E-4f, 18.6273f, 5.3728f, 23.9998f, 12.0001f, 23.9998f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFF1F2F2)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(19.3504f, 8.6244f)
                    curveTo(18.8362f, 8.8524f, 18.283f, 9.0066f, 17.7029f, 9.0755f)
                    curveTo(18.2952f, 8.7206f, 18.7498f, 8.1591f, 18.9643f, 7.4889f)
                    curveTo(18.4101f, 7.8177f, 17.796f, 8.0562f, 17.1429f, 8.1848f)
                    curveTo(16.6197f, 7.6275f, 15.8742f, 7.2793f, 15.0487f, 7.2793f)
                    curveTo(13.4649f, 7.2793f, 12.1804f, 8.5638f, 12.1804f, 10.1476f)
                    curveTo(12.1804f, 10.3724f, 12.2059f, 10.5913f, 12.2551f, 10.8013f)
                    curveTo(9.8713f, 10.6817f, 7.7576f, 9.5399f, 6.3428f, 7.804f)
                    curveTo(6.096f, 8.2276f, 5.9544f, 8.7206f, 5.9544f, 9.2462f)
                    curveTo(5.9544f, 10.2411f, 6.4611f, 11.1194f, 7.2303f, 11.6336f)
                    curveTo(6.7604f, 11.6189f, 6.3178f, 11.4899f, 5.9313f, 11.2745f)
                    curveTo(5.931f, 11.2867f, 5.931f, 11.2989f, 5.931f, 11.3108f)
                    curveTo(5.931f, 12.7006f, 6.9202f, 13.8598f, 8.2321f, 14.1231f)
                    curveTo(7.9916f, 14.189f, 7.7377f, 14.2239f, 7.4766f, 14.2239f)
                    curveTo(7.2913f, 14.2239f, 7.1118f, 14.2061f, 6.9369f, 14.1727f)
                    curveTo(7.3018f, 15.312f, 8.3609f, 16.1414f, 9.6163f, 16.1647f)
                    curveTo(8.6344f, 16.9341f, 7.3978f, 17.3925f, 6.0536f, 17.3925f)
                    curveTo(5.8226f, 17.3925f, 5.5937f, 17.379f, 5.3698f, 17.3523f)
                    curveTo(6.6385f, 18.1664f, 8.1465f, 18.6411f, 9.7662f, 18.6411f)
                    curveTo(15.0421f, 18.6411f, 17.9273f, 14.2705f, 17.9273f, 10.4798f)
                    curveTo(17.9273f, 10.3555f, 17.9245f, 10.2317f, 17.919f, 10.1089f)
                    curveTo(18.4798f, 9.7046f, 18.9661f, 9.1996f, 19.3504f, 8.6244f)
                    close()
                }
            }
        }
        .build()
        return twitter!!
    }

private var twitter: ImageVector? = null
