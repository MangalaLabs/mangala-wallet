package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.material.icons.materialIcon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

val MangalaWalletPack.TransactionHistory: ImageVector
    get() {
        if (_transactionHistory != null) {
            return _transactionHistory!!
        }
        _transactionHistory = materialIcon(name = "TransactionHistory") {
            path(
                fill = SolidColor(Color.Black),
                stroke = SolidColor(Color(0xFF484848)),
                fillAlpha = 1.0F,
                strokeAlpha = 1.0F,
                strokeLineWidth = 1.5F,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 4.0F,
                pathFillType = PathFillType.NonZero,
            ) {
                moveTo(13.799F, 6.79F)
                horizontalLineTo(10.199F)
                horizontalLineTo(6.599F)
                close()
            }
            path(
                fill = SolidColor(Color(0x00000000)),
                stroke = SolidColor(Color(0xFF484848)),
                fillAlpha = 1.0F,
                strokeAlpha = 1.0F,
                strokeLineWidth = 1.5F,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 4.0F,
                pathFillType = PathFillType.NonZero,
            ) {
                moveTo(11.999F, 10.579F)
                horizontalLineTo(9.299F)
                horizontalLineTo(6.599F)
                close()
            }
            path(
                fill = SolidColor(Color(0x00000000)),
                stroke = SolidColor(Color(0xFF484848)),
                fillAlpha = 1.0F,
                strokeAlpha = 1.0F,
                strokeLineWidth = 1.5F,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 4.0F,
                pathFillType = PathFillType.NonZero,
            ) {
                moveTo(10.199F, 14.368F)
                horizontalLineTo(8.399F)
                horizontalLineTo(6.599F)
                close()
            }
            path(
                fill = SolidColor(Color(0x00000000)),
                stroke = SolidColor(Color(0xFF484848)),
                fillAlpha = 1.0F,
                strokeAlpha = 1.0F,
                strokeLineWidth = 1.5F,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 4.0F,
                pathFillType = PathFillType.NonZero,
            ) {
                moveTo(17.4F, 9.121F)
                verticalLineTo(6.6F)
                curveTo(17.4F, 4.612F, 15.788F, 3.0F, 13.8F, 3.0F)
                lineTo(6.6F, 3.0F)
                curveTo(4.612F, 3.0F, 3.0F, 4.612F, 3.0F, 6.6F)
                verticalLineTo(16.453F)
                curveTo(3.0F, 18.441F, 4.612F, 20.053F, 6.6F, 20.053F)
                horizontalLineTo(11.1F)
            }
            path(
                fill = SolidColor(Color(0x00000000)),
                stroke = SolidColor(Color(0xFF484848)),
                fillAlpha = 1.0F,
                strokeAlpha = 1.0F,
                strokeLineWidth = 1.5F,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 4.0F,
                pathFillType = PathFillType.EvenOdd,
            ) {
                moveTo(21.0F, 16.0F)
                curveTo(21.0F, 18.209F, 19.209F, 20.0F, 17.0F, 20.0F)
                curveTo(14.791F, 20.0F, 13.0F, 18.209F, 13.0F, 16.0F)
                curveTo(13.0F, 13.791F, 14.791F, 12.0F, 17.0F, 12.0F)
                curveTo(19.209F, 12.0F, 21.0F, 13.791F, 21.0F, 16.0F)
                close()
            }
            path(
                fill = SolidColor(Color(0x00000000)),
                stroke = SolidColor(Color(0xFF484848)),
                fillAlpha = 1.0F,
                strokeAlpha = 1.0F,
                strokeLineWidth = 1.5F,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 4.0F,
                pathFillType = PathFillType.NonZero,
            ) {
                moveTo(18.0F, 17.0F)
                lineTo(17.0F, 16.366F)
                verticalLineTo(15.0F)
                close()
            }
        }
        return _transactionHistory!!
    }

private var _transactionHistory: ImageVector? = null
