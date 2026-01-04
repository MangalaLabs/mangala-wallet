package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import kotlin.Unit

public val MangalaWalletPack.IcCopy: ImageVector
    get() {
        if (_icCopy != null) {
            return _icCopy!!
        }
        _icCopy = Builder(name = "IcCopy", defaultWidth = 21.0.dp, defaultHeight = 20.0.dp,
                viewportWidth = 21.0f, viewportHeight = 20.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF262626)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(6.109f, 16.417f)
                curveTo(6.792f, 15.684f, 7.834f, 15.742f, 8.434f, 16.542f)
                lineTo(9.275f, 17.667f)
                curveTo(9.95f, 18.559f, 11.042f, 18.559f, 11.717f, 17.667f)
                lineTo(12.559f, 16.542f)
                curveTo(13.159f, 15.742f, 14.2f, 15.684f, 14.884f, 16.417f)
                curveTo(16.367f, 18.0f, 17.575f, 17.475f, 17.575f, 15.259f)
                verticalLineTo(5.867f)
                curveTo(17.584f, 2.509f, 16.8f, 1.667f, 13.65f, 1.667f)
                horizontalLineTo(7.35f)
                curveTo(4.2f, 1.667f, 3.417f, 2.509f, 3.417f, 5.867f)
                verticalLineTo(15.25f)
                curveTo(3.417f, 17.475f, 4.634f, 17.992f, 6.109f, 16.417f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF262626)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(7.167f, 5.833f)
                horizontalLineTo(13.834f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF262626)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(8.0f, 9.167f)
                horizontalLineTo(13.0f)
            }
        }
        .build()
        return _icCopy!!
    }

private var _icCopy: ImageVector? = null
