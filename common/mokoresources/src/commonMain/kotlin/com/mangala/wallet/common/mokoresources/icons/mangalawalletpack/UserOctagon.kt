package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import kotlin.Unit

public val MangalaWalletPack.`UserOctagon`: ImageVector
    get() {
        if (`_userOctagon` != null) {
            return `_userOctagon`!!
        }
        `_userOctagon` = Builder(name = "User-octagon", defaultWidth = 24.0.dp, defaultHeight =
            24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF00A699)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero) {
                moveTo(19.51f, 5.85f)
                lineTo(13.57f, 2.42f)
                curveTo(12.6f, 1.86f, 11.4f, 1.86f, 10.42f, 2.42f)
                lineTo(4.49f, 5.85f)
                curveTo(3.52f, 6.41f, 2.92f, 7.45f, 2.92f, 8.58f)
                verticalLineTo(15.42f)
                curveTo(2.92f, 16.54f, 3.52f, 17.58f, 4.49f, 18.15f)
                lineTo(10.43f, 21.58f)
                curveTo(11.4f, 22.14f, 12.6f, 22.14f, 13.58f, 21.58f)
                lineTo(19.52f, 18.15f)
                curveTo(20.49f, 17.59f, 21.09f, 16.55f, 21.09f, 15.42f)
                verticalLineTo(8.58f)
                curveTo(21.08f, 7.45f, 20.48f, 6.42f, 19.51f, 5.85f)
                close()
                moveTo(12.0f, 7.34f)
                curveTo(13.29f, 7.34f, 14.33f, 8.38f, 14.33f, 9.67f)
                curveTo(14.33f, 10.96f, 13.29f, 12.0f, 12.0f, 12.0f)
                curveTo(10.71f, 12.0f, 9.67f, 10.96f, 9.67f, 9.67f)
                curveTo(9.67f, 8.39f, 10.71f, 7.34f, 12.0f, 7.34f)
                close()
                moveTo(14.68f, 16.66f)
                horizontalLineTo(9.32f)
                curveTo(8.51f, 16.66f, 8.04f, 15.76f, 8.49f, 15.09f)
                curveTo(9.17f, 14.08f, 10.49f, 13.4f, 12.0f, 13.4f)
                curveTo(13.51f, 13.4f, 14.83f, 14.08f, 15.51f, 15.09f)
                curveTo(15.96f, 15.75f, 15.48f, 16.66f, 14.68f, 16.66f)
                close()
            }
        }
            .build()
        return `_userOctagon`!!
    }

private var `_userOctagon`: ImageVector? = null

