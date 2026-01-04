package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.Clear: ImageVector
    get() {
        if (_clear != null) {
            return _clear!!
        }
        _clear = Builder(name = "Clear", defaultWidth = 16.0.dp, defaultHeight = 16.0.dp,
            viewportWidth = 16.0f, viewportHeight = 16.0f).apply {

            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color.Red),
                strokeLineWidth = 1.33333f, strokeLineCap = Round, strokeLineJoin =
                StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(10.6668f, 5.3333f)
                lineTo(5.3335f, 10.6666f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color.Red),
                strokeLineWidth = 1.33333f, strokeLineCap = Round, strokeLineJoin =
                StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(10.6668f, 10.6666f)
                lineTo(5.3335f, 5.3333f)
            }
        }
            .build()
        return _clear!!
    }

private var _clear: ImageVector? = null
