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

public val MangalaWalletPack.ArrowDownCollapse: ImageVector
    get() {
        if (arrowDownCollapse != null) {
            return arrowDownCollapse!!
        }
        arrowDownCollapse = Builder(name = "Arrow-down", defaultWidth = 18.0.dp, defaultHeight =
                18.0.dp, viewportWidth = 18.0f, viewportHeight = 18.0f).apply {
            path(fill = SolidColor(Color(0xFF292D32)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(4.5599f, 11.865f)
                lineTo(9.2324f, 11.865f)
                horizontalLineTo(13.4399f)
                curveTo(14.1599f, 11.865f, 14.5199f, 10.995f, 14.0099f, 10.485f)
                lineTo(10.1249f, 6.6f)
                curveTo(9.5024f, 5.9775f, 8.4899f, 5.9775f, 7.8674f, 6.6f)
                lineTo(6.3899f, 8.0775f)
                lineTo(3.9824f, 10.485f)
                curveTo(3.4799f, 10.995f, 3.8399f, 11.865f, 4.5599f, 11.865f)
                close()
            }
        }
        .build()
        return arrowDownCollapse!!
    }

private var arrowDownCollapse: ImageVector? = null
