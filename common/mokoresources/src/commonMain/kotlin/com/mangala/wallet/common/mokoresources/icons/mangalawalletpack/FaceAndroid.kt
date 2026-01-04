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

public val MangalaWalletPack.FaceAndroid: ImageVector
    get() {
        if (_faceAndroid != null) {
            return _faceAndroid!!
        }
        _faceAndroid = Builder(name = "FaceAndroid", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF383838)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(9.0742f, 16.1915f)
                curveTo(9.0528f, 16.7531f, 9.1938f, 17.309f, 9.4803f, 17.7926f)
                curveTo(9.7667f, 18.2761f, 10.1865f, 18.6669f, 10.6894f, 18.9179f)
                curveTo(11.1922f, 19.169f, 11.7568f, 19.2698f, 12.3154f, 19.2082f)
                curveTo(12.8741f, 19.1467f, 13.4031f, 18.9253f, 13.8392f, 18.5707f)
                lineTo(12.8804f, 17.3915f)
                curveTo(12.6711f, 17.5617f, 12.4171f, 17.668f, 12.1489f, 17.6975f)
                curveTo(11.8807f, 17.7271f, 11.6097f, 17.6787f, 11.3683f, 17.5582f)
                curveTo(11.1269f, 17.4377f, 10.9254f, 17.2501f, 10.7879f, 17.0179f)
                curveTo(10.6504f, 16.7858f, 10.5827f, 16.5189f, 10.5929f, 16.2493f)
                lineTo(9.0742f, 16.1915f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF383838)),
                    strokeLineWidth = 1.5f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(13.0771f, 11.3845f)
                verticalLineTo(13.8461f)
                horizontalLineTo(11.0771f)
            }
            path(fill = SolidColor(Color(0xFF383838)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(17.3845f, 8.0f)
                lineTo(17.3845f, 8.0f)
                arcTo(1.0769f, 1.0769f, 0.0f, false, true, 18.4615f, 9.0769f)
                lineTo(18.4615f, 9.0769f)
                arcTo(1.0769f, 1.0769f, 0.0f, false, true, 17.3845f, 10.1539f)
                lineTo(17.3845f, 10.1539f)
                arcTo(1.0769f, 1.0769f, 0.0f, false, true, 16.3076f, 9.0769f)
                lineTo(16.3076f, 9.0769f)
                arcTo(1.0769f, 1.0769f, 0.0f, false, true, 17.3845f, 8.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFF383838)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(6.616f, 8.0f)
                lineTo(6.616f, 8.0f)
                arcTo(1.0769f, 1.0769f, 0.0f, false, true, 7.6929f, 9.0769f)
                lineTo(7.6929f, 9.0769f)
                arcTo(1.0769f, 1.0769f, 0.0f, false, true, 6.616f, 10.1539f)
                lineTo(6.616f, 10.1539f)
                arcTo(1.0769f, 1.0769f, 0.0f, false, true, 5.5391f, 9.0769f)
                lineTo(5.5391f, 9.0769f)
                arcTo(1.0769f, 1.0769f, 0.0f, false, true, 6.616f, 8.0f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF383838)),
                    strokeLineWidth = 1.5f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(12.0f, 12.0f)
                moveToRelative(-11.25f, 0.0f)
                arcToRelative(11.25f, 11.25f, 0.0f, true, true, 22.5f, 0.0f)
                arcToRelative(11.25f, 11.25f, 0.0f, true, true, -22.5f, 0.0f)
            }
        }
        .build()
        return _faceAndroid!!
    }

private var _faceAndroid: ImageVector? = null
