package com.mangala.wallet.features.addressbook.icon.contacticon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.features.addressbook.icon.ContactIcon

public val ContactIcon.TagUser: ImageVector
    get() {
        if (_tagUser != null) {
            return _tagUser!!
        }
        _tagUser = Builder(name = "TagUser", defaultWidth = 16.0.dp, defaultHeight = 17.0.dp,
                viewportWidth = 16.0f, viewportHeight = 17.0f).apply {
            path(fill = SolidColor(Color(0xFF6D6D6D)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(12.0f, 1.833f)
                horizontalLineTo(4.0f)
                curveTo(2.893f, 1.833f, 2.0f, 2.72f, 2.0f, 3.813f)
                verticalLineTo(11.086f)
                curveTo(2.0f, 12.18f, 2.893f, 13.073f, 4.0f, 13.073f)
                horizontalLineTo(4.507f)
                curveTo(5.033f, 13.073f, 5.547f, 13.28f, 5.92f, 13.653f)
                lineTo(7.06f, 14.78f)
                curveTo(7.58f, 15.293f, 8.42f, 15.293f, 8.94f, 14.78f)
                lineTo(10.08f, 13.653f)
                curveTo(10.453f, 13.28f, 10.967f, 13.073f, 11.493f, 13.073f)
                horizontalLineTo(12.0f)
                curveTo(13.107f, 13.073f, 14.0f, 12.18f, 14.0f, 11.086f)
                verticalLineTo(3.813f)
                curveTo(14.0f, 2.72f, 13.107f, 1.833f, 12.0f, 1.833f)
                close()
                moveTo(8.0f, 4.2f)
                curveTo(8.72f, 4.2f, 9.3f, 4.786f, 9.3f, 5.5f)
                curveTo(9.3f, 6.206f, 8.74f, 6.773f, 8.047f, 6.8f)
                curveTo(8.02f, 6.8f, 7.98f, 6.8f, 7.947f, 6.8f)
                curveTo(7.247f, 6.773f, 6.693f, 6.206f, 6.693f, 5.5f)
                curveTo(6.7f, 4.786f, 7.28f, 4.2f, 8.0f, 4.2f)
                close()
                moveTo(9.833f, 10.293f)
                curveTo(8.827f, 10.966f, 7.173f, 10.966f, 6.167f, 10.293f)
                curveTo(5.28f, 9.706f, 5.28f, 8.733f, 6.167f, 8.14f)
                curveTo(7.18f, 7.466f, 8.833f, 7.466f, 9.833f, 8.14f)
                curveTo(10.72f, 8.733f, 10.72f, 9.7f, 9.833f, 10.293f)
                close()
            }
        }
        .build()
        return _tagUser!!
    }

private var _tagUser: ImageVector? = null
