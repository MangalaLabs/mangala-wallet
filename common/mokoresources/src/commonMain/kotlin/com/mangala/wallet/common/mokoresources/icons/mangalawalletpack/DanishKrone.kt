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

public val MangalaWalletPack.DanishKrone: ImageVector
    get() {
        if (danish_krone != null) {
            return danish_krone!!
        }
        danish_krone = Builder(name = "Danish krone", defaultWidth = 24.0.dp, defaultHeight =
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
                horizontalLineTo(7.0348f)
                verticalLineTo(12.0689f)
                horizontalLineTo(7.1236f)
                lineTo(9.9609f, 8.7273f)
                horizontalLineTo(11.5696f)
                lineTo(8.7571f, 11.9908f)
                lineTo(11.5945f, 16.0f)
                horizontalLineTo(10.0107f)
                lineTo(7.8409f, 12.8821f)
                lineTo(7.0348f, 13.8338f)
                verticalLineTo(16.0f)
                horizontalLineTo(5.7173f)
                close()
                moveTo(12.4281f, 16.0f)
                verticalLineTo(10.5455f)
                horizontalLineTo(13.6745f)
                verticalLineTo(11.4545f)
                horizontalLineTo(13.7314f)
                curveTo(13.8308f, 11.1397f, 14.0012f, 10.897f, 14.2427f, 10.7266f)
                curveTo(14.4866f, 10.5537f, 14.7647f, 10.4673f, 15.0772f, 10.4673f)
                curveTo(15.1483f, 10.4673f, 15.2276f, 10.4709f, 15.3152f, 10.478f)
                curveTo(15.4051f, 10.4827f, 15.4797f, 10.491f, 15.5389f, 10.5028f)
                verticalLineTo(11.6854f)
                curveTo(15.4844f, 11.6664f, 15.398f, 11.6499f, 15.2797f, 11.6357f)
                curveTo(15.1636f, 11.6191f, 15.0512f, 11.6108f, 14.9423f, 11.6108f)
                curveTo(14.7079f, 11.6108f, 14.4972f, 11.6617f, 14.3102f, 11.7635f)
                curveTo(14.1255f, 11.8629f, 13.9799f, 12.0014f, 13.8734f, 12.179f)
                curveTo(13.7669f, 12.3565f, 13.7136f, 12.5613f, 13.7136f, 12.7933f)
                verticalLineTo(16.0f)
                horizontalLineTo(12.4281f)
                close()
                moveTo(16.5598f, 16.0781f)
                curveTo(16.3444f, 16.0781f, 16.1597f, 16.0024f, 16.0059f, 15.8509f)
                curveTo(15.852f, 15.6993f, 15.7762f, 15.5147f, 15.7786f, 15.2969f)
                curveTo(15.7762f, 15.0838f, 15.852f, 14.9015f, 16.0059f, 14.75f)
                curveTo(16.1597f, 14.5985f, 16.3444f, 14.5227f, 16.5598f, 14.5227f)
                curveTo(16.7682f, 14.5227f, 16.9493f, 14.5985f, 17.1032f, 14.75f)
                curveTo(17.2594f, 14.9015f, 17.3387f, 15.0838f, 17.3411f, 15.2969f)
                curveTo(17.3387f, 15.4413f, 17.3008f, 15.5727f, 17.2275f, 15.6911f)
                curveTo(17.1564f, 15.8094f, 17.0617f, 15.9041f, 16.9434f, 15.9751f)
                curveTo(16.8274f, 16.0438f, 16.6995f, 16.0781f, 16.5598f, 16.0781f)
                close()
            }
        }
        .build()
        return danish_krone!!
    }

private var danish_krone: ImageVector? = null
