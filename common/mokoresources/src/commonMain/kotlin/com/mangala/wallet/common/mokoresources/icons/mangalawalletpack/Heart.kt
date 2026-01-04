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

public val MangalaWalletPack.Heart: ImageVector
    get() {
        if (_heart != null) {
            return _heart!!
        }
        _heart = Builder(name = "Heart", defaultWidth = 18.0.dp, defaultHeight = 17.0.dp,
                viewportWidth = 18.0f, viewportHeight = 17.0f).apply {
            path(fill = SolidColor(Color(0xFF383838)), stroke = null, fillAlpha = 0.4f, strokeAlpha
                    = 0.4f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(8.8121f, 16.1979f)
                curveTo(6.9095f, 15.0227f, 5.141f, 13.6371f, 3.5387f, 12.0663f)
                curveTo(2.4075f, 10.9448f, 1.5437f, 9.5754f, 1.0131f, 8.0627f)
                curveTo(0.065f, 5.1127f, 1.1686f, 1.7412f, 4.2497f, 0.7403f)
                curveTo(5.8759f, 0.2296f, 7.6446f, 0.5431f, 9.0046f, 1.5832f)
                curveTo(10.3651f, 0.5443f, 12.1333f, 0.2309f, 13.7596f, 0.7403f)
                curveTo(16.8407f, 1.7412f, 17.9516f, 5.1127f, 17.0036f, 8.0627f)
                curveTo(16.4773f, 9.574f, 15.6186f, 10.9433f, 14.4928f, 12.0663f)
                curveTo(12.8891f, 13.6354f, 11.1207f, 15.0209f, 9.2194f, 16.1979f)
                lineTo(9.012f, 16.3333f)
                lineTo(8.8121f, 16.1979f)
                close()
            }
            path(fill = SolidColor(Color(0xFF383838)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(9.0084f, 16.3335f)
                lineTo(8.8127f, 16.198f)
                curveTo(6.9078f, 15.023f, 5.1367f, 13.6374f, 3.5319f, 12.0665f)
                curveTo(2.3953f, 10.9465f, 1.5263f, 9.577f, 0.9914f, 8.0629f)
                curveTo(0.0508f, 5.1129f, 1.1544f, 1.7414f, 4.2355f, 0.7405f)
                curveTo(5.8617f, 0.2298f, 7.6537f, 0.5435f, 9.0084f, 1.5923f)
                verticalLineTo(16.3335f)
                close()
            }
            path(fill = SolidColor(Color(0xFF383838)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(14.1907f, 6.3327f)
                curveTo(14.0233f, 6.3219f, 13.8674f, 6.2382f, 13.7596f, 6.1013f)
                curveTo(13.6517f, 5.9644f, 13.6014f, 5.7862f, 13.6203f, 5.6085f)
                curveTo(13.6384f, 5.0232f, 13.3054f, 4.4904f, 12.7918f, 4.2831f)
                curveTo(12.4661f, 4.1942f, 12.2689f, 3.8416f, 12.3504f, 3.4937f)
                curveTo(12.4277f, 3.1515f, 12.7482f, 2.9387f, 13.0702f, 3.0157f)
                curveTo(13.1109f, 3.0225f, 13.15f, 3.0372f, 13.1857f, 3.0592f)
                curveTo(14.2154f, 3.4555f, 14.8821f, 4.522f, 14.8291f, 5.6881f)
                curveTo(14.8274f, 5.8649f, 14.7585f, 6.0333f, 14.6381f, 6.1548f)
                curveTo(14.5178f, 6.2764f, 14.3563f, 6.3405f, 14.1907f, 6.3327f)
                close()
            }
        }
        .build()
        return _heart!!
    }

private var _heart: ImageVector? = null
