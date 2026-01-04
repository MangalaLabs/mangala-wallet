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

public val MangalaWalletPack.Document: ImageVector
    get() {
        if (_document != null) {
            return _document!!
        }
        _document = Builder(name = "Document", defaultWidth = 20.0.dp, defaultHeight =
                20.0.dp, viewportWidth = 20.0f, viewportHeight = 20.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(18.3334f, 8.3334f)
                verticalLineTo(12.5001f)
                curveTo(18.3334f, 16.6667f, 16.6667f, 18.3334f, 12.5001f, 18.3334f)
                horizontalLineTo(7.5001f)
                curveTo(3.3334f, 18.3334f, 1.6667f, 16.6667f, 1.6667f, 12.5001f)
                verticalLineTo(7.5001f)
                curveTo(1.6667f, 3.3334f, 3.3334f, 1.6667f, 7.5001f, 1.6667f)
                horizontalLineTo(11.6667f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(18.3334f, 8.3334f)
                horizontalLineTo(15.0001f)
                curveTo(12.5001f, 8.3334f, 11.6667f, 7.5001f, 11.6667f, 5.0001f)
                verticalLineTo(1.6667f)
                lineTo(18.3334f, 8.3334f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(5.8333f, 10.8333f)
                horizontalLineTo(10.8333f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(5.8333f, 14.1667f)
                horizontalLineTo(9.1666f)
            }
        }
        .build()
        return _document!!
    }

private var _document: ImageVector? = null
