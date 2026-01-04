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

public val ContactIcon.ClearText: ImageVector
    get() {
        if (_clearText != null) {
            return _clearText!!
        }
        _clearText = Builder(name = "ClearText", defaultWidth = 14.0.dp, defaultHeight = 14.0.dp,
                viewportWidth = 14.0f, viewportHeight = 14.0f).apply {
            path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(11.401f, 10.474f)
                curveTo(11.525f, 10.597f, 11.594f, 10.764f, 11.594f, 10.938f)
                curveTo(11.594f, 11.113f, 11.525f, 11.28f, 11.401f, 11.403f)
                curveTo(11.278f, 11.526f, 11.111f, 11.596f, 10.937f, 11.596f)
                curveTo(10.762f, 11.596f, 10.595f, 11.526f, 10.472f, 11.403f)
                lineTo(7.0f, 7.93f)
                lineTo(3.527f, 11.402f)
                curveTo(3.403f, 11.525f, 3.236f, 11.595f, 3.062f, 11.595f)
                curveTo(2.887f, 11.595f, 2.72f, 11.525f, 2.597f, 11.402f)
                curveTo(2.474f, 11.279f, 2.404f, 11.112f, 2.404f, 10.937f)
                curveTo(2.404f, 10.763f, 2.474f, 10.596f, 2.597f, 10.472f)
                lineTo(6.07f, 7.0f)
                lineTo(2.598f, 3.527f)
                curveTo(2.475f, 3.404f, 2.405f, 3.237f, 2.405f, 3.062f)
                curveTo(2.405f, 2.888f, 2.475f, 2.721f, 2.598f, 2.597f)
                curveTo(2.721f, 2.474f, 2.888f, 2.405f, 3.063f, 2.405f)
                curveTo(3.237f, 2.405f, 3.404f, 2.474f, 3.528f, 2.597f)
                lineTo(7.0f, 6.071f)
                lineTo(10.473f, 2.597f)
                curveTo(10.596f, 2.474f, 10.763f, 2.404f, 10.938f, 2.404f)
                curveTo(11.112f, 2.404f, 11.279f, 2.474f, 11.403f, 2.597f)
                curveTo(11.526f, 2.72f, 11.595f, 2.887f, 11.595f, 3.062f)
                curveTo(11.595f, 3.236f, 11.526f, 3.403f, 11.403f, 3.527f)
                lineTo(7.929f, 7.0f)
                lineTo(11.401f, 10.474f)
                close()
            }
        }
        .build()
        return _clearText!!
    }

private var _clearText: ImageVector? = null