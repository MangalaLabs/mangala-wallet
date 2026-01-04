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

public val MangalaWalletPack.WalletReal: ImageVector
    get() {
        if (_walletReal != null) {
            return _walletReal!!
        }
        _walletReal = Builder(name = "Wallet", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(18.04f, 13.55f)
                curveTo(17.62f, 13.96f, 17.38f, 14.55f, 17.44f, 15.18f)
                curveTo(17.53f, 16.26f, 18.52f, 17.05f, 19.6f, 17.05f)
                horizontalLineTo(21.5f)
                verticalLineTo(18.24f)
                curveTo(21.5f, 20.31f, 19.81f, 22.0f, 17.74f, 22.0f)
                horizontalLineTo(6.26f)
                curveTo(4.19f, 22.0f, 2.5f, 20.31f, 2.5f, 18.24f)
                verticalLineTo(11.51f)
                curveTo(2.5f, 9.44f, 4.19f, 7.75f, 6.26f, 7.75f)
                horizontalLineTo(17.74f)
                curveTo(19.81f, 7.75f, 21.5f, 9.44f, 21.5f, 11.51f)
                verticalLineTo(12.95f)
                horizontalLineTo(19.48f)
                curveTo(18.92f, 12.95f, 18.41f, 13.17f, 18.04f, 13.55f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(2.5f, 12.4101f)
                verticalLineTo(7.8401f)
                curveTo(2.5f, 6.6501f, 3.23f, 5.5901f, 4.34f, 5.1701f)
                lineTo(12.28f, 2.1701f)
                curveTo(13.52f, 1.7001f, 14.85f, 2.6201f, 14.85f, 3.9501f)
                verticalLineTo(7.7501f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(22.5588f, 13.9702f)
                verticalLineTo(16.0302f)
                curveTo(22.5588f, 16.5802f, 22.1188f, 17.0302f, 21.5588f, 17.0502f)
                horizontalLineTo(19.5988f)
                curveTo(18.5188f, 17.0502f, 17.5288f, 16.2602f, 17.4388f, 15.1802f)
                curveTo(17.3788f, 14.5502f, 17.6188f, 13.9602f, 18.0388f, 13.5502f)
                curveTo(18.4088f, 13.1702f, 18.9188f, 12.9502f, 19.4788f, 12.9502f)
                horizontalLineTo(21.5588f)
                curveTo(22.1188f, 12.9702f, 22.5588f, 13.4202f, 22.5588f, 13.9702f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF484848)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(7.0f, 12.0f)
                horizontalLineTo(14.0f)
            }
        }
        .build()
        return _walletReal!!
    }

private var _walletReal: ImageVector? = null
