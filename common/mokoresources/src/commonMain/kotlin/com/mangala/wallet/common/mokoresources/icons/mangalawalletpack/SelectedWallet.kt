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

public val MangalaWalletPack.SelectedWallet: ImageVector
    get() {
        if (_selectedWallet != null) {
            return _selectedWallet!!
        }
        _selectedWallet = Builder(name = "selectedWallet", defaultWidth = 8.0.dp, defaultHeight = 4.0.dp,
                viewportWidth = 8.0f, viewportHeight = 4.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFffffff)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(1.5f, 1.99365f)
                lineTo(3.08653f, 3.25f)
                lineTo(6.5f, 0.75f)
            }
        }
        .build()
        return _selectedWallet!!
    }

private var _selectedWallet: ImageVector? = null
