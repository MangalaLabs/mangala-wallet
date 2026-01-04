package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.French: ImageVector
    get() {
        if (french != null) {
            return french!!
        }
        french = Builder(name = "French", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            group {
                path(fill = SolidColor(Color(0xFFED2939)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = EvenOdd) {
                    moveTo(16.96f, 22.9299f)
                    verticalLineTo(1.0701f)
                    curveTo(21.1125f, 2.9575f, 24.0f, 7.1414f, 24.0f, 12.0f)
                    curveTo(24.0f, 16.8585f, 21.1125f, 21.0425f, 16.96f, 22.9299f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF002395)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = EvenOdd) {
                    moveTo(7.04f, 1.0701f)
                    verticalLineTo(22.9299f)
                    curveTo(2.8875f, 21.0425f, 0.0f, 16.8585f, 0.0f, 12.0f)
                    curveTo(0.0f, 7.1414f, 2.8875f, 2.9575f, 7.04f, 1.0701f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFF5F5F5)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = EvenOdd) {
                    moveTo(16.96f, 1.0701f)
                    verticalLineTo(22.9299f)
                    curveTo(15.4016f, 23.6352f, 13.7106f, 24.0f, 12.0f, 24.0f)
                    curveTo(10.2894f, 24.0f, 8.5985f, 23.6352f, 7.04f, 22.9299f)
                    verticalLineTo(1.0701f)
                    curveTo(8.5985f, 0.3648f, 10.2894f, 0.0f, 12.0f, 0.0f)
                    curveTo(13.7106f, 0.0f, 15.4016f, 0.3648f, 16.96f, 1.0701f)
                    close()
                }
            }
        }
        .build()
        return french!!
    }

private var french: ImageVector? = null
