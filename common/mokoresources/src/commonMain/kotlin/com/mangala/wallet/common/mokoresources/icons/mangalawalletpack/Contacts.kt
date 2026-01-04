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

public val MangalaWalletPack.Contacts: ImageVector
    get() {
        if (_contacts != null) {
            return _contacts!!
        }
        _contacts = Builder(name = "Contacts", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(11.5789f, 7.278f)
                moveToRelative(-4.778f, 0.0f)
                arcToRelative(4.778f, 4.778f, 0.0f, true, true, 9.5561f, 0.0f)
                arcToRelative(4.778f, 4.778f, 0.0f, true, true, -9.5561f, 0.0f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                moveTo(4.0f, 18.7014f)
                curveTo(3.9987f, 18.3655f, 4.0739f, 18.0337f, 4.2197f, 17.7311f)
                curveTo(4.6774f, 16.8158f, 5.968f, 16.3307f, 7.0389f, 16.111f)
                curveTo(7.8113f, 15.9462f, 8.5943f, 15.836f, 9.3822f, 15.7815f)
                curveTo(10.8408f, 15.6533f, 12.3079f, 15.6533f, 13.7666f, 15.7815f)
                curveTo(14.5544f, 15.8367f, 15.3374f, 15.9468f, 16.1099f, 16.111f)
                curveTo(17.1808f, 16.3307f, 18.4714f, 16.77f, 18.9291f, 17.7311f)
                curveTo(19.2224f, 18.3479f, 19.2224f, 19.064f, 18.9291f, 19.6808f)
                curveTo(18.4714f, 20.6419f, 17.1808f, 21.0812f, 16.1099f, 21.2918f)
                curveTo(15.3384f, 21.4634f, 14.5551f, 21.5766f, 13.7666f, 21.6304f)
                curveTo(12.5794f, 21.7311f, 11.3866f, 21.7494f, 10.1968f, 21.6854f)
                curveTo(9.9222f, 21.6854f, 9.6568f, 21.6854f, 9.3822f, 21.6304f)
                curveTo(8.5966f, 21.5773f, 7.8163f, 21.4641f, 7.0481f, 21.2918f)
                curveTo(5.968f, 21.0812f, 4.6865f, 20.6419f, 4.2197f, 19.6808f)
                curveTo(4.0746f, 19.3747f, 3.9996f, 19.0401f, 4.0f, 18.7014f)
                close()
            }
        }
        .build()
        return _contacts!!
    }

private var _contacts: ImageVector? = null
