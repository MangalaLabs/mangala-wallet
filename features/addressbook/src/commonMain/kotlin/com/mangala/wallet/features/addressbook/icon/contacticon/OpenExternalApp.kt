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

public val ContactIcon.OpenExternalApp: ImageVector
    get() {
        if (_openExternalApp != null) {
            return _openExternalApp!!
        }
        _openExternalApp = Builder(
            name = "OpenExternalApp", defaultWidth = 16.0.dp, defaultHeight =
                16.0.dp, viewportWidth = 16.0f, viewportHeight = 16.0f
        ).apply {
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFB0B0B0)),
                strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(8.666f, 7.333f)
                lineTo(14.133f, 1.866f)
            }
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFB0B0B0)),
                strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(14.667f, 4.533f)
                verticalLineTo(1.333f)
                horizontalLineTo(11.467f)
            }
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFB0B0B0)),
                strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(7.334f, 1.333f)
                horizontalLineTo(6.001f)
                curveTo(2.667f, 1.333f, 1.334f, 2.666f, 1.334f, 6.0f)
                verticalLineTo(10.0f)
                curveTo(1.334f, 13.333f, 2.667f, 14.666f, 6.001f, 14.666f)
                horizontalLineTo(10.001f)
                curveTo(13.334f, 14.666f, 14.667f, 13.333f, 14.667f, 10.0f)
                verticalLineTo(8.666f)
            }
        }
            .build()
        return _openExternalApp!!
    }

private var _openExternalApp: ImageVector? = null