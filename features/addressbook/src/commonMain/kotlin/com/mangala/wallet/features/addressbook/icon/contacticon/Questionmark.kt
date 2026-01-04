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
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import kotlin.Unit

public val ContactIcon.QuestionMark: ImageVector
    get() {
        if (_questionmark != null) {
            return _questionmark!!
        }
        _questionmark = Builder(name = "Questionmark", defaultWidth = 14.0.dp, defaultHeight =
                15.0.dp, viewportWidth = 14.0f, viewportHeight = 15.0f).apply {
            group {
                path(fill = SolidColor(Color(0xFFB0B0B0)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(7.0f, 0.5f)
                    curveTo(3.131f, 0.5f, 0.0f, 3.631f, 0.0f, 7.5f)
                    curveTo(0.0f, 11.369f, 3.131f, 14.5f, 7.0f, 14.5f)
                    curveTo(10.869f, 14.5f, 14.0f, 11.369f, 14.0f, 7.5f)
                    curveTo(14.0f, 3.631f, 10.869f, 0.5f, 7.0f, 0.5f)
                    close()
                    moveTo(6.793f, 10.776f)
                    curveTo(6.397f, 10.776f, 6.093f, 10.445f, 6.093f, 10.067f)
                    curveTo(6.093f, 9.68f, 6.406f, 9.357f, 6.793f, 9.357f)
                    curveTo(7.18f, 9.357f, 7.502f, 9.68f, 7.502f, 10.067f)
                    curveTo(7.502f, 10.445f, 7.189f, 10.776f, 6.793f, 10.776f)
                    close()
                    moveTo(7.843f, 7.192f)
                    curveTo(7.336f, 7.588f, 7.327f, 7.865f, 7.327f, 8.344f)
                    curveTo(7.327f, 8.519f, 7.235f, 8.722f, 6.784f, 8.722f)
                    curveTo(6.406f, 8.722f, 6.277f, 8.583f, 6.277f, 8.104f)
                    curveTo(6.277f, 7.312f, 6.627f, 6.934f, 6.894f, 6.704f)
                    curveTo(7.198f, 6.446f, 7.714f, 6.16f, 7.714f, 5.662f)
                    curveTo(7.714f, 5.239f, 7.346f, 5.036f, 6.885f, 5.036f)
                    curveTo(5.945f, 5.036f, 6.148f, 5.745f, 5.65f, 5.745f)
                    curveTo(5.402f, 5.745f, 5.097f, 5.58f, 5.097f, 5.22f)
                    curveTo(5.097f, 4.723f, 5.669f, 3.986f, 6.913f, 3.986f)
                    curveTo(8.092f, 3.986f, 8.875f, 4.64f, 8.875f, 5.506f)
                    curveTo(8.875f, 6.372f, 8.092f, 6.999f, 7.843f, 7.192f)
                    close()
                }
            }
        }
        .build()
        return _questionmark!!
    }

private var _questionmark: ImageVector? = null
