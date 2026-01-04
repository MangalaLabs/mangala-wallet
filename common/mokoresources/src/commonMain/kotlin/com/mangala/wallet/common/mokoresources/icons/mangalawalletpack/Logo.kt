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

public val MangalaWalletPack.Logo : ImageVector
    get() {
        if (logo != null) {
            return logo!!
        }
        logo = Builder(name = "Frame 427322845", defaultWidth = 512.0.dp,
                defaultHeight = 512.0.dp, viewportWidth = 512.0f, viewportHeight = 512.0f).apply {
            path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(0.0f, 0.0f)
                horizontalLineToRelative(512.0f)
                verticalLineToRelative(512.0f)
                horizontalLineToRelative(-512.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFF383838)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(101.782f, 418.0f)
                curveTo(91.261f, 418.0f, 86.0f, 412.351f, 86.0f, 401.054f)
                verticalLineTo(110.946f)
                curveTo(86.0f, 99.648f, 91.261f, 94.0f, 101.782f, 94.0f)
                horizontalLineTo(167.167f)
                curveTo(175.885f, 94.0f, 182.123f, 100.025f, 185.881f, 112.075f)
                lineTo(246.981f, 310.226f)
                curveTo(249.687f, 318.812f, 251.566f, 325.891f, 252.618f, 331.464f)
                curveTo(253.67f, 336.887f, 254.572f, 343.364f, 255.324f, 350.895f)
                horizontalLineTo(256.676f)
                curveTo(257.428f, 343.364f, 258.33f, 336.887f, 259.382f, 331.464f)
                curveTo(260.434f, 325.891f, 262.313f, 318.812f, 265.019f, 310.226f)
                lineTo(326.119f, 112.075f)
                curveTo(329.877f, 100.025f, 336.115f, 94.0f, 344.833f, 94.0f)
                horizontalLineTo(410.218f)
                curveTo(420.739f, 94.0f, 426.0f, 99.648f, 426.0f, 110.946f)
                verticalLineTo(401.054f)
                curveTo(426.0f, 412.351f, 420.739f, 418.0f, 410.218f, 418.0f)
                horizontalLineTo(375.947f)
                curveTo(365.425f, 418.0f, 360.164f, 412.351f, 360.164f, 401.054f)
                verticalLineTo(214.427f)
                curveTo(360.164f, 205.389f, 360.54f, 196.351f, 361.292f, 187.314f)
                horizontalLineTo(359.714f)
                lineTo(294.554f, 399.699f)
                curveTo(290.797f, 411.9f, 284.333f, 418.0f, 275.164f, 418.0f)
                horizontalLineTo(236.836f)
                curveTo(227.667f, 418.0f, 221.203f, 411.9f, 217.446f, 399.699f)
                lineTo(152.286f, 187.314f)
                horizontalLineTo(150.708f)
                curveTo(151.46f, 196.351f, 151.836f, 205.389f, 151.836f, 214.427f)
                verticalLineTo(401.054f)
                curveTo(151.836f, 412.351f, 146.575f, 418.0f, 136.053f, 418.0f)
                horizontalLineTo(101.782f)
                close()
            }
        }
        .build()
        return logo!!
    }

private var logo: ImageVector? = null
