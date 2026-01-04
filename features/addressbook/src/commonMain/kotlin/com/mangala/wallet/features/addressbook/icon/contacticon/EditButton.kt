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

public val ContactIcon.EditButton: ImageVector
    get() {
        if (_editButton != null) {
            return _editButton!!
        }
        _editButton = Builder(name = "EditButton", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF262626)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(13.475f, 5.344f)
                lineTo(5.171f, 13.649f)
                curveTo(4.843f, 13.977f, 4.538f, 14.587f, 4.467f, 15.033f)
                lineTo(4.021f, 18.199f)
                curveTo(3.857f, 19.349f, 4.655f, 20.147f, 5.804f, 19.982f)
                lineTo(8.971f, 19.537f)
                curveTo(9.417f, 19.466f, 10.05f, 19.161f, 10.355f, 18.833f)
                lineTo(18.66f, 10.529f)
                curveTo(20.091f, 9.098f, 20.771f, 7.432f, 18.66f, 5.321f)
                curveTo(16.572f, 3.233f, 14.906f, 3.913f, 13.475f, 5.344f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF262626)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(12.279f, 6.541f)
                curveTo(12.983f, 9.075f, 14.954f, 11.045f, 17.487f, 11.749f)
            }
        }
        .build()
        return _editButton!!
    }

private var _editButton: ImageVector? = null

