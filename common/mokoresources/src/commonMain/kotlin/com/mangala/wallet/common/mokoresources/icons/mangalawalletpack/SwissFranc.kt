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

public val MangalaWalletPack.SwissFranc: ImageVector
    get() {
        if (swiss_franc != null) {
            return swiss_franc!!
        }
        swiss_franc = Builder(name = "Swiss franc", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFFE23A5C)), stroke = null, strokeLineWidth = 0.0f,
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
            path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(5.7173f, 16.0f)
                verticalLineTo(8.7273f)
                horizontalLineTo(10.3764f)
                verticalLineTo(9.8317f)
                horizontalLineTo(7.0348f)
                verticalLineTo(11.8061f)
                horizontalLineTo(10.0568f)
                verticalLineTo(12.9105f)
                horizontalLineTo(7.0348f)
                verticalLineTo(16.0f)
                horizontalLineTo(5.7173f)
                close()
                moveTo(11.4418f, 16.0f)
                verticalLineTo(10.5455f)
                horizontalLineTo(12.6882f)
                verticalLineTo(11.4545f)
                horizontalLineTo(12.745f)
                curveTo(12.8445f, 11.1397f, 13.0149f, 10.897f, 13.2564f, 10.7266f)
                curveTo(13.5002f, 10.5537f, 13.7784f, 10.4673f, 14.0909f, 10.4673f)
                curveTo(14.1619f, 10.4673f, 14.2412f, 10.4709f, 14.3288f, 10.478f)
                curveTo(14.4188f, 10.4827f, 14.4934f, 10.491f, 14.5526f, 10.5028f)
                verticalLineTo(11.6854f)
                curveTo(14.4981f, 11.6664f, 14.4117f, 11.6499f, 14.2933f, 11.6357f)
                curveTo(14.1773f, 11.6191f, 14.0649f, 11.6108f, 13.956f, 11.6108f)
                curveTo(13.7216f, 11.6108f, 13.5109f, 11.6617f, 13.3239f, 11.7635f)
                curveTo(13.1392f, 11.8629f, 12.9936f, 12.0014f, 12.8871f, 12.179f)
                curveTo(12.7805f, 12.3565f, 12.7273f, 12.5613f, 12.7273f, 12.7933f)
                verticalLineTo(16.0f)
                horizontalLineTo(11.4418f)
                close()
                moveTo(15.5735f, 16.0781f)
                curveTo(15.3581f, 16.0781f, 15.1734f, 16.0024f, 15.0195f, 15.8509f)
                curveTo(14.8656f, 15.6993f, 14.7899f, 15.5147f, 14.7923f, 15.2969f)
                curveTo(14.7899f, 15.0838f, 14.8656f, 14.9015f, 15.0195f, 14.75f)
                curveTo(15.1734f, 14.5985f, 15.3581f, 14.5227f, 15.5735f, 14.5227f)
                curveTo(15.7818f, 14.5227f, 15.9629f, 14.5985f, 16.1168f, 14.75f)
                curveTo(16.2731f, 14.9015f, 16.3524f, 15.0838f, 16.3548f, 15.2969f)
                curveTo(16.3524f, 15.4413f, 16.3145f, 15.5727f, 16.2411f, 15.6911f)
                curveTo(16.1701f, 15.8094f, 16.0754f, 15.9041f, 15.957f, 15.9751f)
                curveTo(15.841f, 16.0438f, 15.7132f, 16.0781f, 15.5735f, 16.0781f)
                close()
            }
        }
        .build()
        return swiss_franc!!
    }

private var swiss_franc: ImageVector? = null
