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

public val MangalaWalletPack.Browser: ImageVector
    get() {
        if (_browser != null) {
            return _browser!!
        }
        _browser = Builder(name = "Browser", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                moveTo(8.2705f, 14.9519f)
                lineTo(9.8632f, 9.8627f)
                lineTo(14.9524f, 8.27f)
                lineTo(13.3598f, 13.3593f)
                lineTo(8.2705f, 14.9519f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(11.611f, 11.611f)
                moveToRelative(-9.611f, 0.0f)
                arcToRelative(9.611f, 9.611f, 0.0f, true, true, 19.222f, 0.0f)
                arcToRelative(9.611f, 9.611f, 0.0f, true, true, -19.222f, 0.0f)
            }
        }
        .build()
        return _browser!!
    }

private var _browser: ImageVector? = null
