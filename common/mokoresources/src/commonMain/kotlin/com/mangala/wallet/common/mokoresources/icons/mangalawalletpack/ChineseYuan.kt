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

public val MangalaWalletPack.ChineseYuan: ImageVector
    get() {
        if (chinese_yuan != null) {
            return chinese_yuan!!
        }
        chinese_yuan = Builder(name = "Chinese yuan", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFFE94268)), stroke = null, strokeLineWidth = 0.0f,
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
                moveTo(18.4453f, 4.9688f)
                curveTo(18.7125f, 4.9688f, 18.7875f, 5.0953f, 18.6422f, 5.3625f)
                lineTo(14.1f, 13.125f)
                horizontalLineTo(16.4391f)
                curveTo(16.6359f, 13.125f, 16.725f, 13.2141f, 16.725f, 13.3922f)
                verticalLineTo(14.1281f)
                curveTo(16.725f, 14.3062f, 16.6359f, 14.4f, 16.4391f, 14.4f)
                horizontalLineTo(13.6547f)
                verticalLineTo(15.7641f)
                horizontalLineTo(16.4391f)
                curveTo(16.6359f, 15.7641f, 16.725f, 15.8531f, 16.725f, 16.0359f)
                verticalLineTo(16.7719f)
                curveTo(16.725f, 16.95f, 16.6359f, 17.0438f, 16.4391f, 17.0438f)
                horizontalLineTo(13.6547f)
                verticalLineTo(18.7313f)
                curveTo(13.6547f, 18.9281f, 13.5656f, 19.0359f, 13.3828f, 19.0359f)
                horizontalLineTo(10.5984f)
                curveTo(10.4203f, 19.0359f, 10.3312f, 18.9281f, 10.3312f, 18.7313f)
                verticalLineTo(17.0438f)
                horizontalLineTo(7.5656f)
                curveTo(7.3875f, 17.0438f, 7.2984f, 16.9547f, 7.2984f, 16.7719f)
                verticalLineTo(16.0312f)
                curveTo(7.2984f, 15.8531f, 7.3875f, 15.7594f, 7.5656f, 15.7594f)
                horizontalLineTo(10.3312f)
                verticalLineTo(14.3953f)
                horizontalLineTo(7.5656f)
                curveTo(7.3875f, 14.3953f, 7.2984f, 14.3062f, 7.2984f, 14.1234f)
                verticalLineTo(13.3875f)
                curveTo(7.2984f, 13.2094f, 7.3875f, 13.1203f, 7.5656f, 13.1203f)
                horizontalLineTo(9.9187f)
                lineTo(5.3578f, 5.3625f)
                curveTo(5.2125f, 5.0906f, 5.2828f, 4.9688f, 5.5547f, 4.9688f)
                horizontalLineTo(8.6812f)
                curveTo(8.8594f, 4.9688f, 9.0234f, 5.0578f, 9.1641f, 5.2547f)
                lineTo(12.0f, 10.6453f)
                lineTo(14.8547f, 5.2594f)
                curveTo(14.9437f, 5.0766f, 15.1359f, 4.9641f, 15.3375f, 4.9734f)
                horizontalLineTo(18.4453f)
                verticalLineTo(4.9688f)
                close()
            }
        }
        .build()
        return chinese_yuan!!
    }

private var chinese_yuan: ImageVector? = null
