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

public val MangalaWalletPack.ArrowLeftNavigation: ImageVector
    get() {
        if (`_arrow-left` != null) {
            return `_arrow-left`!!
        }
        `_arrow-left` = Builder(name = "Arrow-left", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF262626)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(15.0001f, 19.9201f)
                lineTo(8.4801f, 13.4001f)
                curveTo(7.7101f, 12.6301f, 7.7101f, 11.3701f, 8.4801f, 10.6001f)
                lineTo(15.0001f, 4.0801f)
            }
        }
        .build()
        return `_arrow-left`!!
    }

private var `_arrow-left`: ImageVector? = null
