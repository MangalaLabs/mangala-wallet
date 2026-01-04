package com.mangala.wallet.features.addressbook.icon.contacticon

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.features.addressbook.icon.ContactIcon

public val ContactIcon.Circle: ImageVector
    get() {
        if (_circle != null) {
            return _circle!!
        }
        _circle = Builder(name = "Circle", defaultWidth = 32.0.dp, defaultHeight = 32.0.dp,
                viewportWidth = 32.0f, viewportHeight = 32.0f).apply {
            path(fill = linearGradient(0.0f to Color(0xFFB0BACC), 1.0f to Color(0xFF969EAE), start =
                    Offset(0.165f,16.32f), end = Offset(31.956f,16.32f)), stroke = null,
                    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(31.594f, 19.515f)
                curveTo(31.269f, 20.873f, 30.782f, 22.311f, 30.215f, 23.508f)
                curveTo(28.673f, 26.463f, 26.238f, 28.779f, 23.236f, 30.297f)
                curveTo(20.152f, 31.814f, 16.501f, 32.453f, 12.849f, 31.654f)
                curveTo(4.248f, 29.897f, -1.27f, 21.592f, 0.515f, 13.126f)
                curveTo(2.3f, 4.661f, 10.658f, -0.85f, 19.26f, 0.987f)
                curveTo(22.343f, 1.626f, 25.021f, 3.144f, 27.293f, 5.22f)
                curveTo(31.107f, 8.974f, 32.73f, 14.404f, 31.594f, 19.515f)
                close()
            }
        }
        .build()
        return _circle!!
    }

private var _circle: ImageVector? = null

