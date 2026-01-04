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

public val MangalaWalletPack.Bitcoin: ImageVector
    get() {
        if (bitcoin != null) {
            return bitcoin!!
        }
        bitcoin = Builder(name = "Bitcoin 1", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            group {
                path(fill = SolidColor(Color(0xFFFFC700)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(14.5383f, 13.8539f)
                    curveTo(14.5392f, 15.5498f, 11.6353f, 15.3567f, 10.71f, 15.3577f)
                    verticalLineTo(12.3511f)
                    curveTo(11.6362f, 12.3511f, 14.5383f, 12.0853f, 14.5383f, 13.8539f)
                    close()
                    moveTo(24.0f, 12.0f)
                    curveTo(24.0f, 18.6272f, 18.6272f, 24.0f, 12.0f, 24.0f)
                    curveTo(5.3728f, 24.0f, 0.0f, 18.6272f, 0.0f, 12.0f)
                    curveTo(0.0f, 5.3728f, 5.3728f, 0.0f, 12.0f, 0.0f)
                    curveTo(18.6272f, 0.0f, 24.0f, 5.3723f, 24.0f, 12.0f)
                    close()
                    moveTo(15.278f, 11.2992f)
                    curveTo(16.0697f, 10.8961f, 16.5647f, 10.1855f, 16.4489f, 9.0023f)
                    curveTo(16.2933f, 7.3847f, 14.8973f, 6.8428f, 13.1344f, 6.6881f)
                    verticalLineTo(4.4447f)
                    horizontalLineTo(11.7689f)
                    verticalLineTo(6.6291f)
                    curveTo(11.4098f, 6.6291f, 11.0428f, 6.6361f, 10.6786f, 6.6436f)
                    verticalLineTo(4.4447f)
                    horizontalLineTo(9.3141f)
                    verticalLineTo(6.6877f)
                    curveTo(9.0178f, 6.6937f, 8.7277f, 6.6994f, 8.4445f, 6.6994f)
                    verticalLineTo(6.6923f)
                    lineTo(6.5606f, 6.6919f)
                    verticalLineTo(8.1506f)
                    curveTo(6.5606f, 8.1506f, 7.5694f, 8.1314f, 7.5525f, 8.1502f)
                    curveTo(8.1056f, 8.1502f, 8.2856f, 8.4712f, 8.3381f, 8.7487f)
                    verticalLineTo(11.3048f)
                    curveTo(8.3766f, 11.3048f, 8.4263f, 11.3067f, 8.4825f, 11.3142f)
                    curveTo(8.437f, 11.3142f, 8.3883f, 11.3142f, 8.3381f, 11.3142f)
                    verticalLineTo(14.8945f)
                    curveTo(8.3133f, 15.0684f, 8.2111f, 15.3464f, 7.8248f, 15.3469f)
                    curveTo(7.8422f, 15.3623f, 6.832f, 15.3469f, 6.832f, 15.3469f)
                    lineTo(6.5606f, 16.9777f)
                    horizontalLineTo(8.3377f)
                    curveTo(8.6686f, 16.9777f, 8.9939f, 16.9837f, 9.3131f, 16.9856f)
                    verticalLineTo(19.2548f)
                    horizontalLineTo(10.6777f)
                    verticalLineTo(17.0095f)
                    curveTo(11.0522f, 17.0175f, 11.4145f, 17.0203f, 11.7684f, 17.0198f)
                    verticalLineTo(19.2548f)
                    horizontalLineTo(13.1334f)
                    verticalLineTo(16.9898f)
                    curveTo(15.4298f, 16.8581f, 17.0367f, 16.2797f, 17.2364f, 14.1239f)
                    curveTo(17.3977f, 12.3877f, 16.5806f, 11.6133f, 15.2775f, 11.3002f)
                    lineTo(15.278f, 11.2992f)
                    close()
                    moveTo(13.9031f, 9.6122f)
                    curveTo(13.9031f, 8.003f, 11.4811f, 8.2481f, 10.7105f, 8.2481f)
                    verticalLineTo(10.9748f)
                    curveTo(11.4811f, 10.9748f, 13.9027f, 11.1548f, 13.9031f, 9.6122f)
                    close()
                }
            }
        }
        .build()
        return bitcoin!!
    }

private var bitcoin: ImageVector? = null
