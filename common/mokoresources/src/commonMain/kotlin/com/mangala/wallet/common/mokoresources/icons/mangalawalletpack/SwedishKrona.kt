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

public val MangalaWalletPack.SwedishKrona: ImageVector
    get() {
        if (swedish_krona != null) {
            return swedish_krona!!
        }
        swedish_krona = Builder(name = "Swedish krona", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF225FB7)), stroke = null, strokeLineWidth = 0.0f,
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
            path(fill = SolidColor(Color(0xFFF8D33A)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(8.0284f, 13.7798f)
                lineTo(8.0199f, 12.2244f)
                horizontalLineTo(8.2415f)
                lineTo(10.8494f, 9.4545f)
                horizontalLineTo(12.375f)
                lineTo(9.4006f, 12.608f)
                horizontalLineTo(9.2003f)
                lineTo(8.0284f, 13.7798f)
                close()
                moveTo(6.8565f, 16.0f)
                verticalLineTo(7.2727f)
                horizontalLineTo(8.1307f)
                verticalLineTo(16.0f)
                horizontalLineTo(6.8565f)
                close()
                moveTo(10.9901f, 16.0f)
                lineTo(8.6463f, 12.8892f)
                lineTo(9.5241f, 11.9986f)
                lineTo(12.554f, 16.0f)
                horizontalLineTo(10.9901f)
                close()
                moveTo(13.5362f, 16.0f)
                verticalLineTo(9.4545f)
                horizontalLineTo(14.7678f)
                verticalLineTo(10.4943f)
                horizontalLineTo(14.8359f)
                curveTo(14.9553f, 10.142f, 15.1655f, 9.8651f, 15.4666f, 9.6634f)
                curveTo(15.7706f, 9.4588f, 16.1143f, 9.3565f, 16.4979f, 9.3565f)
                curveTo(16.5774f, 9.3565f, 16.6712f, 9.3594f, 16.7791f, 9.3651f)
                curveTo(16.8899f, 9.3707f, 16.9766f, 9.3778f, 17.0391f, 9.3864f)
                verticalLineTo(10.6051f)
                curveTo(16.9879f, 10.5909f, 16.897f, 10.5753f, 16.7663f, 10.5582f)
                curveTo(16.6357f, 10.5384f, 16.505f, 10.5284f, 16.3743f, 10.5284f)
                curveTo(16.0732f, 10.5284f, 15.8047f, 10.5923f, 15.5689f, 10.7202f)
                curveTo(15.3359f, 10.8452f, 15.1513f, 11.0199f, 15.0149f, 11.2443f)
                curveTo(14.8786f, 11.4659f, 14.8104f, 11.7187f, 14.8104f, 12.0028f)
                verticalLineTo(16.0f)
                horizontalLineTo(13.5362f)
                close()
            }
        }
        .build()
        return swedish_krona!!
    }

private var swedish_krona: ImageVector? = null
