package com.mangala.wallet.features.addressbook.icon.contacticon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.features.addressbook.icon.ContactIcon

public val ContactIcon.Star: ImageVector
    get() {
        if (_star != null) {
            return _star!!
        }
        _star = Builder(name = "Star", defaultWidth = 16.0.dp, defaultHeight = 16.0.dp,
                viewportWidth = 16.0f, viewportHeight = 16.0f).apply {
            group {
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(15.677f, 6.246f)
                    curveTo(15.575f, 5.931f, 15.296f, 5.708f, 14.966f, 5.678f)
                    lineTo(10.479f, 5.271f)
                    lineTo(8.705f, 1.118f)
                    curveTo(8.575f, 0.814f, 8.277f, 0.617f, 7.946f, 0.617f)
                    curveTo(7.615f, 0.617f, 7.317f, 0.814f, 7.186f, 1.119f)
                    lineTo(5.412f, 5.271f)
                    lineTo(0.925f, 5.678f)
                    curveTo(0.595f, 5.709f, 0.317f, 5.931f, 0.215f, 6.246f)
                    curveTo(0.112f, 6.561f, 0.207f, 6.906f, 0.456f, 7.124f)
                    lineTo(3.847f, 10.098f)
                    lineTo(2.847f, 14.502f)
                    curveTo(2.774f, 14.826f, 2.9f, 15.161f, 3.168f, 15.355f)
                    curveTo(3.313f, 15.46f, 3.482f, 15.513f, 3.652f, 15.513f)
                    curveTo(3.799f, 15.513f, 3.945f, 15.473f, 4.076f, 15.395f)
                    lineTo(7.946f, 13.082f)
                    lineTo(11.814f, 15.395f)
                    curveTo(12.097f, 15.565f, 12.454f, 15.55f, 12.722f, 15.355f)
                    curveTo(12.991f, 15.16f, 13.116f, 14.826f, 13.043f, 14.502f)
                    lineTo(12.043f, 10.098f)
                    lineTo(15.434f, 7.124f)
                    curveTo(15.684f, 6.906f, 15.779f, 6.561f, 15.677f, 6.246f)
                    close()
                }
            }
        }
        .build()
        return _star!!
    }

private var _star: ImageVector? = null
