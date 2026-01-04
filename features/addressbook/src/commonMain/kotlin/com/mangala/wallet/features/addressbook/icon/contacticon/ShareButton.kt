package com.mangala.wallet.features.addressbook.icon.contacticon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.features.addressbook.icon.ContactIcon

public val ContactIcon.ShareButton: ImageVector
    get() {
        if (_shareButton != null) {
            return _shareButton!!
        }
        _shareButton = Builder(name = "ShareButton", defaultWidth = 20.0.dp, defaultHeight =
                20.0.dp, viewportWidth = 20.0f, viewportHeight = 20.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF262626)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(15.0f, 6.667f)
                curveTo(15.495f, 6.667f, 15.978f, 6.52f, 16.389f, 6.246f)
                curveTo(16.8f, 5.971f, 17.121f, 5.581f, 17.31f, 5.124f)
                curveTo(17.499f, 4.667f, 17.548f, 4.164f, 17.452f, 3.679f)
                curveTo(17.355f, 3.194f, 17.117f, 2.749f, 16.768f, 2.399f)
                curveTo(16.418f, 2.05f, 15.973f, 1.811f, 15.488f, 1.715f)
                curveTo(15.003f, 1.619f, 14.5f, 1.668f, 14.043f, 1.857f)
                curveTo(13.587f, 2.047f, 13.196f, 2.367f, 12.921f, 2.778f)
                curveTo(12.647f, 3.189f, 12.5f, 3.673f, 12.5f, 4.167f)
                curveTo(12.5f, 4.83f, 12.763f, 5.466f, 13.232f, 5.935f)
                curveTo(13.701f, 6.404f, 14.337f, 6.667f, 15.0f, 6.667f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF262626)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(5.0f, 12.5f)
                curveTo(5.494f, 12.5f, 5.978f, 12.353f, 6.389f, 12.079f)
                curveTo(6.8f, 11.804f, 7.121f, 11.414f, 7.31f, 10.957f)
                curveTo(7.499f, 10.5f, 7.548f, 9.997f, 7.452f, 9.512f)
                curveTo(7.356f, 9.027f, 7.117f, 8.582f, 6.768f, 8.232f)
                curveTo(6.418f, 7.883f, 5.973f, 7.644f, 5.488f, 7.548f)
                curveTo(5.003f, 7.452f, 4.5f, 7.501f, 4.043f, 7.69f)
                curveTo(3.586f, 7.88f, 3.196f, 8.2f, 2.921f, 8.611f)
                curveTo(2.647f, 9.022f, 2.5f, 9.506f, 2.5f, 10.0f)
                curveTo(2.5f, 10.663f, 2.763f, 11.299f, 3.232f, 11.768f)
                curveTo(3.701f, 12.237f, 4.337f, 12.5f, 5.0f, 12.5f)
                verticalLineTo(12.5f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF262626)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(15.0f, 18.334f)
                curveTo(15.495f, 18.334f, 15.978f, 18.187f, 16.389f, 17.913f)
                curveTo(16.8f, 17.638f, 17.121f, 17.247f, 17.31f, 16.791f)
                curveTo(17.499f, 16.334f, 17.548f, 15.831f, 17.452f, 15.346f)
                curveTo(17.355f, 14.861f, 17.117f, 14.416f, 16.768f, 14.066f)
                curveTo(16.418f, 13.717f, 15.973f, 13.479f, 15.488f, 13.382f)
                curveTo(15.003f, 13.285f, 14.5f, 13.335f, 14.043f, 13.524f)
                curveTo(13.587f, 13.714f, 13.196f, 14.034f, 12.921f, 14.445f)
                curveTo(12.647f, 14.856f, 12.5f, 15.34f, 12.5f, 15.834f)
                curveTo(12.5f, 16.497f, 12.763f, 17.133f, 13.232f, 17.602f)
                curveTo(13.701f, 18.071f, 14.337f, 18.334f, 15.0f, 18.334f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF262626)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(7.158f, 11.259f)
                lineTo(12.85f, 14.575f)
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF262626)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(12.842f, 5.426f)
                lineTo(7.158f, 8.742f)
            }
        }
        .build()
        return _shareButton!!
    }

private var _shareButton: ImageVector? = null
