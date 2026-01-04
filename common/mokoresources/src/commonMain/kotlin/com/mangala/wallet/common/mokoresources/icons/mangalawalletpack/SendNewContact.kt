package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.SendNewContact: ImageVector
    get() {
        if (_sendnewcontact != null) {
            return _sendnewcontact!!
        }
        _sendnewcontact = Builder(name = "Sendnewcontact", defaultWidth = 20.0.dp, defaultHeight =
                20.0.dp, viewportWidth = 20.0f, viewportHeight = 20.0f).apply {
            path(fill = SolidColor(Color(0xFF767676)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(9.9998f, 12.5f)
                curveTo(5.8248f, 12.5f, 2.4248f, 15.3f, 2.4248f, 18.75f)
                curveTo(2.4248f, 18.9833f, 2.6081f, 19.1667f, 2.8415f, 19.1667f)
                horizontalLineTo(17.1581f)
                curveTo(17.3915f, 19.1667f, 17.5748f, 18.9833f, 17.5748f, 18.75f)
                curveTo(17.5748f, 15.3f, 14.1748f, 12.5f, 9.9998f, 12.5f)
                close()
            }
            path(fill = SolidColor(Color(0xFF767676)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(13.0913f, 3.0501f)
                curveTo(12.3413f, 2.2001f, 11.2247f, 1.6667f, 9.9997f, 1.6667f)
                curveTo(8.833f, 1.6667f, 7.7663f, 2.1418f, 7.008f, 2.9251f)
                curveTo(6.283f, 3.6751f, 5.833f, 4.7084f, 5.833f, 5.8334f)
                curveTo(5.833f, 6.6167f, 6.0497f, 7.3501f, 6.4413f, 7.9751f)
                curveTo(6.6497f, 8.3334f, 6.9163f, 8.6584f, 7.233f, 8.9251f)
                curveTo(7.958f, 9.5918f, 8.9247f, 10.0001f, 9.9997f, 10.0001f)
                curveTo(11.5247f, 10.0001f, 12.8413f, 9.1834f, 13.5663f, 7.9751f)
                curveTo(13.783f, 7.6167f, 13.9497f, 7.2168f, 14.0413f, 6.8001f)
                curveTo(14.1247f, 6.4917f, 14.1663f, 6.1667f, 14.1663f, 5.8334f)
                curveTo(14.1663f, 4.7667f, 13.758f, 3.7917f, 13.0913f, 3.0501f)
                close()
                moveTo(11.558f, 6.6001f)
                horizontalLineTo(10.783f)
                verticalLineTo(7.4084f)
                curveTo(10.783f, 7.8418f, 10.433f, 8.1917f, 9.9997f, 8.1917f)
                curveTo(9.5663f, 8.1917f, 9.2163f, 7.8418f, 9.2163f, 7.4084f)
                verticalLineTo(6.6001f)
                horizontalLineTo(8.4413f)
                curveTo(8.008f, 6.6001f, 7.658f, 6.2501f, 7.658f, 5.8168f)
                curveTo(7.658f, 5.3834f, 8.008f, 5.0334f, 8.4413f, 5.0334f)
                horizontalLineTo(9.2163f)
                verticalLineTo(4.2917f)
                curveTo(9.2163f, 3.8584f, 9.5663f, 3.5084f, 9.9997f, 3.5084f)
                curveTo(10.433f, 3.5084f, 10.783f, 3.8584f, 10.783f, 4.2917f)
                verticalLineTo(5.0334f)
                horizontalLineTo(11.558f)
                curveTo(11.9913f, 5.0334f, 12.3413f, 5.3834f, 12.3413f, 5.8168f)
                curveTo(12.3413f, 6.2501f, 11.9913f, 6.6001f, 11.558f, 6.6001f)
                close()
            }
        }
        .build()
        return _sendnewcontact!!
    }

private var _sendnewcontact: ImageVector? = null
