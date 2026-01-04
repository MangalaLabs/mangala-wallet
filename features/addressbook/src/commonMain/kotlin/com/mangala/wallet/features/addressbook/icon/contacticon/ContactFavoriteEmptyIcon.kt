package com.mangala.wallet.features.addressbook.icon.contacticon

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
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

public val ContactIcon.ContactFavoriteEmptyIcon: ImageVector
    get() {
        if (_contactFavoriteEmptyIcon != null) {
            return _contactFavoriteEmptyIcon!!
        }
        _contactFavoriteEmptyIcon = Builder(name = "ContactFavoriteEmptyIcon", defaultWidth =
                87.0.dp, defaultHeight = 85.0.dp, viewportWidth = 87.0f, viewportHeight =
                85.0f).apply {
            path(fill = SolidColor(Color(0xFFEAEEF9)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(86.5f, 42.32f)
                curveTo(86.5f, 52.666f, 82.754f, 62.077f, 76.491f, 69.451f)
                curveTo(68.607f, 78.752f, 56.752f, 84.64f, 43.444f, 84.64f)
                curveTo(30.695f, 84.64f, 19.288f, 79.192f, 11.404f, 70.552f)
                curveTo(4.638f, 63.067f, 0.5f, 53.161f, 0.5f, 42.32f)
                curveTo(0.5f, 18.931f, 19.735f, 0.0f, 43.5f, 0.0f)
                curveTo(67.265f, 0.0f, 86.5f, 18.931f, 86.5f, 42.32f)
                close()
            }
            path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(76.548f, 69.451f)
                curveTo(68.664f, 78.752f, 56.809f, 84.641f, 43.501f, 84.641f)
                curveTo(30.752f, 84.641f, 19.345f, 79.192f, 11.461f, 70.552f)
                curveTo(12.803f, 69.507f, 14.48f, 68.571f, 16.549f, 67.745f)
                lineTo(18.115f, 67.14f)
                curveTo(21.079f, 65.984f, 24.937f, 64.554f, 30.137f, 62.407f)
                curveTo(31.311f, 61.912f, 32.262f, 61.032f, 32.765f, 59.876f)
                curveTo(33.045f, 59.271f, 33.213f, 58.555f, 33.213f, 57.84f)
                verticalLineTo(48.319f)
                curveTo(33.045f, 48.099f, 32.877f, 47.934f, 32.765f, 47.714f)
                curveTo(31.927f, 46.503f, 31.367f, 45.072f, 31.2f, 43.531f)
                lineTo(30.361f, 43.146f)
                curveTo(27.845f, 43.751f, 28.012f, 41.11f, 27.397f, 36.047f)
                curveTo(27.118f, 33.956f, 27.453f, 33.515f, 28.404f, 33.185f)
                lineTo(29.187f, 32.139f)
                curveTo(24.378f, 21.408f, 26.782f, 14.694f, 31.535f, 12.328f)
                curveTo(29.969f, 8.916f, 29.858f, 7.705f, 29.858f, 7.705f)
                curveTo(29.858f, 7.705f, 39.979f, 9.356f, 43.501f, 8.696f)
                curveTo(47.919f, 7.815f, 54.741f, 8.861f, 57.257f, 14.639f)
                curveTo(61.451f, 16.29f, 63.128f, 18.877f, 63.464f, 21.738f)
                curveTo(64.079f, 26.251f, 61.451f, 31.204f, 60.947f, 33.13f)
                curveTo(61.003f, 33.24f, 61.059f, 33.295f, 61.059f, 33.35f)
                curveTo(61.73f, 33.68f, 61.954f, 34.231f, 61.73f, 36.047f)
                curveTo(61.115f, 40.835f, 61.283f, 43.806f, 58.766f, 43.146f)
                lineTo(56.53f, 46.998f)
                curveTo(56.418f, 47.438f, 56.418f, 47.604f, 56.362f, 47.934f)
                curveTo(56.25f, 48.649f, 56.306f, 50.08f, 56.306f, 57.95f)
                curveTo(56.306f, 58.885f, 56.586f, 59.766f, 57.089f, 60.591f)
                curveTo(57.648f, 61.417f, 58.431f, 62.132f, 59.438f, 62.517f)
                curveTo(59.438f, 62.517f, 59.493f, 62.517f, 59.549f, 62.572f)
                curveTo(65.309f, 64.939f, 69.447f, 66.48f, 72.466f, 67.635f)
                curveTo(74.032f, 68.131f, 75.374f, 68.791f, 76.548f, 69.451f)
                close()
            }
            path(fill = linearGradient(0.0f to Color(0xFFB0BACC), 1.0f to Color(0xFF969EAE), start =
                    Offset(26.657f,20.393f), end = Offset(63.62f,20.393f)), stroke = null,
                    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(61.059f, 33.075f)
                curveTo(61.283f, 31.864f, 60.723f, 30.378f, 60.276f, 29.333f)
                curveTo(60.22f, 29.168f, 60.108f, 29.003f, 60.052f, 28.782f)
                curveTo(58.095f, 24.93f, 53.79f, 23.389f, 49.652f, 23.169f)
                curveTo(39.084f, 22.619f, 38.133f, 24.6f, 34.834f, 21.628f)
                curveTo(35.84f, 23.169f, 35.952f, 25.536f, 34.387f, 28.507f)
                curveTo(33.268f, 30.599f, 31.143f, 31.534f, 29.298f, 32.139f)
                curveTo(24.489f, 21.408f, 26.894f, 14.694f, 31.647f, 12.328f)
                curveTo(30.081f, 8.916f, 29.969f, 7.705f, 29.969f, 7.705f)
                curveTo(29.969f, 7.705f, 40.09f, 9.356f, 43.557f, 8.696f)
                curveTo(47.974f, 7.815f, 54.796f, 8.861f, 57.313f, 14.639f)
                curveTo(61.506f, 16.29f, 63.128f, 18.877f, 63.519f, 21.738f)
                curveTo(64.134f, 26.196f, 61.562f, 31.094f, 61.059f, 33.075f)
                close()
            }
            path(fill = linearGradient(0.0f to Color(0xFFFFFFFF), 1.0f to Color(0xFFE2E5EC), start =
                    Offset(44.991f,62.444f), end = Offset(44.991f,54.077f)), stroke = null,
                    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(57.201f, 60.481f)
                verticalLineTo(62.352f)
                horizontalLineTo(32.766f)
                verticalLineTo(59.931f)
                curveTo(33.045f, 59.325f, 33.213f, 58.61f, 33.213f, 57.894f)
                verticalLineTo(48.319f)
                curveTo(33.045f, 48.099f, 32.877f, 47.933f, 32.766f, 47.713f)
                verticalLineTo(47.163f)
                curveTo(33.269f, 48.044f, 33.94f, 48.869f, 34.779f, 49.529f)
                lineTo(42.439f, 54.758f)
                curveTo(44.229f, 56.243f, 46.801f, 56.299f, 48.646f, 54.868f)
                lineTo(55.803f, 48.539f)
                curveTo(56.027f, 48.319f, 56.307f, 48.099f, 56.53f, 47.879f)
                curveTo(56.418f, 48.594f, 56.474f, 50.025f, 56.474f, 57.894f)
                curveTo(56.418f, 58.83f, 56.698f, 59.765f, 57.201f, 60.481f)
                close()
            }
        }
        .build()
        return _contactFavoriteEmptyIcon!!
    }

private var _contactFavoriteEmptyIcon: ImageVector? = null

