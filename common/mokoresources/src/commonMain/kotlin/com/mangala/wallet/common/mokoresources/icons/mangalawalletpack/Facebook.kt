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

val MangalaWalletPack.Facebook: ImageVector
    get() {
        if (facebook != null) {
            return facebook!!
        }
        facebook = Builder(name = "Facebook 1", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            group {
                path(fill = SolidColor(Color(0xFF1877F2)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(24.0f, 12.0f)
                    curveTo(24.0f, 17.9897f, 19.6116f, 22.9542f, 13.875f, 23.8542f)
                    verticalLineTo(15.4688f)
                    horizontalLineTo(16.6711f)
                    lineTo(17.2031f, 12.0f)
                    horizontalLineTo(13.875f)
                    verticalLineTo(9.7491f)
                    curveTo(13.875f, 8.7998f, 14.34f, 7.875f, 15.8306f, 7.875f)
                    horizontalLineTo(17.3438f)
                    verticalLineTo(4.9219f)
                    curveTo(17.3438f, 4.9219f, 15.9703f, 4.6875f, 14.6573f, 4.6875f)
                    curveTo(11.9166f, 4.6875f, 10.125f, 6.3488f, 10.125f, 9.3562f)
                    verticalLineTo(12.0f)
                    horizontalLineTo(7.0781f)
                    verticalLineTo(15.4688f)
                    horizontalLineTo(10.125f)
                    verticalLineTo(23.8542f)
                    curveTo(4.3884f, 22.9542f, 0.0f, 17.9897f, 0.0f, 12.0f)
                    curveTo(0.0f, 5.3728f, 5.3728f, 0.0f, 12.0f, 0.0f)
                    curveTo(18.6272f, 0.0f, 24.0f, 5.3728f, 24.0f, 12.0f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(16.6711f, 15.4688f)
                    lineTo(17.2031f, 12.0f)
                    horizontalLineTo(13.875f)
                    verticalLineTo(9.749f)
                    curveTo(13.875f, 8.8f, 14.3399f, 7.875f, 15.8306f, 7.875f)
                    horizontalLineTo(17.3438f)
                    verticalLineTo(4.9219f)
                    curveTo(17.3438f, 4.9219f, 15.9705f, 4.6875f, 14.6576f, 4.6875f)
                    curveTo(11.9165f, 4.6875f, 10.125f, 6.3488f, 10.125f, 9.3562f)
                    verticalLineTo(12.0f)
                    horizontalLineTo(7.0781f)
                    verticalLineTo(15.4688f)
                    horizontalLineTo(10.125f)
                    verticalLineTo(23.8542f)
                    curveTo(10.736f, 23.95f, 11.3621f, 24.0f, 12.0f, 24.0f)
                    curveTo(12.6379f, 24.0f, 13.264f, 23.95f, 13.875f, 23.8542f)
                    verticalLineTo(15.4688f)
                    horizontalLineTo(16.6711f)
                    close()
                }
            }
        }
        .build()
        return facebook!!
    }

private var facebook: ImageVector? = null
