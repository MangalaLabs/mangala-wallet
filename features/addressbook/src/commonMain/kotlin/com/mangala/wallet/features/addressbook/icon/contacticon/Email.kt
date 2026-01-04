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

public val ContactIcon.Email: ImageVector
    get() {
        if (_email != null) {
            return _email!!
        }
        _email = Builder(name = "Email", defaultWidth = 16.0.dp, defaultHeight = 17.0.dp,
                viewportWidth = 16.0f, viewportHeight = 17.0f).apply {
            path(fill = SolidColor(Color(0xFF6D6D6D)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(11.334f, 2.833f)
                horizontalLineTo(4.667f)
                curveTo(2.667f, 2.833f, 1.334f, 3.833f, 1.334f, 6.166f)
                verticalLineTo(10.833f)
                curveTo(1.334f, 13.166f, 2.667f, 14.166f, 4.667f, 14.166f)
                horizontalLineTo(11.334f)
                curveTo(13.334f, 14.166f, 14.667f, 13.166f, 14.667f, 10.833f)
                verticalLineTo(6.166f)
                curveTo(14.667f, 3.833f, 13.334f, 2.833f, 11.334f, 2.833f)
                close()
                moveTo(11.647f, 6.893f)
                lineTo(9.561f, 8.56f)
                curveTo(9.121f, 8.913f, 8.561f, 9.086f, 8.001f, 9.086f)
                curveTo(7.441f, 9.086f, 6.874f, 8.913f, 6.441f, 8.56f)
                lineTo(4.354f, 6.893f)
                curveTo(4.141f, 6.72f, 4.107f, 6.4f, 4.274f, 6.186f)
                curveTo(4.447f, 5.973f, 4.761f, 5.933f, 4.974f, 6.106f)
                lineTo(7.061f, 7.773f)
                curveTo(7.567f, 8.18f, 8.427f, 8.18f, 8.934f, 7.773f)
                lineTo(11.021f, 6.106f)
                curveTo(11.234f, 5.933f, 11.554f, 5.966f, 11.721f, 6.186f)
                curveTo(11.894f, 6.4f, 11.861f, 6.72f, 11.647f, 6.893f)
                close()
            }
        }
        .build()
        return _email!!
    }

private var _email: ImageVector? = null


