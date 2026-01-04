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

public val MangalaWalletPack.HungarianForint: ImageVector
    get() {
        if (hungarian_forint != null) {
            return hungarian_forint!!
        }
        hungarian_forint = Builder(name = "Hungarian forint", defaultWidth = 24.0.dp,
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
                moveTo(7.7173f, 16.0f)
                verticalLineTo(8.7273f)
                horizontalLineTo(12.3764f)
                verticalLineTo(9.8317f)
                horizontalLineTo(9.0348f)
                verticalLineTo(11.8061f)
                horizontalLineTo(12.0568f)
                verticalLineTo(12.9105f)
                horizontalLineTo(9.0348f)
                verticalLineTo(16.0f)
                horizontalLineTo(7.7173f)
                close()
                moveTo(16.2436f, 10.5455f)
                verticalLineTo(11.5398f)
                horizontalLineTo(13.108f)
                verticalLineTo(10.5455f)
                horizontalLineTo(16.2436f)
                close()
                moveTo(13.8821f, 9.2386f)
                horizontalLineTo(15.1676f)
                verticalLineTo(14.3594f)
                curveTo(15.1676f, 14.5322f, 15.1937f, 14.6648f, 15.2457f, 14.7571f)
                curveTo(15.3002f, 14.8471f, 15.3712f, 14.9086f, 15.4588f, 14.9418f)
                curveTo(15.5464f, 14.9749f, 15.6435f, 14.9915f, 15.75f, 14.9915f)
                curveTo(15.8305f, 14.9915f, 15.9039f, 14.9856f, 15.9702f, 14.9737f)
                curveTo(16.0388f, 14.9619f, 16.0909f, 14.9512f, 16.1264f, 14.9418f)
                lineTo(16.343f, 15.9467f)
                curveTo(16.2744f, 15.9704f, 16.1761f, 15.9964f, 16.0483f, 16.0249f)
                curveTo(15.9228f, 16.0533f, 15.7689f, 16.0698f, 15.5866f, 16.0746f)
                curveTo(15.2647f, 16.084f, 14.9747f, 16.0355f, 14.7166f, 15.929f)
                curveTo(14.4586f, 15.8201f, 14.2538f, 15.652f, 14.1023f, 15.4247f)
                curveTo(13.9531f, 15.1974f, 13.8797f, 14.9134f, 13.8821f, 14.5724f)
                verticalLineTo(9.2386f)
                close()
            }
        }
        .build()
        return hungarian_forint!!
    }

private var hungarian_forint: ImageVector? = null
