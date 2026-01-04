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

public val MangalaWalletPack.MyanmarKyat: ImageVector
    get() {
        if (myanmar_kyat != null) {
            return myanmar_kyat!!
        }
        myanmar_kyat = Builder(name = "Myanmar kyat", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
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
            path(fill = SolidColor(Color(0xFFF8D33A)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(8.9588f, 16.0f)
                verticalLineTo(7.2727f)
                horizontalLineTo(10.2756f)
                verticalLineTo(11.4403f)
                horizontalLineTo(10.3821f)
                lineTo(14.0426f, 7.2727f)
                horizontalLineTo(15.7003f)
                lineTo(12.1804f, 11.2017f)
                lineTo(15.7131f, 16.0f)
                horizontalLineTo(14.1278f)
                lineTo(11.3068f, 12.1009f)
                lineTo(10.2756f, 13.2855f)
                verticalLineTo(16.0f)
                horizontalLineTo(8.9588f)
                close()
            }
        }
        .build()
        return myanmar_kyat!!
    }

private var myanmar_kyat: ImageVector? = null
