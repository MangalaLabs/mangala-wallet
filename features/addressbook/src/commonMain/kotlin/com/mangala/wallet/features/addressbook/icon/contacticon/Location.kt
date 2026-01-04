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

public val ContactIcon.Location: ImageVector
    get() {
        if (_location != null) {
            return _location!!
        }
        _location = Builder(name = "Location", defaultWidth = 16.0.dp, defaultHeight = 16.0.dp,
                viewportWidth = 16.0f, viewportHeight = 16.0f).apply {
            path(fill = SolidColor(Color(0xFF6D6D6D)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(13.747f, 5.634f)
                curveTo(13.047f, 2.554f, 10.36f, 1.167f, 8.0f, 1.167f)
                curveTo(8.0f, 1.167f, 8.0f, 1.167f, 7.993f, 1.167f)
                curveTo(5.64f, 1.167f, 2.947f, 2.547f, 2.247f, 5.627f)
                curveTo(1.467f, 9.067f, 3.573f, 11.98f, 5.48f, 13.814f)
                curveTo(6.187f, 14.494f, 7.093f, 14.834f, 8.0f, 14.834f)
                curveTo(8.907f, 14.834f, 9.813f, 14.494f, 10.513f, 13.814f)
                curveTo(12.42f, 11.98f, 14.527f, 9.074f, 13.747f, 5.634f)
                close()
                moveTo(8.0f, 8.974f)
                curveTo(6.84f, 8.974f, 5.9f, 8.034f, 5.9f, 6.874f)
                curveTo(5.9f, 5.714f, 6.84f, 4.774f, 8.0f, 4.774f)
                curveTo(9.16f, 4.774f, 10.1f, 5.714f, 10.1f, 6.874f)
                curveTo(10.1f, 8.034f, 9.16f, 8.974f, 8.0f, 8.974f)
                close()
            }
        }
        .build()
        return _location!!
    }

private var _location: ImageVector? = null
