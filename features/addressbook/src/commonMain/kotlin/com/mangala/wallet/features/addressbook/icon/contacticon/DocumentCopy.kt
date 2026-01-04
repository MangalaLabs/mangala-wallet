package com.mangala.wallet.features.addressbook.icon.contacticon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.features.addressbook.icon.ContactIcon

public val ContactIcon.DocumentCopy: ImageVector
    get() {
        if (_documentCopy != null) {
            return _documentCopy!!
        }
        _documentCopy = Builder(name = "DocumentCopy", defaultWidth = 16.0.dp, defaultHeight =
                16.0.dp, viewportWidth = 16.0f, viewportHeight = 16.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFB0B0B0)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(11.334f, 8.934f)
                verticalLineTo(10.934f)
                curveTo(11.334f, 13.6f, 10.267f, 14.667f, 7.601f, 14.667f)
                horizontalLineTo(5.067f)
                curveTo(2.401f, 14.667f, 1.334f, 13.6f, 1.334f, 10.934f)
                verticalLineTo(8.4f)
                curveTo(1.334f, 5.734f, 2.401f, 4.667f, 5.067f, 4.667f)
                horizontalLineTo(7.067f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFB0B0B0)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(11.333f, 8.934f)
                horizontalLineTo(9.2f)
                curveTo(7.6f, 8.934f, 7.066f, 8.4f, 7.066f, 6.8f)
                verticalLineTo(4.667f)
                lineTo(11.333f, 8.934f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFB0B0B0)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(7.732f, 1.333f)
                horizontalLineTo(10.399f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFB0B0B0)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(4.666f, 3.333f)
                curveTo(4.666f, 2.226f, 5.559f, 1.333f, 6.666f, 1.333f)
                horizontalLineTo(8.413f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFB0B0B0)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(14.666f, 5.333f)
                verticalLineTo(9.46f)
                curveTo(14.666f, 10.493f, 13.826f, 11.333f, 12.793f, 11.333f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFB0B0B0)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(14.666f, 5.333f)
                horizontalLineTo(12.666f)
                curveTo(11.166f, 5.333f, 10.666f, 4.833f, 10.666f, 3.333f)
                verticalLineTo(1.333f)
                lineTo(14.666f, 5.333f)
                close()
            }
        }
        .build()
        return _documentCopy!!
    }

private var _documentCopy: ImageVector? = null

