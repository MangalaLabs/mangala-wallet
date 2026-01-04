package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.Portfolio: ImageVector
    get() {
        if (_portfolio != null) {
            return _portfolio!!
        }
        _portfolio = Builder(name = "Portfolio", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                moveTo(21.5241f, 9.753f)
                curveTo(20.885f, 5.8196f, 17.5441f, 2.8933f, 13.5608f, 2.7781f)
                curveTo(13.3714f, 2.7707f, 13.1868f, 2.8389f, 13.0477f, 2.9677f)
                curveTo(12.9086f, 3.0965f, 12.8265f, 3.2752f, 12.8193f, 3.4646f)
                verticalLineTo(3.4646f)
                verticalLineTo(3.5287f)
                lineTo(13.2678f, 10.2381f)
                curveTo(13.2973f, 10.6897f, 13.6856f, 11.0331f, 14.1374f, 11.007f)
                lineTo(20.8651f, 10.5585f)
                curveTo(21.0547f, 10.5444f, 21.2308f, 10.4554f, 21.3545f, 10.3111f)
                curveTo(21.4782f, 10.1668f, 21.5393f, 9.9791f, 21.5241f, 9.7896f)
                verticalLineTo(9.753f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF767676)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(8.9015f, 6.769f)
                curveTo(9.3295f, 6.67f, 9.7674f, 6.889f, 9.945f, 7.2908f)
                curveTo(9.9915f, 7.3853f, 10.0195f, 7.4878f, 10.0274f, 7.5928f)
                curveTo(10.1189f, 8.8926f, 10.3111f, 11.7393f, 10.421f, 13.277f)
                curveTo(10.4397f, 13.554f, 10.5681f, 13.8121f, 10.7777f, 13.9942f)
                curveTo(10.9872f, 14.1763f, 11.2608f, 14.2674f, 11.5377f, 14.2473f)
                verticalLineTo(14.2473f)
                lineTo(17.1853f, 13.8994f)
                curveTo(17.4374f, 13.8843f, 17.6846f, 13.974f, 17.8683f, 14.1473f)
                curveTo(18.052f, 14.3207f, 18.156f, 14.5622f, 18.1555f, 14.8148f)
                verticalLineTo(14.8148f)
                curveTo(17.9266f, 18.2252f, 15.4764f, 21.0762f, 12.1392f, 21.8152f)
                curveTo(8.802f, 22.5542f, 5.3772f, 21.0042f, 3.7299f, 18.0093f)
                curveTo(3.2381f, 17.1473f, 2.9267f, 16.1944f, 2.8145f, 15.2084f)
                curveTo(2.7666f, 14.9058f, 2.7482f, 14.5992f, 2.7596f, 14.293f)
                curveTo(2.7694f, 10.651f, 5.3271f, 7.513f, 8.8924f, 6.769f)
            }
        }
        .build()
        return _portfolio!!
    }

private var _portfolio: ImageVector? = null
