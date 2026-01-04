package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
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

public val MangalaWalletPack.Telegram: ImageVector
    get() {
        if (telegram != null) {
            return telegram!!
        }
        telegram = Builder(name = "Telegram 1", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            group {
                path(fill = linearGradient(0.0f to Color(0xFF2AABEE), 1.0f to Color(0xFF229ED9),
                        start = Offset(12.0f,0.0f), end = Offset(12.0f,23.8125f)), stroke = null,
                        strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                        strokeLineMiter = 4.0f, pathFillType = NonZero) {
                    moveTo(12.0f, 24.0f)
                    curveTo(18.6274f, 24.0f, 24.0f, 18.6274f, 24.0f, 12.0f)
                    curveTo(24.0f, 5.3726f, 18.6274f, 0.0f, 12.0f, 0.0f)
                    curveTo(5.3726f, 0.0f, 0.0f, 5.3726f, 0.0f, 12.0f)
                    curveTo(0.0f, 18.6274f, 5.3726f, 24.0f, 12.0f, 24.0f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = EvenOdd) {
                    moveTo(5.4319f, 11.873f)
                    curveTo(8.9301f, 10.3488f, 11.2629f, 9.344f, 12.43f, 8.8586f)
                    curveTo(15.7625f, 7.4725f, 16.455f, 7.2317f, 16.9063f, 7.2237f)
                    curveTo(17.0056f, 7.222f, 17.2276f, 7.2466f, 17.3713f, 7.3633f)
                    curveTo(17.5846f, 7.5363f, 17.5858f, 7.912f, 17.5622f, 8.1606f)
                    curveTo(17.3816f, 10.0581f, 16.6002f, 14.6628f, 16.2026f, 16.788f)
                    curveTo(16.0344f, 17.6872f, 15.7032f, 17.9888f, 15.3825f, 18.0183f)
                    curveTo(14.6857f, 18.0824f, 14.1565f, 17.5577f, 13.4816f, 17.1153f)
                    curveTo(12.4254f, 16.423f, 11.8287f, 15.992f, 10.8035f, 15.3165f)
                    curveTo(9.6188f, 14.5357f, 10.3868f, 14.1066f, 11.062f, 13.4053f)
                    curveTo(11.2387f, 13.2218f, 14.3091f, 10.429f, 14.3685f, 10.1757f)
                    curveTo(14.376f, 10.144f, 14.3829f, 10.0259f, 14.3127f, 9.9635f)
                    curveTo(14.2425f, 9.9012f, 14.139f, 9.9225f, 14.0643f, 9.9394f)
                    curveTo(13.9583f, 9.9635f, 12.2711f, 11.0787f, 9.0026f, 13.285f)
                    curveTo(8.5237f, 13.6139f, 8.0899f, 13.7741f, 7.7013f, 13.7657f)
                    curveTo(7.2728f, 13.7565f, 6.4486f, 13.5234f, 5.8359f, 13.3243f)
                    curveTo(5.0844f, 13.08f, 4.4871f, 12.9508f, 4.5392f, 12.536f)
                    curveTo(4.5663f, 12.3198f, 4.8638f, 12.0989f, 5.4319f, 11.873f)
                    close()
                }
            }
        }
        .build()
        return telegram!!
    }

private var telegram: ImageVector? = null
