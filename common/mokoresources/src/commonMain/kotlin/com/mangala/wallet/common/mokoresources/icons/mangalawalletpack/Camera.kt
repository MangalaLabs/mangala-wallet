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

public val MangalaWalletPack.Camera: ImageVector
    get() {
        if (`camera` != null) {
            return `camera`!!
        }
        `camera` = Builder(name = "Fi-sr-camera", defaultWidth = 14.0.dp, defaultHeight =
                14.0.dp, viewportWidth = 14.0f, viewportHeight = 14.0f).apply {
            group {
                path(fill = SolidColor(Color(0xFF292929)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(9.917f, 1.75f)
                    lineTo(9.124f, 0.681f)
                    curveTo(8.966f, 0.47f, 8.764f, 0.299f, 8.534f, 0.181f)
                    curveTo(8.303f, 0.063f, 8.049f, 0.001f, 7.792f, 0.0f)
                    lineTo(5.625f, 0.0f)
                    curveTo(5.368f, 0.001f, 5.114f, 0.063f, 4.883f, 0.181f)
                    curveTo(4.652f, 0.299f, 4.45f, 0.47f, 4.292f, 0.681f)
                    lineTo(3.5f, 1.75f)
                    horizontalLineTo(9.917f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF292929)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(6.999f, 10.501f)
                    curveTo(8.288f, 10.501f, 9.333f, 9.456f, 9.333f, 8.167f)
                    curveTo(9.333f, 6.879f, 8.288f, 5.834f, 6.999f, 5.834f)
                    curveTo(5.711f, 5.834f, 4.666f, 6.879f, 4.666f, 8.167f)
                    curveTo(4.666f, 9.456f, 5.711f, 10.501f, 6.999f, 10.501f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF292929)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(11.083f, 2.916f)
                    horizontalLineTo(2.917f)
                    curveTo(2.143f, 2.917f, 1.402f, 3.225f, 0.855f, 3.771f)
                    curveTo(0.309f, 4.318f, 0.001f, 5.059f, 0.0f, 5.833f)
                    lineTo(0.0f, 11.083f)
                    curveTo(0.001f, 11.856f, 0.309f, 12.597f, 0.855f, 13.144f)
                    curveTo(1.402f, 13.691f, 2.143f, 13.998f, 2.917f, 13.999f)
                    horizontalLineTo(11.083f)
                    curveTo(11.857f, 13.998f, 12.598f, 13.691f, 13.145f, 13.144f)
                    curveTo(13.691f, 12.597f, 13.999f, 11.856f, 14.0f, 11.083f)
                    verticalLineTo(5.833f)
                    curveTo(13.999f, 5.059f, 13.691f, 4.318f, 13.145f, 3.771f)
                    curveTo(12.598f, 3.225f, 11.857f, 2.917f, 11.083f, 2.916f)
                    close()
                    moveTo(7.0f, 11.666f)
                    curveTo(6.308f, 11.666f, 5.631f, 11.461f, 5.056f, 11.076f)
                    curveTo(4.48f, 10.692f, 4.031f, 10.145f, 3.766f, 9.505f)
                    curveTo(3.502f, 8.866f, 3.432f, 8.162f, 3.567f, 7.483f)
                    curveTo(3.702f, 6.804f, 4.036f, 6.181f, 4.525f, 5.691f)
                    curveTo(5.015f, 5.202f, 5.638f, 4.868f, 6.317f, 4.733f)
                    curveTo(6.996f, 4.598f, 7.7f, 4.668f, 8.339f, 4.932f)
                    curveTo(8.979f, 5.197f, 9.526f, 5.646f, 9.91f, 6.222f)
                    curveTo(10.295f, 6.797f, 10.5f, 7.474f, 10.5f, 8.166f)
                    curveTo(10.499f, 9.094f, 10.13f, 9.984f, 9.474f, 10.64f)
                    curveTo(8.818f, 11.296f, 7.928f, 11.665f, 7.0f, 11.666f)
                    close()
                }
            }
        }
        .build()
        return `camera`!!
    }

private var `camera`: ImageVector? = null
