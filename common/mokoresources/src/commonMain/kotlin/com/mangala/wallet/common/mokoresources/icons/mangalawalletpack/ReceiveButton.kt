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

public val MangalaWalletPack.ReceiveButton: ImageVector
    get() {
        if (com.mangala.wallet.common.mokoresources.icons.mangalawalletpack._receiveButton != null) {
            return com.mangala.wallet.common.mokoresources.icons.mangalawalletpack._receiveButton!!
        }
        com.mangala.wallet.common.mokoresources.icons.mangalawalletpack._receiveButton = Builder(name = "ReceiveButton", defaultWidth = 20.0.dp, defaultHeight =
                20.0.dp, viewportWidth = 20.0f, viewportHeight = 20.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF262626)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(9.999f, 1.667f)
                curveTo(5.399f, 1.667f, 1.666f, 5.4f, 1.666f, 10.0f)
                curveTo(1.666f, 14.6f, 5.399f, 18.334f, 9.999f, 18.334f)
                curveTo(14.599f, 18.334f, 18.333f, 14.6f, 18.333f, 10.0f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF262626)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(18.333f, 1.667f)
                lineTo(11.5f, 8.5f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF262626)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(10.834f, 5.142f)
                verticalLineTo(9.167f)
                horizontalLineTo(14.859f)
            }
        }
        .build()
        return com.mangala.wallet.common.mokoresources.icons.mangalawalletpack._receiveButton!!
    }

private var _receiveButton: ImageVector? = null