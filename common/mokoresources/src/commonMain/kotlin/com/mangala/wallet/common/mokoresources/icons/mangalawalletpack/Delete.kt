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

public val MangalaWalletPack.Delete: ImageVector
    get() {
        if (_delete != null) {
            return _delete!!
        }
        _delete = Builder(name = "Delete", defaultWidth = 18.0.dp, defaultHeight = 18.0.dp,
                viewportWidth = 18.0f, viewportHeight = 18.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF383838)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(14.4933f, 7.1016f)
                curveTo(14.4933f, 7.1016f, 14.0861f, 12.1528f, 13.8498f, 14.2806f)
                curveTo(13.7373f, 15.2968f, 13.1096f, 15.8923f, 12.0813f, 15.9111f)
                curveTo(10.1246f, 15.9463f, 8.1656f, 15.9486f, 6.2096f, 15.9073f)
                curveTo(5.2203f, 15.8871f, 4.6031f, 15.2841f, 4.4928f, 14.2858f)
                curveTo(4.2551f, 12.1393f, 3.8501f, 7.1016f, 3.8501f, 7.1016f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF383838)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(15.531f, 4.6807f)
                horizontalLineTo(2.8125f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF383838)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(13.0804f, 4.6802f)
                curveTo(12.4917f, 4.6802f, 11.9847f, 4.264f, 11.8692f, 3.6872f)
                lineTo(11.6869f, 2.7752f)
                curveTo(11.5744f, 2.3545f, 11.1934f, 2.0635f, 10.7592f, 2.0635f)
                horizontalLineTo(7.5844f)
                curveTo(7.1502f, 2.0635f, 6.7692f, 2.3545f, 6.6567f, 2.7752f)
                lineTo(6.4744f, 3.6872f)
                curveTo(6.3589f, 4.264f, 5.8519f, 4.6802f, 5.2632f, 4.6802f)
            }
        }
        .build()
        return _delete!!
    }

private var _delete: ImageVector? = null
