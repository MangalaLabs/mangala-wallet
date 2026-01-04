package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

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

public val MangalaWalletPack.EditNew: ImageVector
    get() {
        if (_editNew != null) {
            return _editNew!!
        }
        _editNew = Builder(name = "My icon", defaultWidth = 20.0.dp, defaultHeight = 20.0.dp,
            viewportWidth = 20.0f, viewportHeight = 20.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF6D6D6D)),
                strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(11.2293f, 4.4532f)
                lineTo(4.309f, 11.3735f)
                curveTo(4.0353f, 11.6472f, 3.7812f, 12.1554f, 3.7225f, 12.5268f)
                lineTo(3.3511f, 15.166f)
                curveTo(3.2143f, 16.1238f, 3.8789f, 16.7885f, 4.8368f, 16.6517f)
                lineTo(7.4759f, 16.2802f)
                curveTo(7.8474f, 16.2216f, 8.3752f, 15.9674f, 8.6293f, 15.6937f)
                lineTo(15.5496f, 8.7735f)
                curveTo(16.7421f, 7.581f, 17.309f, 6.1931f, 15.5496f, 4.4337f)
                curveTo(13.8098f, 2.6938f, 12.4218f, 3.2607f, 11.2293f, 4.4532f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF6D6D6D)),
                strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(10.2327f, 5.4512f)
                curveTo(10.8191f, 7.5624f, 12.4612f, 9.2045f, 14.5725f, 9.791f)
            }
        }
            .build()
        return _editNew!!
    }

private var _editNew: ImageVector? = null