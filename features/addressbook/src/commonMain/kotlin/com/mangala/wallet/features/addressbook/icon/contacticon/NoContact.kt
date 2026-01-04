package com.mangala.wallet.features.addressbook.icon.contacticon

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.mangala.wallet.features.addressbook.icon.ContactIcon

public val ContactIcon.NoContact: ImageVector
    get() {
        if (_noContact != null) {
            return _noContact!!
        }
        _noContact = Builder(name = "NoContact", defaultWidth = 121.0.dp, defaultHeight = 120.0.dp,
                viewportWidth = 121.0f, viewportHeight = 120.0f).apply {
            path(fill = SolidColor(Color(0xFFEAEEF9)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(61.96f, 100.09f)
                curveTo(84.85f, 100.09f, 103.41f, 81.62f, 103.41f, 58.83f)
                curveTo(103.41f, 36.04f, 84.85f, 17.57f, 61.96f, 17.57f)
                curveTo(39.07f, 17.57f, 20.51f, 36.04f, 20.51f, 58.83f)
                curveTo(20.51f, 81.62f, 39.07f, 100.09f, 61.96f, 100.09f)
                close()
            }
            path(fill = SolidColor(Color(0xFFEAEEF9)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(111.09f, 68.2f)
                curveTo(112.84f, 68.2f, 114.26f, 66.79f, 114.26f, 65.05f)
                curveTo(114.26f, 63.31f, 112.84f, 61.89f, 111.09f, 61.89f)
                curveTo(109.34f, 61.89f, 107.92f, 63.31f, 107.92f, 65.05f)
                curveTo(107.92f, 66.79f, 109.34f, 68.2f, 111.09f, 68.2f)
                close()
            }
            path(fill = SolidColor(Color(0xFFEAEEF9)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(24.18f, 30.47f)
                curveTo(25.33f, 30.47f, 26.27f, 29.54f, 26.27f, 28.39f)
                curveTo(26.27f, 27.25f, 25.33f, 26.32f, 24.18f, 26.32f)
                curveTo(23.03f, 26.32f, 22.1f, 27.25f, 22.1f, 28.39f)
                curveTo(22.1f, 29.54f, 23.03f, 30.47f, 24.18f, 30.47f)
                close()
            }
            path(fill = SolidColor(Color(0xFFDCE2F0)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(58.46f, 92.55f)
                curveTo(58.46f, 96.45f, 56.79f, 99.94f, 54.12f, 102.43f)
                curveTo(53.37f, 103.09f, 52.62f, 103.68f, 51.7f, 104.17f)
                curveTo(49.7f, 105.34f, 47.37f, 106.0f, 44.86f, 106.0f)
                curveTo(42.36f, 106.0f, 40.03f, 105.34f, 38.03f, 104.17f)
                curveTo(37.69f, 104.01f, 37.44f, 103.84f, 37.11f, 103.59f)
                curveTo(33.61f, 101.18f, 31.27f, 97.12f, 31.27f, 92.55f)
                curveTo(31.27f, 85.08f, 37.36f, 79.1f, 44.78f, 79.1f)
                curveTo(52.37f, 79.02f, 58.46f, 85.08f, 58.46f, 92.55f)
                close()
            }
            path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(42.11f, 95.54f)
                curveTo(42.28f, 95.71f, 42.45f, 95.96f, 42.7f, 96.12f)
                curveTo(42.86f, 96.29f, 43.03f, 96.37f, 43.2f, 96.54f)
                curveTo(43.28f, 96.62f, 43.45f, 96.7f, 43.53f, 96.79f)
                curveTo(43.53f, 96.79f, 43.62f, 96.79f, 43.62f, 96.87f)
                lineTo(43.7f, 96.95f)
                verticalLineTo(98.11f)
                curveTo(43.7f, 98.11f, 43.7f, 98.11f, 43.62f, 98.03f)
                curveTo(43.53f, 97.95f, 43.36f, 97.86f, 43.28f, 97.78f)
                curveTo(43.11f, 97.7f, 42.95f, 97.53f, 42.78f, 97.45f)
                curveTo(42.7f, 97.45f, 42.7f, 97.37f, 42.61f, 97.37f)
                curveTo(42.03f, 97.03f, 41.53f, 96.7f, 41.53f, 96.29f)
                curveTo(41.61f, 96.12f, 41.78f, 95.87f, 42.11f, 95.54f)
                close()
                moveTo(53.96f, 102.01f)
                curveTo(53.46f, 100.94f, 52.54f, 99.86f, 51.37f, 99.28f)
                curveTo(50.79f, 99.03f, 50.12f, 98.78f, 49.45f, 98.78f)
                curveTo(49.29f, 98.78f, 49.04f, 98.78f, 48.87f, 98.78f)
                curveTo(48.79f, 98.78f, 48.7f, 98.78f, 48.62f, 98.78f)
                curveTo(47.7f, 98.69f, 47.62f, 98.53f, 47.62f, 98.53f)
                verticalLineTo(96.62f)
                curveTo(48.2f, 96.12f, 48.79f, 95.54f, 49.29f, 94.96f)
                curveTo(49.7f, 94.38f, 50.04f, 93.71f, 50.2f, 92.88f)
                curveTo(50.95f, 92.72f, 51.45f, 92.05f, 51.37f, 91.22f)
                curveTo(51.37f, 90.89f, 51.12f, 90.56f, 51.12f, 90.23f)
                curveTo(51.12f, 90.06f, 51.12f, 89.89f, 51.12f, 89.73f)
                curveTo(51.12f, 89.65f, 51.12f, 89.48f, 51.12f, 89.4f)
                curveTo(51.12f, 89.31f, 51.12f, 89.15f, 51.12f, 89.06f)
                curveTo(51.04f, 88.48f, 50.87f, 87.9f, 50.54f, 87.24f)
                curveTo(49.54f, 85.41f, 47.7f, 84.25f, 45.53f, 84.25f)
                curveTo(45.12f, 84.25f, 44.7f, 84.33f, 44.28f, 84.42f)
                curveTo(43.53f, 84.58f, 42.78f, 84.91f, 42.2f, 85.41f)
                curveTo(42.11f, 85.49f, 41.95f, 85.58f, 41.86f, 85.74f)
                lineTo(41.78f, 85.83f)
                curveTo(41.11f, 86.49f, 40.53f, 87.24f, 40.28f, 88.15f)
                curveTo(39.95f, 89.06f, 39.95f, 89.98f, 40.03f, 90.89f)
                curveTo(40.03f, 90.89f, 40.03f, 90.89f, 40.03f, 90.97f)
                verticalLineTo(91.06f)
                curveTo(40.03f, 91.22f, 40.11f, 91.22f, 40.03f, 91.31f)
                curveTo(40.03f, 91.39f, 39.95f, 91.39f, 39.95f, 91.47f)
                curveTo(39.78f, 91.72f, 39.7f, 92.05f, 39.86f, 92.55f)
                curveTo(40.2f, 93.38f, 40.7f, 93.3f, 41.28f, 93.71f)
                curveTo(41.28f, 93.71f, 41.2f, 93.71f, 41.2f, 93.8f)
                lineTo(40.61f, 93.96f)
                curveTo(37.94f, 94.79f, 37.03f, 97.03f, 38.19f, 98.45f)
                curveTo(38.61f, 98.94f, 39.28f, 99.36f, 40.28f, 99.61f)
                curveTo(40.03f, 99.61f, 39.78f, 99.77f, 39.61f, 99.94f)
                curveTo(38.44f, 100.85f, 37.69f, 102.26f, 37.53f, 103.59f)
                curveTo(37.53f, 103.68f, 37.53f, 103.76f, 37.53f, 103.84f)
                curveTo(37.86f, 104.01f, 38.11f, 104.26f, 38.44f, 104.42f)
                horizontalLineTo(51.7f)
                curveTo(52.54f, 103.93f, 53.37f, 103.34f, 54.12f, 102.68f)
                curveTo(54.04f, 102.26f, 54.04f, 102.1f, 53.96f, 102.01f)
                close()
            }
            path(fill = SolidColor(Color(0xFFEAEEF9)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(54.12f, 102.43f)
                curveTo(53.37f, 103.1f, 52.62f, 103.68f, 51.7f, 104.17f)
                curveTo(49.7f, 105.34f, 47.37f, 106.0f, 44.87f, 106.0f)
                curveTo(42.36f, 106.0f, 40.03f, 105.34f, 38.03f, 104.17f)
                curveTo(37.69f, 104.01f, 37.44f, 103.84f, 37.11f, 103.59f)
                curveTo(37.11f, 103.51f, 37.11f, 103.43f, 37.11f, 103.34f)
                curveTo(37.28f, 102.01f, 38.03f, 100.6f, 39.19f, 99.69f)
                curveTo(39.36f, 99.52f, 39.61f, 99.44f, 39.86f, 99.36f)
                curveTo(38.86f, 99.19f, 38.19f, 98.78f, 37.78f, 98.2f)
                horizontalLineTo(40.95f)
                curveTo(41.86f, 99.44f, 43.28f, 100.19f, 44.95f, 100.19f)
                curveTo(46.37f, 100.19f, 47.62f, 99.61f, 48.53f, 98.69f)
                curveTo(48.62f, 98.69f, 48.7f, 98.69f, 48.78f, 98.69f)
                curveTo(48.95f, 98.69f, 49.12f, 98.69f, 49.37f, 98.69f)
                curveTo(50.04f, 98.69f, 50.7f, 98.86f, 51.29f, 99.19f)
                curveTo(52.45f, 99.77f, 53.29f, 100.77f, 53.87f, 101.93f)
                curveTo(54.04f, 102.1f, 54.04f, 102.26f, 54.12f, 102.43f)
                close()
            }
            path(fill = linearGradient(0.0f to Color(0xFFFFFFFF), 0.99f to Color(0xFFD6DEEA), start
                    = Offset(45.06f,98.47f), end = Offset(45.06f,97.47f)), stroke = null,
                    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(47.7f, 96.62f)
                verticalLineTo(98.36f)
                lineTo(42.45f, 98.53f)
                lineTo(42.7f, 97.37f)
                curveTo(42.78f, 97.37f, 42.78f, 97.45f, 42.86f, 97.45f)
                curveTo(43.03f, 97.53f, 43.2f, 97.7f, 43.36f, 97.78f)
                curveTo(43.45f, 97.86f, 43.53f, 97.95f, 43.7f, 98.03f)
                curveTo(43.7f, 98.03f, 43.78f, 98.03f, 43.78f, 98.11f)
                verticalLineTo(96.95f)
                lineTo(43.7f, 96.87f)
                curveTo(44.62f, 97.37f, 45.87f, 97.7f, 47.7f, 96.62f)
                close()
            }
            path(fill = SolidColor(Color(0xFFA2ABBC)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(51.12f, 89.4f)
                curveTo(50.12f, 89.73f, 48.95f, 89.89f, 47.87f, 89.81f)
                curveTo(46.12f, 89.65f, 44.45f, 88.98f, 43.11f, 87.82f)
                curveTo(42.7f, 89.06f, 41.61f, 89.98f, 40.36f, 90.56f)
                curveTo(40.19f, 90.64f, 40.03f, 90.72f, 39.86f, 90.72f)
                curveTo(39.86f, 90.72f, 39.86f, 90.72f, 39.86f, 90.64f)
                curveTo(39.78f, 89.73f, 39.78f, 88.82f, 40.11f, 87.9f)
                curveTo(40.36f, 86.99f, 40.94f, 86.24f, 41.61f, 85.58f)
                lineTo(41.7f, 85.49f)
                curveTo(41.78f, 85.41f, 41.95f, 85.33f, 42.03f, 85.16f)
                curveTo(42.61f, 84.66f, 43.36f, 84.33f, 44.11f, 84.17f)
                curveTo(44.53f, 84.08f, 44.95f, 84.0f, 45.36f, 84.0f)
                curveTo(47.53f, 84.0f, 49.45f, 85.16f, 50.37f, 86.99f)
                curveTo(50.7f, 87.65f, 50.87f, 88.32f, 50.95f, 88.82f)
                curveTo(51.12f, 89.15f, 51.12f, 89.31f, 51.12f, 89.4f)
                close()
            }
            path(fill = SolidColor(Color(0xFFA2ABBC)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(44.45f, 101.68f)
                curveTo(43.95f, 102.26f, 43.11f, 102.26f, 42.36f, 102.26f)
                curveTo(43.11f, 101.52f, 42.7f, 99.11f, 40.03f, 99.36f)
                curveTo(36.36f, 98.7f, 36.69f, 94.88f, 40.36f, 93.71f)
                lineTo(40.95f, 93.55f)
                lineTo(41.03f, 93.63f)
                curveTo(41.28f, 94.38f, 41.7f, 95.04f, 42.11f, 95.54f)
                curveTo(40.61f, 96.79f, 42.7f, 97.12f, 43.7f, 98.11f)
                curveTo(44.53f, 98.61f, 45.28f, 100.69f, 44.45f, 101.68f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFffffff)),
                    strokeLineWidth = 1.6434f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(58.46f, 92.55f)
                curveTo(58.46f, 96.45f, 56.79f, 99.94f, 54.12f, 102.43f)
                curveTo(53.37f, 103.09f, 52.62f, 103.68f, 51.7f, 104.17f)
                curveTo(49.7f, 105.34f, 47.37f, 106.0f, 44.86f, 106.0f)
                curveTo(42.36f, 106.0f, 40.03f, 105.34f, 38.03f, 104.17f)
                curveTo(37.69f, 104.01f, 37.44f, 103.84f, 37.11f, 103.59f)
                curveTo(33.61f, 101.18f, 31.27f, 97.12f, 31.27f, 92.55f)
                curveTo(31.27f, 85.08f, 37.36f, 79.1f, 44.78f, 79.1f)
                curveTo(52.37f, 79.02f, 58.46f, 85.08f, 58.46f, 92.55f)
                close()
            }
            path(fill = SolidColor(Color(0xFFE9F0F8)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(95.99f, 59.51f)
                curveTo(95.99f, 63.24f, 94.48f, 66.57f, 92.15f, 69.06f)
                curveTo(91.98f, 69.31f, 91.73f, 69.47f, 91.48f, 69.64f)
                curveTo(88.98f, 71.96f, 85.64f, 73.37f, 81.98f, 73.37f)
                curveTo(79.06f, 73.37f, 76.3f, 72.46f, 74.05f, 70.88f)
                curveTo(73.39f, 70.47f, 72.8f, 69.89f, 72.22f, 69.39f)
                curveTo(69.63f, 66.9f, 68.05f, 63.41f, 68.05f, 59.51f)
                curveTo(68.05f, 51.87f, 74.3f, 45.64f, 81.98f, 45.64f)
                curveTo(89.73f, 45.64f, 95.99f, 51.87f, 95.99f, 59.51f)
                close()
            }
            path(fill = SolidColor(Color(0xFFDCE2F0)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(95.99f, 59.51f)
                curveTo(95.99f, 63.58f, 94.23f, 67.15f, 91.48f, 69.72f)
                curveTo(88.98f, 72.04f, 85.64f, 73.46f, 81.98f, 73.46f)
                curveTo(79.06f, 73.46f, 76.3f, 72.54f, 74.05f, 70.96f)
                curveTo(70.38f, 68.47f, 68.05f, 64.32f, 68.05f, 59.59f)
                curveTo(68.05f, 51.95f, 74.3f, 45.73f, 81.98f, 45.73f)
                curveTo(89.65f, 45.73f, 95.99f, 51.87f, 95.99f, 59.51f)
                close()
            }
            path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(92.15f, 69.06f)
                curveTo(91.98f, 69.31f, 91.73f, 69.47f, 91.48f, 69.64f)
                curveTo(88.98f, 71.96f, 85.65f, 73.37f, 81.98f, 73.37f)
                curveTo(79.06f, 73.37f, 76.31f, 72.46f, 74.05f, 70.88f)
                curveTo(73.39f, 70.47f, 72.8f, 69.89f, 72.22f, 69.39f)
                curveTo(72.55f, 68.97f, 73.05f, 68.72f, 74.14f, 68.31f)
                lineTo(74.55f, 68.14f)
                curveTo(75.39f, 67.81f, 76.56f, 67.4f, 78.06f, 66.73f)
                curveTo(78.31f, 66.65f, 78.47f, 66.48f, 78.64f, 66.32f)
                curveTo(78.72f, 66.23f, 78.81f, 66.15f, 78.81f, 65.98f)
                curveTo(78.89f, 65.82f, 78.97f, 65.57f, 78.97f, 65.4f)
                verticalLineTo(62.58f)
                curveTo(78.89f, 62.5f, 78.89f, 62.5f, 78.81f, 62.41f)
                curveTo(78.56f, 62.08f, 78.39f, 61.67f, 78.39f, 61.17f)
                lineTo(78.22f, 61.09f)
                curveTo(77.47f, 61.25f, 77.56f, 60.51f, 77.39f, 59.01f)
                curveTo(77.31f, 58.43f, 77.39f, 58.26f, 77.72f, 58.18f)
                lineTo(77.97f, 57.85f)
                curveTo(77.47f, 56.69f, 77.22f, 55.61f, 77.22f, 54.78f)
                curveTo(77.22f, 53.37f, 77.81f, 52.45f, 78.64f, 52.04f)
                curveTo(78.14f, 51.04f, 78.14f, 50.71f, 78.14f, 50.71f)
                curveTo(78.14f, 50.71f, 81.06f, 51.21f, 82.06f, 51.04f)
                curveTo(83.31f, 50.79f, 85.31f, 51.12f, 86.06f, 52.78f)
                curveTo(87.31f, 53.28f, 87.73f, 54.03f, 87.9f, 54.86f)
                curveTo(88.06f, 56.19f, 87.31f, 57.6f, 87.15f, 58.18f)
                verticalLineTo(58.26f)
                curveTo(87.31f, 58.35f, 87.4f, 58.51f, 87.31f, 59.09f)
                curveTo(87.15f, 60.51f, 87.15f, 61.34f, 86.48f, 61.17f)
                lineTo(85.81f, 62.33f)
                curveTo(85.81f, 62.5f, 85.81f, 62.5f, 85.73f, 62.58f)
                curveTo(85.73f, 62.83f, 85.73f, 63.25f, 85.73f, 65.49f)
                curveTo(85.73f, 65.74f, 85.81f, 66.07f, 85.98f, 66.23f)
                curveTo(86.06f, 66.32f, 86.06f, 66.4f, 86.15f, 66.4f)
                curveTo(86.31f, 66.57f, 86.48f, 66.73f, 86.65f, 66.73f)
                curveTo(88.31f, 67.4f, 89.48f, 67.89f, 90.4f, 68.23f)
                curveTo(91.23f, 68.56f, 91.82f, 68.81f, 92.15f, 69.06f)
                close()
            }
            path(fill = SolidColor(Color(0xFFEAEEF9)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(92.15f, 69.06f)
                curveTo(91.98f, 69.31f, 91.73f, 69.47f, 91.48f, 69.64f)
                curveTo(88.98f, 71.96f, 85.65f, 73.37f, 81.98f, 73.37f)
                curveTo(79.06f, 73.37f, 76.31f, 72.46f, 74.05f, 70.88f)
                curveTo(73.39f, 70.47f, 72.8f, 69.89f, 72.22f, 69.39f)
                curveTo(72.55f, 68.97f, 73.05f, 68.72f, 74.14f, 68.31f)
                lineTo(74.55f, 68.14f)
                curveTo(75.39f, 67.81f, 76.56f, 67.4f, 78.06f, 66.73f)
                curveTo(78.31f, 66.65f, 78.47f, 66.48f, 78.64f, 66.32f)
                curveTo(79.47f, 67.48f, 80.81f, 68.23f, 82.39f, 68.23f)
                curveTo(83.89f, 68.23f, 85.23f, 67.48f, 86.06f, 66.4f)
                curveTo(86.23f, 66.57f, 86.4f, 66.73f, 86.56f, 66.73f)
                curveTo(88.23f, 67.4f, 89.4f, 67.89f, 90.32f, 68.23f)
                curveTo(91.23f, 68.56f, 91.82f, 68.81f, 92.15f, 69.06f)
                close()
            }
            path(fill = SolidColor(Color(0xFFA2ABBC)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(87.06f, 58.1f)
                curveTo(87.15f, 57.77f, 86.98f, 57.27f, 86.81f, 57.02f)
                curveTo(86.81f, 56.94f, 86.73f, 56.94f, 86.73f, 56.85f)
                curveTo(86.15f, 55.69f, 84.9f, 55.28f, 83.73f, 55.19f)
                curveTo(80.64f, 55.03f, 80.39f, 55.61f, 79.47f, 54.78f)
                curveTo(79.81f, 55.19f, 79.81f, 55.94f, 79.31f, 56.77f)
                curveTo(78.97f, 57.35f, 78.39f, 57.68f, 77.81f, 57.85f)
                curveTo(76.39f, 54.69f, 77.14f, 52.7f, 78.47f, 52.04f)
                curveTo(77.97f, 51.04f, 77.97f, 50.71f, 77.97f, 50.71f)
                curveTo(77.97f, 50.71f, 80.89f, 51.21f, 81.89f, 51.04f)
                curveTo(83.14f, 50.79f, 85.15f, 51.12f, 85.9f, 52.78f)
                curveTo(87.15f, 53.28f, 87.56f, 54.03f, 87.73f, 54.86f)
                curveTo(87.98f, 56.11f, 87.23f, 57.52f, 87.06f, 58.1f)
                close()
            }
            path(fill = linearGradient(0.0f to Color(0xFFFFFFFF), 0.99f to Color(0xFFD6DEEA), start
                    = Offset(82.38f,65.71f), end = Offset(82.38f,63.84f)), stroke = null,
                    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(79.06f, 65.4f)
                verticalLineTo(62.58f)
                curveTo(78.97f, 62.5f, 78.97f, 62.5f, 78.89f, 62.42f)
                verticalLineTo(62.25f)
                curveTo(79.06f, 62.5f, 79.22f, 62.75f, 79.47f, 62.91f)
                lineTo(81.72f, 64.49f)
                curveTo(82.22f, 64.91f, 82.97f, 64.91f, 83.48f, 64.49f)
                lineTo(85.56f, 62.66f)
                curveTo(85.64f, 62.58f, 85.73f, 62.58f, 85.81f, 62.5f)
                curveTo(85.81f, 62.75f, 85.81f, 63.16f, 85.81f, 65.4f)
                curveTo(85.81f, 65.57f, 85.81f, 65.65f, 85.89f, 65.82f)
                horizontalLineTo(79.06f)
                curveTo(78.97f, 65.65f, 79.06f, 65.57f, 79.06f, 65.4f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFffffff)),
                    strokeLineWidth = 1.6604f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(95.99f, 59.51f)
                curveTo(95.99f, 63.58f, 94.23f, 67.15f, 91.48f, 69.72f)
                curveTo(88.98f, 72.04f, 85.64f, 73.46f, 81.98f, 73.46f)
                curveTo(79.06f, 73.46f, 76.3f, 72.54f, 74.05f, 70.96f)
                curveTo(70.38f, 68.47f, 68.05f, 64.32f, 68.05f, 59.59f)
                curveTo(68.05f, 51.95f, 74.3f, 45.73f, 81.98f, 45.73f)
                curveTo(89.65f, 45.73f, 95.99f, 51.87f, 95.99f, 59.51f)
                close()
            }
            path(fill = SolidColor(Color(0xFFCED6E2)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(2.5f, 69.14f)
                curveTo(2.5f, 71.8f, 4.67f, 73.96f, 7.34f, 73.96f)
                horizontalLineTo(50.2f)
                curveTo(50.62f, 73.96f, 51.03f, 74.12f, 51.29f, 74.37f)
                lineTo(57.29f, 80.35f)
                curveTo(57.46f, 80.6f, 57.79f, 80.68f, 58.12f, 80.51f)
                curveTo(58.37f, 80.43f, 58.62f, 80.1f, 58.62f, 79.85f)
                verticalLineTo(52.2f)
                curveTo(58.62f, 49.55f, 56.46f, 47.39f, 53.79f, 47.39f)
                horizontalLineTo(7.34f)
                curveTo(4.67f, 47.39f, 2.5f, 49.55f, 2.5f, 52.2f)
                verticalLineTo(69.14f)
                close()
            }
            path(fill = SolidColor(Color(0xFFCED6E2)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(38.13f, 27.51f)
                curveTo(38.13f, 30.17f, 40.3f, 32.33f, 42.97f, 32.33f)
                horizontalLineTo(50.2f)
                curveTo(50.62f, 32.33f, 51.04f, 32.49f, 51.29f, 32.74f)
                lineTo(57.29f, 38.72f)
                curveTo(57.46f, 38.97f, 57.79f, 39.05f, 58.12f, 38.88f)
                curveTo(58.37f, 38.8f, 58.62f, 38.47f, 58.62f, 38.22f)
                verticalLineTo(18.82f)
                curveTo(58.62f, 16.16f, 56.46f, 14.0f, 53.79f, 14.0f)
                horizontalLineTo(42.97f)
                curveTo(40.3f, 14.0f, 38.13f, 16.16f, 38.13f, 18.82f)
                verticalLineTo(27.51f)
                close()
            }
            path(fill = linearGradient(0.01f to Color(0x00A6AEBE), 1.0f to Color(0xFFA6AAB5), start
                    = Offset(49.8f,56.43f), end = Offset(10.86f,56.43f)), stroke = null, fillAlpha =
                    0.7f, strokeAlpha = 0.7f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                    strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(48.12f, 58.18f)
                horizontalLineTo(11.26f)
                curveTo(10.92f, 58.18f, 10.59f, 57.85f, 10.59f, 57.52f)
                verticalLineTo(55.36f)
                curveTo(10.59f, 55.03f, 10.92f, 54.69f, 11.26f, 54.69f)
                horizontalLineTo(48.12f)
                curveTo(48.45f, 54.69f, 48.78f, 55.03f, 48.78f, 55.36f)
                verticalLineTo(57.52f)
                curveTo(48.78f, 57.93f, 48.53f, 58.18f, 48.12f, 58.18f)
                close()
            }
            path(fill = linearGradient(0.01f to Color(0x00A6AEBE), 1.0f to Color(0xFFA6AAB5), start
                    = Offset(35.25f,65.48f), end = Offset(10.76f,65.48f)), stroke = null, fillAlpha
                    = 0.7f, strokeAlpha = 0.7f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                    strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(33.94f, 67.23f)
                horizontalLineTo(11.26f)
                curveTo(10.92f, 67.23f, 10.59f, 66.9f, 10.59f, 66.57f)
                verticalLineTo(64.41f)
                curveTo(10.59f, 64.08f, 10.92f, 63.74f, 11.26f, 63.74f)
                horizontalLineTo(33.94f)
                curveTo(34.27f, 63.74f, 34.61f, 64.08f, 34.61f, 64.41f)
                verticalLineTo(66.57f)
                curveTo(34.61f, 66.98f, 34.36f, 67.23f, 33.94f, 67.23f)
                close()
            }
            path(fill = SolidColor(Color(0xFFCED6E2)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(118.5f, 36.51f)
                curveTo(118.5f, 38.92f, 116.58f, 40.83f, 114.17f, 40.83f)
                horizontalLineTo(75.64f)
                curveTo(75.3f, 40.83f, 74.89f, 41.0f, 74.64f, 41.25f)
                lineTo(69.22f, 46.64f)
                curveTo(69.05f, 46.81f, 68.72f, 46.89f, 68.47f, 46.81f)
                curveTo(68.22f, 46.73f, 68.05f, 46.48f, 68.05f, 46.23f)
                verticalLineTo(21.32f)
                curveTo(68.05f, 18.91f, 69.97f, 17.0f, 72.39f, 17.0f)
                horizontalLineTo(114.08f)
                curveTo(116.5f, 17.0f, 118.42f, 18.91f, 118.42f, 21.32f)
                verticalLineTo(36.51f)
                horizontalLineTo(118.5f)
                close()
            }
            path(fill = linearGradient(0.01f to Color(0x00A6AEBE), 1.0f to Color(0xFFA6AAB5), start
                    = Offset(111.41f,25.13f), end = Offset(76.46f,25.13f)), stroke = null, fillAlpha
                    = 0.7f, strokeAlpha = 0.7f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                    strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(109.91f, 26.72f)
                horizontalLineTo(76.8f)
                curveTo(76.47f, 26.72f, 76.22f, 26.47f, 76.22f, 26.14f)
                verticalLineTo(24.14f)
                curveTo(76.22f, 23.81f, 76.47f, 23.56f, 76.8f, 23.56f)
                horizontalLineTo(109.91f)
                curveTo(110.25f, 23.56f, 110.5f, 23.81f, 110.5f, 24.14f)
                verticalLineTo(26.14f)
                curveTo(110.5f, 26.47f, 110.25f, 26.72f, 109.91f, 26.72f)
                close()
            }
            path(fill = linearGradient(0.01f to Color(0x00A6AEBE), 1.0f to Color(0xFFA6AAB5), start
                    = Offset(98.4f,33.27f), end = Offset(76.37f,33.27f)), stroke = null, fillAlpha =
                    0.7f, strokeAlpha = 0.7f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                    strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(97.24f, 34.85f)
                horizontalLineTo(76.8f)
                curveTo(76.47f, 34.85f, 76.22f, 34.6f, 76.22f, 34.27f)
                verticalLineTo(32.28f)
                curveTo(76.22f, 31.95f, 76.47f, 31.7f, 76.8f, 31.7f)
                horizontalLineTo(97.24f)
                curveTo(97.57f, 31.7f, 97.82f, 31.95f, 97.82f, 32.28f)
                verticalLineTo(34.27f)
                curveTo(97.82f, 34.52f, 97.49f, 34.85f, 97.24f, 34.85f)
                close()
            }
            path(fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(48.39f, 17.81f)
                curveTo(45.41f, 17.81f, 43.01f, 20.23f, 43.01f, 23.17f)
                curveTo(43.01f, 26.12f, 45.43f, 28.53f, 48.39f, 28.53f)
                curveTo(51.36f, 28.53f, 53.78f, 26.14f, 53.78f, 23.17f)
                curveTo(53.78f, 20.23f, 51.38f, 17.84f, 48.39f, 17.81f)
                close()
                moveTo(50.45f, 20.52f)
                curveTo(50.94f, 20.52f, 51.33f, 20.91f, 51.33f, 21.39f)
                curveTo(51.33f, 21.88f, 50.94f, 22.27f, 50.45f, 22.27f)
                curveTo(49.96f, 22.27f, 49.57f, 21.88f, 49.57f, 21.39f)
                curveTo(49.57f, 20.91f, 49.96f, 20.52f, 50.45f, 20.52f)
                close()
                moveTo(46.36f, 20.49f)
                curveTo(46.85f, 20.49f, 47.24f, 20.88f, 47.24f, 21.37f)
                curveTo(47.24f, 21.86f, 46.85f, 22.25f, 46.36f, 22.25f)
                curveTo(45.87f, 22.25f, 45.48f, 21.86f, 45.48f, 21.37f)
                curveTo(45.48f, 20.91f, 45.87f, 20.49f, 46.36f, 20.49f)
                close()
                moveTo(51.5f, 24.41f)
                curveTo(50.84f, 26.12f, 48.93f, 26.97f, 47.22f, 26.34f)
                curveTo(46.34f, 26.0f, 45.63f, 25.29f, 45.28f, 24.41f)
                curveTo(45.21f, 24.19f, 45.31f, 23.97f, 45.53f, 23.9f)
                curveTo(45.58f, 23.88f, 45.63f, 23.88f, 45.68f, 23.88f)
                horizontalLineTo(51.11f)
                curveTo(51.33f, 23.88f, 51.53f, 24.07f, 51.53f, 24.29f)
                curveTo(51.53f, 24.32f, 51.53f, 24.36f, 51.5f, 24.41f)
                close()
            }
        }
        .build()
        return _noContact!!
    }

private var _noContact: ImageVector? = null
