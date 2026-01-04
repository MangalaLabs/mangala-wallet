package com.mangala.wallet.features.addressbook.icon.contacticon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import kotlin.Unit

public val ContactIcon.DropDown: ImageVector
    get() {
        if (_dropdown != null) {
            return _dropdown!!
        }
        _dropdown = Builder(name = "Dropdown", defaultWidth = 20.0.dp, defaultHeight = 20.0.dp,
                viewportWidth = 20.0f, viewportHeight = 20.0f).apply {
            path(fill = SolidColor(Color(0xFF6D6D6D)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(14.934f, 6.816f)
                horizontalLineTo(9.742f)
                horizontalLineTo(5.067f)
                curveTo(4.267f, 6.816f, 3.867f, 7.783f, 4.434f, 8.35f)
                lineTo(8.75f, 12.666f)
                curveTo(9.442f, 13.358f, 10.567f, 13.358f, 11.259f, 12.666f)
                lineTo(12.9f, 11.025f)
                lineTo(15.575f, 8.35f)
                curveTo(16.134f, 7.783f, 15.734f, 6.816f, 14.934f, 6.816f)
                close()
            }
        }
        .build()
        return _dropdown!!
    }

private var _dropdown: ImageVector? = null

