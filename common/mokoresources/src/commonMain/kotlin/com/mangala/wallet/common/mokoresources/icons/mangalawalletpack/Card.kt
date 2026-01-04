package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import kotlin.Unit

public val MangalaWalletPack.Card: ImageVector
    get() {
        if (_card != null) {
            return _card!!
        }
        _card = Builder(name = "Card", defaultWidth = 25.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 25.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFF1F5F9)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(2.334f, 10.0f)
                horizontalLineTo(22.334f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFF1F5F9)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(11.882f, 20.5f)
                horizontalLineTo(6.772f)
                curveTo(3.222f, 20.5f, 2.322f, 19.62f, 2.322f, 16.11f)
                verticalLineTo(7.89f)
                curveTo(2.322f, 4.71f, 3.062f, 3.69f, 5.852f, 3.53f)
                curveTo(6.132f, 3.52f, 6.442f, 3.51f, 6.772f, 3.51f)
                horizontalLineTo(17.882f)
                curveTo(21.432f, 3.51f, 22.332f, 4.39f, 22.332f, 7.9f)
                verticalLineTo(12.31f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFF1F5F9)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(6.334f, 16.0f)
                horizontalLineTo(10.334f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFF1F5F9)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(22.334f, 18.0f)
                curveTo(22.334f, 18.75f, 22.124f, 19.46f, 21.754f, 20.06f)
                curveTo(21.064f, 21.22f, 19.794f, 22.0f, 18.334f, 22.0f)
                curveTo(16.874f, 22.0f, 15.604f, 21.22f, 14.914f, 20.06f)
                curveTo(14.544f, 19.46f, 14.334f, 18.75f, 14.334f, 18.0f)
                curveTo(14.334f, 15.79f, 16.124f, 14.0f, 18.334f, 14.0f)
                curveTo(20.544f, 14.0f, 22.334f, 15.79f, 22.334f, 18.0f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFF1F5F9)),
                    strokeLineWidth = 1.6f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(19.501f, 19.333f)
                lineTo(18.334f, 18.594f)
                verticalLineTo(17.0f)
            }
        }
        .build()
        return _card!!
    }

private var _card: ImageVector? = null
