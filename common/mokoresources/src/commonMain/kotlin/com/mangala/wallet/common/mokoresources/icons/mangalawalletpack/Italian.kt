package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack

public val MangalaWalletPack.Italian: ImageVector
    get() {
        if (italian != null) {
            return italian!!
        }
        italian = Builder(name = "Italian", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            group {
            }
            group {
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(21.9763f, 23.4847f)
                    curveTo(22.8143f, 23.4847f, 23.4884f, 22.8092f, 23.4884f, 21.9712f)
                    verticalLineTo(2.0243f)
                    curveTo(23.4884f, 1.1863f, 22.8143f, 0.5122f, 21.9763f, 0.5122f)
                    horizontalLineTo(2.0294f)
                    curveTo(1.1914f, 0.5122f, 0.5159f, 1.1863f, 0.5159f, 2.0243f)
                    verticalLineTo(21.9712f)
                    curveTo(0.5159f, 22.8092f, 1.1914f, 23.4847f, 2.0294f, 23.4847f)
                    horizontalLineTo(21.9763f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFC0392B)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(23.4883f, 2.0243f)
                    curveTo(23.4883f, 1.1863f, 22.7973f, 0.5122f, 21.9403f, 0.5122f)
                    horizontalLineTo(15.8308f)
                    verticalLineTo(23.4847f)
                    horizontalLineTo(21.9403f)
                    curveTo(22.7973f, 23.4847f, 23.4883f, 22.8092f, 23.4883f, 21.9712f)
                    verticalLineTo(2.0243f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF27AE60)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(0.5159f, 21.9727f)
                    curveTo(0.5159f, 22.8107f, 1.2068f, 23.4847f, 2.0639f, 23.4847f)
                    horizontalLineTo(8.1734f)
                    verticalLineTo(0.5122f)
                    horizontalLineTo(2.0639f)
                    curveTo(1.2068f, 0.5122f, 0.5159f, 1.1878f, 0.5159f, 2.0258f)
                    verticalLineTo(21.9727f)
                    close()
                }
            }
        }
        .build()
        return italian!!
    }

private var italian: ImageVector? = null
