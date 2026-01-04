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

public val MangalaWalletPack.MoreHorizontalCircle: ImageVector
    get() {
        if (_moreHorizontalCircle != null) {
            return _moreHorizontalCircle!!
        }
        _moreHorizontalCircle = Builder(name = "MoreHorizontalCircle", defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFFF8F8F8)), stroke = null, strokeLineWidth = 0.0f,
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
            path(fill = SolidColor(Color(0xFF383838)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(6.3977f, 13.7602f)
                curveTo(7.3697f, 13.7602f, 8.1577f, 12.9723f, 8.1577f, 12.0002f)
                curveTo(8.1577f, 11.0282f, 7.3697f, 10.2402f, 6.3977f, 10.2402f)
                curveTo(5.4257f, 10.2402f, 4.6377f, 11.0282f, 4.6377f, 12.0002f)
                curveTo(4.6377f, 12.9723f, 5.4257f, 13.7602f, 6.3977f, 13.7602f)
                close()
            }
            path(fill = SolidColor(Color(0xFF383838)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(11.9978f, 13.7602f)
                curveTo(12.9698f, 13.7602f, 13.7578f, 12.9723f, 13.7578f, 12.0002f)
                curveTo(13.7578f, 11.0282f, 12.9698f, 10.2402f, 11.9978f, 10.2402f)
                curveTo(11.0258f, 10.2402f, 10.2378f, 11.0282f, 10.2378f, 12.0002f)
                curveTo(10.2378f, 12.9723f, 11.0258f, 13.7602f, 11.9978f, 13.7602f)
                close()
            }
            path(fill = SolidColor(Color(0xFF383838)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(17.5979f, 13.7602f)
                curveTo(18.5699f, 13.7602f, 19.3579f, 12.9723f, 19.3579f, 12.0002f)
                curveTo(19.3579f, 11.0282f, 18.5699f, 10.2402f, 17.5979f, 10.2402f)
                curveTo(16.6259f, 10.2402f, 15.8379f, 11.0282f, 15.8379f, 12.0002f)
                curveTo(15.8379f, 12.9723f, 16.6259f, 13.7602f, 17.5979f, 13.7602f)
                close()
            }
        }
        .build()
        return _moreHorizontalCircle!!
    }

private var _moreHorizontalCircle: ImageVector? = null
