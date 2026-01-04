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

public val ContactIcon.Phone: ImageVector
    get() {
        if (_phone != null) {
            return _phone!!
        }
        _phone = Builder(name = "Phone", defaultWidth = 16.0.dp, defaultHeight = 16.0.dp,
                viewportWidth = 16.0f, viewportHeight = 16.0f).apply {
            path(fill = SolidColor(Color(0xFF6D6D6D)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(7.367f, 9.966f)
                lineTo(6.134f, 11.2f)
                curveTo(5.874f, 11.46f, 5.461f, 11.46f, 5.194f, 11.206f)
                curveTo(5.121f, 11.133f, 5.047f, 11.066f, 4.974f, 10.993f)
                curveTo(4.287f, 10.3f, 3.667f, 9.573f, 3.114f, 8.813f)
                curveTo(2.567f, 8.053f, 2.127f, 7.293f, 1.807f, 6.54f)
                curveTo(1.494f, 5.78f, 1.334f, 5.053f, 1.334f, 4.36f)
                curveTo(1.334f, 3.906f, 1.414f, 3.473f, 1.574f, 3.073f)
                curveTo(1.734f, 2.666f, 1.987f, 2.293f, 2.341f, 1.96f)
                curveTo(2.767f, 1.54f, 3.234f, 1.333f, 3.727f, 1.333f)
                curveTo(3.914f, 1.333f, 4.101f, 1.373f, 4.267f, 1.453f)
                curveTo(4.441f, 1.533f, 4.594f, 1.653f, 4.714f, 1.826f)
                lineTo(6.261f, 4.006f)
                curveTo(6.381f, 4.173f, 6.467f, 4.326f, 6.527f, 4.473f)
                curveTo(6.587f, 4.613f, 6.621f, 4.753f, 6.621f, 4.88f)
                curveTo(6.621f, 5.04f, 6.574f, 5.2f, 6.481f, 5.353f)
                curveTo(6.394f, 5.506f, 6.267f, 5.666f, 6.107f, 5.826f)
                lineTo(5.601f, 6.353f)
                curveTo(5.527f, 6.426f, 5.494f, 6.513f, 5.494f, 6.62f)
                curveTo(5.494f, 6.673f, 5.501f, 6.72f, 5.514f, 6.773f)
                curveTo(5.534f, 6.826f, 5.554f, 6.866f, 5.567f, 6.906f)
                curveTo(5.687f, 7.126f, 5.894f, 7.413f, 6.187f, 7.76f)
                curveTo(6.487f, 8.106f, 6.807f, 8.46f, 7.154f, 8.813f)
                curveTo(7.221f, 8.88f, 7.294f, 8.946f, 7.361f, 9.013f)
                curveTo(7.627f, 9.273f, 7.634f, 9.7f, 7.367f, 9.966f)
                close()
            }
            path(fill = SolidColor(Color(0xFF6D6D6D)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(14.647f, 12.22f)
                curveTo(14.647f, 12.407f, 14.614f, 12.6f, 14.547f, 12.787f)
                curveTo(14.527f, 12.84f, 14.507f, 12.894f, 14.48f, 12.947f)
                curveTo(14.367f, 13.187f, 14.22f, 13.414f, 14.027f, 13.627f)
                curveTo(13.7f, 13.987f, 13.34f, 14.247f, 12.934f, 14.414f)
                curveTo(12.927f, 14.414f, 12.92f, 14.42f, 12.914f, 14.42f)
                curveTo(12.52f, 14.58f, 12.094f, 14.667f, 11.634f, 14.667f)
                curveTo(10.954f, 14.667f, 10.227f, 14.507f, 9.46f, 14.18f)
                curveTo(8.694f, 13.854f, 7.927f, 13.414f, 7.167f, 12.86f)
                curveTo(6.907f, 12.667f, 6.647f, 12.474f, 6.4f, 12.267f)
                lineTo(8.58f, 10.087f)
                curveTo(8.767f, 10.227f, 8.934f, 10.334f, 9.074f, 10.407f)
                curveTo(9.107f, 10.42f, 9.147f, 10.44f, 9.194f, 10.46f)
                curveTo(9.247f, 10.48f, 9.3f, 10.487f, 9.36f, 10.487f)
                curveTo(9.474f, 10.487f, 9.56f, 10.447f, 9.634f, 10.374f)
                lineTo(10.14f, 9.874f)
                curveTo(10.307f, 9.707f, 10.467f, 9.58f, 10.62f, 9.5f)
                curveTo(10.774f, 9.407f, 10.927f, 9.36f, 11.094f, 9.36f)
                curveTo(11.22f, 9.36f, 11.354f, 9.387f, 11.5f, 9.447f)
                curveTo(11.647f, 9.507f, 11.8f, 9.594f, 11.967f, 9.707f)
                lineTo(14.174f, 11.274f)
                curveTo(14.347f, 11.394f, 14.467f, 11.534f, 14.54f, 11.7f)
                curveTo(14.607f, 11.867f, 14.647f, 12.034f, 14.647f, 12.22f)
                close()
            }
        }
        .build()
        return _phone!!
    }

private var _phone: ImageVector? = null
