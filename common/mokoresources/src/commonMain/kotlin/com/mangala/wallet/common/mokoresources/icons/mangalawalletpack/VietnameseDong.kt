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

public val MangalaWalletPack.VietnameseDong: ImageVector
    get() {
        if (vietnamese_dong != null) {
            return vietnamese_dong!!
        }
        vietnamese_dong = Builder(name = "Vietnamese dong", defaultWidth = 24.0.dp, defaultHeight
                = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {

            path(fill = SolidColor(Color(0xFFE94267)), stroke = null, strokeLineWidth = 0.0f,
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
            path(fill = SolidColor(Color(0xFFF8D33A)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(7.7947f, 17.9053f)
                horizontalLineTo(15.695f)
                verticalLineTo(20.0f)
                horizontalLineTo(7.7947f)
                verticalLineTo(17.9053f)
                close()
                moveTo(17.0f, 6.6778f)
                horizontalLineTo(15.7768f)
                verticalLineTo(16.8134f)
                horizontalLineTo(13.1239f)
                lineTo(12.9758f, 15.8477f)
                curveTo(12.3681f, 16.6054f, 11.6007f, 16.9805f, 10.6658f, 16.9805f)
                curveTo(9.5399f, 16.9805f, 8.6478f, 16.5682f, 7.9895f, 15.7474f)
                curveTo(7.3311f, 14.9229f, 7.0f, 13.753f, 7.0f, 12.234f)
                curveTo(7.0f, 10.8041f, 7.3233f, 9.6787f, 7.97f, 8.8542f)
                curveTo(8.6167f, 8.0297f, 9.5204f, 7.6212f, 10.6813f, 7.6212f)
                curveTo(11.5267f, 7.6212f, 12.2396f, 7.9369f, 12.8161f, 8.572f)
                verticalLineTo(6.6778f)
                horizontalLineTo(10.6112f)
                verticalLineTo(5.0845f)
                horizontalLineTo(12.8161f)
                verticalLineTo(4.0f)
                horizontalLineTo(15.7729f)
                verticalLineTo(5.0845f)
                horizontalLineTo(16.9961f)
                verticalLineTo(6.6778f)
                horizontalLineTo(17.0f)
                close()
                moveTo(12.8161f, 10.5887f)
                curveTo(12.5473f, 10.0539f, 12.0877f, 9.7864f, 11.4332f, 9.7864f)
                curveTo(10.5216f, 9.7864f, 10.0308f, 10.4884f, 9.9529f, 11.8886f)
                lineTo(9.9451f, 12.4048f)
                curveTo(9.9451f, 14.0056f, 10.4359f, 14.8078f, 11.4137f, 14.8078f)
                curveTo(12.0682f, 14.8078f, 12.5356f, 14.5478f, 12.8122f, 14.0241f)
                verticalLineTo(10.5887f)
                horizontalLineTo(12.8161f)
                close()
            }
        }
        .build()
        return vietnamese_dong!!
    }

private var vietnamese_dong: ImageVector? = null
