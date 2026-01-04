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

public val ContactIcon.DeleteButton: ImageVector
    get() {
        if (_deleteButton != null) {
            return _deleteButton!!
        }
        _deleteButton = Builder(name = "DeleteButton", defaultWidth = 19.0.dp, defaultHeight =
                20.0.dp, viewportWidth = 19.0f, viewportHeight = 20.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFFA0000)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(16.324f, 7.469f)
                curveTo(16.324f, 7.469f, 15.781f, 14.204f, 15.466f, 17.041f)
                curveTo(15.316f, 18.396f, 14.479f, 19.19f, 13.108f, 19.215f)
                curveTo(10.499f, 19.262f, 7.887f, 19.265f, 5.279f, 19.21f)
                curveTo(3.96f, 19.183f, 3.137f, 18.379f, 2.99f, 17.048f)
                curveTo(2.673f, 14.186f, 2.133f, 7.469f, 2.133f, 7.469f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFFA0000)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(17.708f, 4.24f)
                horizontalLineTo(0.75f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFFA0000)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(14.441f, 4.24f)
                curveTo(13.656f, 4.24f, 12.98f, 3.685f, 12.826f, 2.916f)
                lineTo(12.583f, 1.7f)
                curveTo(12.433f, 1.139f, 11.925f, 0.751f, 11.346f, 0.751f)
                horizontalLineTo(7.113f)
                curveTo(6.534f, 0.751f, 6.026f, 1.139f, 5.876f, 1.7f)
                lineTo(5.633f, 2.916f)
                curveTo(5.479f, 3.685f, 4.803f, 4.24f, 4.018f, 4.24f)
            }
        }
        .build()
        return _deleteButton!!
    }

private var _deleteButton: ImageVector? = null
