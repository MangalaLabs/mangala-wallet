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

public val MangalaWalletPack.MoreVertical: ImageVector
    get() {
        if (_moreVertical != null) {
            return _moreVertical!!
        }
        _moreVertical = Builder(name = "MoreVertical", defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF151B33)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(14.0f, 5.0f)
                curveTo(14.0f, 3.9f, 13.1f, 3.0f, 12.0f, 3.0f)
                curveTo(10.9f, 3.0f, 10.0f, 3.9f, 10.0f, 5.0f)
                curveTo(10.0f, 6.1f, 10.9f, 7.0f, 12.0f, 7.0f)
                curveTo(13.1f, 7.0f, 14.0f, 6.1f, 14.0f, 5.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFF151B33)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(14.0f, 19.0f)
                curveTo(14.0f, 17.9f, 13.1f, 17.0f, 12.0f, 17.0f)
                curveTo(10.9f, 17.0f, 10.0f, 17.9f, 10.0f, 19.0f)
                curveTo(10.0f, 20.1f, 10.9f, 21.0f, 12.0f, 21.0f)
                curveTo(13.1f, 21.0f, 14.0f, 20.1f, 14.0f, 19.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFF151B33)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(14.0f, 12.0f)
                curveTo(14.0f, 10.9f, 13.1f, 10.0f, 12.0f, 10.0f)
                curveTo(10.9f, 10.0f, 10.0f, 10.9f, 10.0f, 12.0f)
                curveTo(10.0f, 13.1f, 10.9f, 14.0f, 12.0f, 14.0f)
                curveTo(13.1f, 14.0f, 14.0f, 13.1f, 14.0f, 12.0f)
                close()
            }
        }
        .build()
        return _moreVertical!!
    }

private var _moreVertical: ImageVector? = null
