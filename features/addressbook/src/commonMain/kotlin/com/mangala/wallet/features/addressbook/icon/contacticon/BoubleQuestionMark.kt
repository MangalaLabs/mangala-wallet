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

public val ContactIcon.BoubleQuestionMark: ImageVector
    get() {
        if (_boubleQuestionMark != null) {
            return _boubleQuestionMark!!
        }
        _boubleQuestionMark = Builder(
            name = "BoubleQuestionMark", defaultWidth = 16.0.dp,
            defaultHeight = 16.0.dp, viewportWidth = 16.0f, viewportHeight = 16.0f
        ).apply {
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFB0B0B0)),
                strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(11.334f, 12.287f)
                horizontalLineTo(8.667f)
                lineTo(5.701f, 14.26f)
                curveTo(5.261f, 14.554f, 4.667f, 14.24f, 4.667f, 13.707f)
                verticalLineTo(12.287f)
                curveTo(2.667f, 12.287f, 1.334f, 10.953f, 1.334f, 8.953f)
                verticalLineTo(4.953f)
                curveTo(1.334f, 2.953f, 2.667f, 1.62f, 4.667f, 1.62f)
                horizontalLineTo(11.334f)
                curveTo(13.334f, 1.62f, 14.667f, 2.953f, 14.667f, 4.953f)
                verticalLineTo(8.953f)
                curveTo(14.667f, 10.953f, 13.334f, 12.287f, 11.334f, 12.287f)
                close()
            }
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFB0B0B0)),
                strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(7.999f, 7.573f)
                verticalLineTo(7.433f)
                curveTo(7.999f, 6.98f, 8.279f, 6.74f, 8.559f, 6.547f)
                curveTo(8.833f, 6.36f, 9.106f, 6.12f, 9.106f, 5.68f)
                curveTo(9.106f, 5.067f, 8.613f, 4.573f, 7.999f, 4.573f)
                curveTo(7.386f, 4.573f, 6.893f, 5.067f, 6.893f, 5.68f)
            }
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFB0B0B0)),
                strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(7.996f, 9.166f)
                horizontalLineTo(8.002f)
            }
        }
            .build()
        return _boubleQuestionMark!!
    }

private var _boubleQuestionMark: ImageVector? = null
