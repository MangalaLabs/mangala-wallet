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

public val MangalaWalletPack.SendExistingContact: ImageVector
    get() {
        if (_sendexistingcontact != null) {
            return _sendexistingcontact!!
        }
        _sendexistingcontact = Builder(name = "Sendexistingcontact", defaultWidth = 20.0.dp,
                defaultHeight = 20.0.dp, viewportWidth = 20.0f, viewportHeight = 20.0f).apply {
            path(fill = SolidColor(Color(0xFF767676)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(9.9998f, 11.6667f)
                curveTo(5.8248f, 11.6667f, 2.4248f, 14.4667f, 2.4248f, 17.9167f)
                curveTo(2.4248f, 18.1501f, 2.6081f, 18.3334f, 2.8415f, 18.3334f)
                horizontalLineTo(17.1581f)
                curveTo(17.3915f, 18.3334f, 17.5748f, 18.1501f, 17.5748f, 17.9167f)
                curveTo(17.5748f, 14.4667f, 14.1748f, 11.6667f, 9.9998f, 11.6667f)
                close()
            }
            path(fill = SolidColor(Color(0xFF767676)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(9.9997f, 1.6667f)
                curveTo(9.0163f, 1.6667f, 8.1163f, 2.0084f, 7.3997f, 2.5834f)
                curveTo(6.4413f, 3.3417f, 5.833f, 4.5167f, 5.833f, 5.8334f)
                curveTo(5.833f, 6.6167f, 6.0497f, 7.3501f, 6.4413f, 7.9751f)
                curveTo(7.158f, 9.1834f, 8.4747f, 10.0001f, 9.9997f, 10.0001f)
                curveTo(11.0497f, 10.0001f, 12.008f, 9.6167f, 12.7413f, 8.9584f)
                curveTo(13.0663f, 8.6834f, 13.3497f, 8.3501f, 13.5663f, 7.9751f)
                curveTo(13.9497f, 7.3501f, 14.1663f, 6.6167f, 14.1663f, 5.8334f)
                curveTo(14.1663f, 3.5334f, 12.2997f, 1.6667f, 9.9997f, 1.6667f)
                close()
                moveTo(12.158f, 5.3834f)
                lineTo(9.933f, 7.4334f)
                curveTo(9.783f, 7.5751f, 9.5913f, 7.6417f, 9.3997f, 7.6417f)
                curveTo(9.1997f, 7.6417f, 8.9997f, 7.5668f, 8.8497f, 7.4167f)
                lineTo(7.8247f, 6.3834f)
                curveTo(7.5163f, 6.0751f, 7.5163f, 5.5834f, 7.8247f, 5.2751f)
                curveTo(8.133f, 4.9668f, 8.6247f, 4.9668f, 8.933f, 5.2751f)
                lineTo(9.4247f, 5.7667f)
                lineTo(11.0997f, 4.2251f)
                curveTo(11.4163f, 3.9334f, 11.908f, 3.9501f, 12.1997f, 4.2667f)
                curveTo(12.4913f, 4.5918f, 12.4747f, 5.0918f, 12.158f, 5.3834f)
                close()
            }
        }
        .build()
        return _sendexistingcontact!!
    }

private var _sendexistingcontact: ImageVector? = null
