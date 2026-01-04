package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.Notification: ImageVector
    get() {
        if (_notification != null) {
            return _notification!!
        }
        _notification = Builder(name = "Notification", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                moveTo(3.5008f, 13.7871f)
                verticalLineTo(13.5681f)
                curveTo(3.5329f, 12.9202f, 3.7406f, 12.2925f, 4.1024f, 11.7496f)
                curveTo(4.7045f, 11.0975f, 5.1167f, 10.2983f, 5.2957f, 9.436f)
                curveTo(5.2957f, 8.7695f, 5.2957f, 8.0935f, 5.3539f, 7.427f)
                curveTo(5.6547f, 4.2184f, 8.8273f, 2.0f, 11.9611f, 2.0f)
                horizontalLineTo(12.0387f)
                curveTo(15.1725f, 2.0f, 18.345f, 4.2184f, 18.6555f, 7.427f)
                curveTo(18.7137f, 8.0935f, 18.6555f, 8.7695f, 18.704f, 9.436f)
                curveTo(18.8854f, 10.3003f, 19.2972f, 11.1019f, 19.8974f, 11.7591f)
                curveTo(20.2618f, 12.2972f, 20.4698f, 12.9227f, 20.4989f, 13.5681f)
                verticalLineTo(13.7776f)
                curveTo(20.5206f, 14.648f, 20.2208f, 15.4968f, 19.6548f, 16.1674f)
                curveTo(18.907f, 16.9515f, 17.8921f, 17.4393f, 16.8024f, 17.5384f)
                curveTo(13.607f, 17.8812f, 10.383f, 17.8812f, 7.1876f, 17.5384f)
                curveTo(6.0991f, 17.435f, 5.0858f, 16.9479f, 4.3352f, 16.1674f)
                curveTo(3.778f, 15.4963f, 3.4822f, 14.6526f, 3.5008f, 13.7871f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(9.5547f, 20.8518f)
                curveTo(10.054f, 21.4785f, 10.7871f, 21.884f, 11.592f, 21.9788f)
                curveTo(12.3968f, 22.0735f, 13.2069f, 21.8495f, 13.843f, 21.3564f)
                curveTo(14.0387f, 21.2106f, 14.2147f, 21.041f, 14.3669f, 20.8518f)
            }
        }
        .build()
        return _notification!!
    }

private var _notification: ImageVector? = null
