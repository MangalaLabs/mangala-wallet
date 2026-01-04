package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.Nft: ImageVector
    get() {
        if (_nft != null) {
            return _nft!!
        }
        _nft = Builder(name = "Nft", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp, viewportWidth
                = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                moveTo(17.857f, 20.4168f)
                curveTo(19.732f, 20.4168f, 21.25f, 18.8978f, 21.25f, 17.0238f)
                verticalLineTo(14.3238f)
                curveTo(20.01f, 14.3238f, 19.01f, 13.3238f, 19.01f, 12.0848f)
                curveTo(19.01f, 10.8448f, 20.01f, 9.8458f, 21.25f, 9.8458f)
                lineTo(21.248f, 7.1428f)
                curveTo(21.248f, 5.2688f, 19.73f, 3.7498f, 17.856f, 3.7498f)
                horizontalLineTo(6.144f)
                curveTo(4.27f, 3.7498f, 2.751f, 5.2688f, 2.751f, 7.1428f)
                lineTo(2.75f, 9.9328f)
                curveTo(3.989f, 9.9328f, 4.989f, 10.8448f, 4.989f, 12.0848f)
                curveTo(4.989f, 13.3238f, 3.989f, 14.3238f, 2.75f, 14.3238f)
                verticalLineTo(17.0238f)
                curveTo(2.75f, 18.8978f, 4.268f, 20.4168f, 6.142f, 20.4168f)
                horizontalLineTo(17.857f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                moveTo(11.9993f, 13.8543f)
                lineTo(13.7393f, 14.7693f)
                curveTo(13.9023f, 14.8543f, 14.0933f, 14.7163f, 14.0623f, 14.5353f)
                lineTo(13.7293f, 12.5963f)
                lineTo(15.1383f, 11.2253f)
                curveTo(15.2703f, 11.0963f, 15.1973f, 10.8733f, 15.0153f, 10.8463f)
                lineTo(13.0693f, 10.5633f)
                lineTo(12.1983f, 8.7993f)
                curveTo(12.1173f, 8.6343f, 11.8823f, 8.6343f, 11.8003f, 8.7993f)
                lineTo(10.9293f, 10.5633f)
                lineTo(8.9843f, 10.8463f)
                curveTo(8.8023f, 10.8733f, 8.7293f, 11.0963f, 8.8613f, 11.2253f)
                lineTo(10.2693f, 12.5963f)
                lineTo(9.9363f, 14.5353f)
                curveTo(9.9053f, 14.7163f, 10.0963f, 14.8543f, 10.2593f, 14.7693f)
                lineTo(11.9993f, 13.8543f)
                close()
            }
        }
        .build()
        return _nft!!
    }

private var _nft: ImageVector? = null
