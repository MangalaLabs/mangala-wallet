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

public val MangalaWalletPack.ArrowDownExpand: ImageVector
    get() {
        if (_ArrowDownExpand != null) {
            return _ArrowDownExpand!!
        }
        _ArrowDownExpand = Builder(name = "ArrowDownExpand", defaultWidth = 18.0.dp, defaultHeight =
                18.0.dp, viewportWidth = 18.0f, viewportHeight = 18.0f).apply {
            path(fill = SolidColor(Color(0xFF292D32)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(13.4401f, 6.135f)
                horizontalLineTo(8.7676f)
                horizontalLineTo(4.5601f)
                curveTo(3.8401f, 6.135f, 3.4801f, 7.005f, 3.9901f, 7.515f)
                lineTo(7.8751f, 11.4f)
                curveTo(8.4976f, 12.0225f, 9.5101f, 12.0225f, 10.1326f, 11.4f)
                lineTo(11.6101f, 9.9225f)
                lineTo(14.0176f, 7.515f)
                curveTo(14.5201f, 7.005f, 14.1601f, 6.135f, 13.4401f, 6.135f)
                close()
            }
        }
        .build()
        return _ArrowDownExpand!!
    }

private var _ArrowDownExpand: ImageVector? = null
