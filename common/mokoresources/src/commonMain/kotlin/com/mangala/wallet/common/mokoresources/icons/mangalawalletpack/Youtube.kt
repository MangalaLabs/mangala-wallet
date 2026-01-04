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

public val MangalaWalletPack.Youtube: ImageVector
    get() {
        if (youtube != null) {
            return youtube!!
        }
        youtube = Builder(name = "Youtube 1", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            group {
                path(fill = SolidColor(Color(0xFFFF0000)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(12.0f, 24.0f)
                    curveTo(18.6274f, 24.0f, 24.0f, 18.6274f, 24.0f, 12.0f)
                    curveTo(24.0f, 5.3726f, 18.6274f, 0.0f, 12.0f, 0.0f)
                    curveTo(5.3726f, 0.0f, 0.0f, 5.3726f, 0.0f, 12.0f)
                    curveTo(0.0f, 18.6274f, 5.3726f, 24.0f, 12.0f, 24.0f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(15.9268f, 7.4854f)
                    horizontalLineTo(8.0731f)
                    curveTo(7.7593f, 7.4854f, 7.4485f, 7.5472f, 7.1586f, 7.6673f)
                    curveTo(6.8686f, 7.7875f, 6.6052f, 7.9636f, 6.3833f, 8.1856f)
                    curveTo(6.1615f, 8.4076f, 5.9856f, 8.6712f, 5.8656f, 8.9612f)
                    curveTo(5.7456f, 9.2512f, 5.684f, 9.562f, 5.6842f, 9.8759f)
                    verticalLineTo(14.1232f)
                    curveTo(5.684f, 14.4371f, 5.7456f, 14.7479f, 5.8656f, 15.0379f)
                    curveTo(5.9856f, 15.328f, 6.1615f, 15.5915f, 6.3833f, 15.8135f)
                    curveTo(6.6052f, 16.0355f, 6.8686f, 16.2116f, 7.1586f, 16.3318f)
                    curveTo(7.4485f, 16.4519f, 7.7593f, 16.5138f, 8.0731f, 16.5138f)
                    horizontalLineTo(15.9268f)
                    curveTo(16.2407f, 16.5138f, 16.5515f, 16.4519f, 16.8414f, 16.3318f)
                    curveTo(17.1314f, 16.2116f, 17.3948f, 16.0355f, 17.6166f, 15.8135f)
                    curveTo(17.8385f, 15.5915f, 18.0144f, 15.328f, 18.1344f, 15.0379f)
                    curveTo(18.2544f, 14.7479f, 18.316f, 14.4371f, 18.3158f, 14.1232f)
                    verticalLineTo(9.8759f)
                    curveTo(18.316f, 9.562f, 18.2544f, 9.2512f, 18.1344f, 8.9612f)
                    curveTo(18.0144f, 8.6712f, 17.8385f, 8.4076f, 17.6166f, 8.1856f)
                    curveTo(17.3948f, 7.9636f, 17.1314f, 7.7875f, 16.8414f, 7.6673f)
                    curveTo(16.5515f, 7.5472f, 16.2407f, 7.4854f, 15.9268f, 7.4854f)
                    close()
                    moveTo(10.3358f, 13.9354f)
                    verticalLineTo(10.0638f)
                    lineTo(13.6642f, 11.9996f)
                    lineTo(10.3358f, 13.9354f)
                    close()
                }
            }
        }
        .build()
        return youtube!!
    }

private var youtube: ImageVector? = null
