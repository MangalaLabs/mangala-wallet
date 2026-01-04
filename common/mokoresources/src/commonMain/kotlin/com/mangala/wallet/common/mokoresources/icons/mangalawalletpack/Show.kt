package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.Show: ImageVector
    get() {
        if (_show != null) {
            return _show!!
        }
        _show = Builder(name = "Show", defaultWidth = 18.0.dp, defaultHeight = 14.0.dp,
            viewportWidth = 18.0f, viewportHeight = 14.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                moveTo(11.6344f, 7.0442f)
                curveTo(11.6344f, 8.4992f, 10.4544f, 9.6784f, 8.9994f, 9.6784f)
                curveTo(7.5444f, 9.6784f, 6.3652f, 8.4992f, 6.3652f, 7.0442f)
                curveTo(6.3652f, 5.5883f, 7.5444f, 4.4092f, 8.9994f, 4.4092f)
                curveTo(10.4544f, 4.4092f, 11.6344f, 5.5883f, 11.6344f, 7.0442f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                moveTo(8.9982f, 13.129f)
                curveTo(12.1715f, 13.129f, 15.074f, 10.8473f, 16.7082f, 7.044f)
                curveTo(15.074f, 3.2406f, 12.1715f, 0.959f, 8.9982f, 0.959f)
                horizontalLineTo(9.0015f)
                curveTo(5.8282f, 0.959f, 2.9257f, 3.2406f, 1.2915f, 7.044f)
                curveTo(2.9257f, 10.8473f, 5.8282f, 13.129f, 9.0015f, 13.129f)
                horizontalLineTo(8.9982f)
                close()
            }
        }
            .build()
        return _show!!
    }

private var _show: ImageVector? = null