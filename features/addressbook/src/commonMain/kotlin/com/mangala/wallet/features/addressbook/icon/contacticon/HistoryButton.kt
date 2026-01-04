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

public val ContactIcon.HistoryButton: ImageVector
    get() {
        if (_historyButton != null) {
            return _historyButton!!
        }
        _historyButton = Builder(name = "HistoryButton", defaultWidth = 20.0.dp, defaultHeight =
                20.0.dp, viewportWidth = 20.0f, viewportHeight = 20.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF262626)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(18.333f, 8.334f)
                verticalLineTo(12.5f)
                curveTo(18.333f, 16.667f, 16.666f, 18.334f, 12.499f, 18.334f)
                horizontalLineTo(7.499f)
                curveTo(3.333f, 18.334f, 1.666f, 16.667f, 1.666f, 12.5f)
                verticalLineTo(7.5f)
                curveTo(1.666f, 3.334f, 3.333f, 1.667f, 7.499f, 1.667f)
                horizontalLineTo(11.666f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF262626)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(18.333f, 8.334f)
                horizontalLineTo(14.999f)
                curveTo(12.499f, 8.334f, 11.666f, 7.5f, 11.666f, 5.0f)
                verticalLineTo(1.667f)
                lineTo(18.333f, 8.334f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF262626)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(5.834f, 10.833f)
                horizontalLineTo(10.834f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF262626)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(5.834f, 14.167f)
                horizontalLineTo(9.167f)
            }
        }
        .build()
        return _historyButton!!
    }

private var _historyButton: ImageVector? = null