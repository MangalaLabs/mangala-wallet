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

public val MangalaWalletPack.PolishZloty: ImageVector
    get() {
        if (polish_zloty != null) {
            return polish_zloty!!
        }
        polish_zloty = Builder(name = "Polish zloty", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFFEEEFF1)), stroke = null, strokeLineWidth = 0.0f,
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
            path(fill = SolidColor(Color(0xFFD9304E)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(11.5724f, 17.3232f)
                horizontalLineTo(5.8215f)
                curveTo(5.4929f, 17.3232f, 5.1643f, 17.1588f, 5.0821f, 16.8302f)
                curveTo(5.0f, 16.5016f, 5.0f, 16.173f, 5.2465f, 15.9265f)
                lineTo(9.6007f, 11.5723f)
                horizontalLineTo(5.8215f)
                curveTo(5.3286f, 11.5723f, 5.0f, 11.2437f, 5.0f, 10.7507f)
                curveTo(5.0f, 10.2578f, 5.3286f, 9.9292f, 5.8215f, 9.9292f)
                horizontalLineTo(11.5724f)
                curveTo(11.901f, 9.9292f, 12.2296f, 10.0935f, 12.3118f, 10.4221f)
                curveTo(12.4761f, 10.7507f, 12.394f, 11.0794f, 12.1475f, 11.3258f)
                lineTo(7.7933f, 15.6801f)
                horizontalLineTo(11.5724f)
                curveTo(12.0653f, 15.6801f, 12.394f, 16.0087f, 12.394f, 16.5016f)
                curveTo(12.394f, 16.9945f, 12.0653f, 17.3232f, 11.5724f, 17.3232f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD9304E)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(15.6802f, 17.3233f)
                curveTo(15.1873f, 17.3233f, 14.8586f, 16.9946f, 14.8586f, 16.5017f)
                verticalLineTo(5.8215f)
                curveTo(14.8586f, 5.3286f, 15.1873f, 5.0f, 15.6802f, 5.0f)
                curveTo(16.1731f, 5.0f, 16.5017f, 5.3286f, 16.5017f, 5.8215f)
                verticalLineTo(16.5017f)
                curveTo(16.5017f, 16.9946f, 16.1731f, 17.3233f, 15.6802f, 17.3233f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD9304E)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(13.2155f, 14.4477f)
                curveTo(12.9691f, 14.4477f, 12.6405f, 14.2834f, 12.4762f, 14.0369f)
                curveTo(12.2297f, 13.6261f, 12.394f, 13.1332f, 12.8048f, 12.8867f)
                lineTo(17.7341f, 10.0113f)
                curveTo(18.1448f, 9.7648f, 18.6378f, 9.9291f, 18.8842f, 10.3399f)
                curveTo(19.1307f, 10.7507f, 18.9664f, 11.2436f, 18.5556f, 11.4901f)
                lineTo(13.6263f, 14.3655f)
                curveTo(13.462f, 14.4477f, 13.3799f, 14.4477f, 13.2155f, 14.4477f)
                close()
            }
        }
        .build()
        return polish_zloty!!
    }

private var polish_zloty: ImageVector? = null
