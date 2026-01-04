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

val MangalaWalletPack.ContactsSquare: ImageVector
    get() {
        if (_contactsSquare != null) {
            return _contactsSquare!!
        }
        _contactsSquare = Builder(name = "contactsSquare", defaultWidth = 18.0.dp, defaultHeight =
                18.0.dp, viewportWidth = 18.0f, viewportHeight = 18.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF00A699)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(13.605f, 16.2151f)
                curveTo(12.945f, 16.4101f, 12.165f, 16.5001f, 11.25f, 16.5001f)
                horizontalLineTo(6.75f)
                curveTo(5.835f, 16.5001f, 5.055f, 16.4101f, 4.395f, 16.2151f)
                curveTo(4.56f, 14.2651f, 6.5625f, 12.7275f, 9.0f, 12.7275f)
                curveTo(11.4375f, 12.7275f, 13.44f, 14.2651f, 13.605f, 16.2151f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF00A699)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(11.25f, 1.5f)
                horizontalLineTo(6.75f)
                curveTo(3.0f, 1.5f, 1.5f, 3.0f, 1.5f, 6.75f)
                verticalLineTo(11.25f)
                curveTo(1.5f, 14.085f, 2.355f, 15.6375f, 4.395f, 16.215f)
                curveTo(4.56f, 14.265f, 6.5625f, 12.7275f, 9.0f, 12.7275f)
                curveTo(11.4375f, 12.7275f, 13.44f, 14.265f, 13.605f, 16.215f)
                curveTo(15.645f, 15.6375f, 16.5f, 14.085f, 16.5f, 11.25f)
                verticalLineTo(6.75f)
                curveTo(16.5f, 3.0f, 15.0f, 1.5f, 11.25f, 1.5f)
                close()
                moveTo(9.0f, 10.6275f)
                curveTo(7.515f, 10.6275f, 6.315f, 9.42f, 6.315f, 7.935f)
                curveTo(6.315f, 6.45f, 7.515f, 5.25f, 9.0f, 5.25f)
                curveTo(10.485f, 5.25f, 11.685f, 6.45f, 11.685f, 7.935f)
                curveTo(11.685f, 9.42f, 10.485f, 10.6275f, 9.0f, 10.6275f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF00A699)),
                    strokeLineWidth = 1.5f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(11.6849f, 7.935f)
                curveTo(11.6849f, 9.42f, 10.4849f, 10.6275f, 8.9999f, 10.6275f)
                curveTo(7.5149f, 10.6275f, 6.3149f, 9.42f, 6.3149f, 7.935f)
                curveTo(6.3149f, 6.45f, 7.5149f, 5.25f, 8.9999f, 5.25f)
                curveTo(10.4849f, 5.25f, 11.6849f, 6.45f, 11.6849f, 7.935f)
                close()
            }
        }
        .build()
        return _contactsSquare!!
    }

private var _contactsSquare: ImageVector? = null
