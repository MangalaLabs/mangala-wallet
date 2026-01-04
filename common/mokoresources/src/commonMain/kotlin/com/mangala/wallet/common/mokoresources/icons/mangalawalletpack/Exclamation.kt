package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.Exclamation: ImageVector
    get() {
        if (_exclamation != null) {
            return _exclamation!!
        }
        _exclamation = ImageVector.Builder(
            name = "Exclamation", defaultWidth = 18.0.dp, defaultHeight =
            18.0.dp, viewportWidth = 18.0f, viewportHeight = 18.0f
        ).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF767676)),
                strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(9.0003f, 15.3002f)
                curveTo(10.2464f, 15.3002f, 11.4644f, 14.9307f, 12.5005f, 14.2384f)
                curveTo(13.5365f, 13.5462f, 14.344f, 12.5622f, 14.8209f, 11.411f)
                curveTo(15.2977f, 10.2598f, 15.4225f, 8.9931f, 15.1794f, 7.771f)
                curveTo(14.9363f, 6.5489f, 14.3363f, 5.4263f, 13.4552f, 4.5452f)
                curveTo(12.5741f, 3.6641f, 11.4515f, 3.0641f, 10.2294f, 2.821f)
                curveTo(9.0073f, 2.5779f, 7.7406f, 2.7027f, 6.5894f, 3.1795f)
                curveTo(5.4382f, 3.6564f, 4.4542f, 4.4639f, 3.762f, 5.4999f)
                curveTo(3.0697f, 6.536f, 2.7002f, 7.754f, 2.7002f, 9.0001f)
                curveTo(2.7002f, 10.671f, 3.364f, 12.2734f, 4.5455f, 13.4549f)
                curveTo(5.727f, 14.6364f, 7.3294f, 15.3002f, 9.0003f, 15.3002f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF767676)),
                strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(9.0f, 6.48f)
                verticalLineTo(9.0f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF767676)),
                strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(9.0f, 11.5203f)
                horizontalLineTo(9.0072f)
            }
        }
            .build()
        return _exclamation!!
    }

private var _exclamation: ImageVector? = null