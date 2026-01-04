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

public val MangalaWalletPack.ConnectWithUs: ImageVector
    get() {
        if (_connectWithUs != null) {
            return _connectWithUs!!
        }
        _connectWithUs = Builder(
            name = "ConnectWithUs", defaultWidth = 24.0.dp, defaultHeight =
            24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(8.5f, 19.0f)
                horizontalLineTo(8.0f)
                curveTo(4.0f, 19.0f, 2.0f, 18.0f, 2.0f, 13.0f)
                verticalLineTo(8.0f)
                curveTo(2.0f, 4.0f, 4.0f, 2.0f, 8.0f, 2.0f)
                horizontalLineTo(16.0f)
                curveTo(20.0f, 2.0f, 22.0f, 4.0f, 22.0f, 8.0f)
                verticalLineTo(13.0f)
                curveTo(22.0f, 17.0f, 20.0f, 19.0f, 16.0f, 19.0f)
                horizontalLineTo(15.5f)
                curveTo(15.19f, 19.0f, 14.89f, 19.15f, 14.7f, 19.4f)
                lineTo(13.2f, 21.4f)
                curveTo(12.54f, 22.28f, 11.46f, 22.28f, 10.8f, 21.4f)
                lineTo(9.3f, 19.4f)
                curveTo(9.14f, 19.18f, 8.77f, 19.0f, 8.5f, 19.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(7.0f, 8.0f)
                horizontalLineTo(17.0f)
            }
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(7.0f, 13.0f)
                horizontalLineTo(13.0f)
            }
        }
            .build()
        return _connectWithUs!!
    }

private var _connectWithUs: ImageVector? = null
