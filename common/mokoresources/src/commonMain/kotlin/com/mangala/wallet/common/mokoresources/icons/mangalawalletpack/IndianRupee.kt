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

public val MangalaWalletPack.IndianRupee: ImageVector
    get() {
        if (indian_rupee != null) {
            return indian_rupee!!
        }
        indian_rupee = Builder(name = "Indian rupee", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFFFFA114)), stroke = null, strokeLineWidth = 0.0f,
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
                moveTo(17.0f, 5.0f)
                horizontalLineTo(7.0f)
                verticalLineTo(6.8914f)
                horizontalLineTo(10.5265f)
                curveTo(11.8216f, 6.8914f, 12.9261f, 7.682f, 13.3362f, 8.7828f)
                horizontalLineTo(7.0f)
                verticalLineTo(10.6741f)
                horizontalLineTo(13.3362f)
                curveTo(12.9261f, 11.7749f, 11.8216f, 12.5655f, 10.5265f, 12.5655f)
                horizontalLineTo(7.0f)
                verticalLineTo(14.4569f)
                lineTo(12.8225f, 20.0f)
                lineTo(14.2274f, 18.6626f)
                lineTo(9.8097f, 14.4569f)
                horizontalLineTo(10.5265f)
                curveTo(12.9251f, 14.4569f, 14.932f, 12.8299f, 15.3935f, 10.6742f)
                horizontalLineTo(17.0f)
                verticalLineTo(8.7828f)
                horizontalLineTo(15.3935f)
                curveTo(15.244f, 8.0843f, 14.9322f, 7.4415f, 14.4975f, 6.8914f)
                horizontalLineTo(17.0f)
                verticalLineTo(5.0f)
                close()
            }
        }
        .build()
        return indian_rupee!!
    }

private var indian_rupee: ImageVector? = null
