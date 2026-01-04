package com.mangala.wallet.common.mokoresources.icons.mangalawalletpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
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
import kotlin.Unit

public val MangalaWalletPack.BackgroundDefaultQr: ImageVector
    get() {
        if (_backgroundDefaultQr != null) {
            return _backgroundDefaultQr!!
        }
        _backgroundDefaultQr = Builder(name = "BackgroundDefaultQr", defaultWidth = 375.0.dp,
                defaultHeight = 812.0.dp, viewportWidth = 375.0f, viewportHeight = 812.0f).apply {
            group {
                path(fill = linearGradient(0.0f to Color(0xFFFFFFFF), 1.0f to Color(0xFFFEF7E5),
                        start = Offset(187.5f,0.0f), end = Offset(187.5f,812.0f)), stroke = null,
                        strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                        strokeLineMiter = 4.0f, pathFillType = NonZero) {
                    moveTo(0.0f, 0.0f)
                    horizontalLineToRelative(375.0f)
                    verticalLineToRelative(812.0f)
                    horizontalLineToRelative(-375.0f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFE9923)), stroke = null, fillAlpha = 0.2f,
                        strokeAlpha = 0.2f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                        strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                    moveTo(264.15f, 751.77f)
                    curveTo(252.6f, 731.75f, 288.75f, 689.23f, 344.95f, 656.78f)
                    curveTo(401.14f, 624.35f, 456.05f, 614.28f, 467.61f, 634.29f)
                    curveTo(474.04f, 645.42f, 480.44f, 656.51f, 486.84f, 667.61f)
                    curveTo(498.41f, 687.65f, 462.24f, 730.18f, 406.07f, 762.63f)
                    curveTo(349.88f, 795.05f, 294.94f, 805.13f, 283.38f, 785.08f)
                    curveTo(276.98f, 773.99f, 270.57f, 762.89f, 264.15f, 751.77f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFECD3D)), stroke = null, fillAlpha = 0.2f,
                        strokeAlpha = 0.2f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                        strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                    moveTo(344.95f, 656.77f)
                    curveTo(401.14f, 624.35f, 456.05f, 614.28f, 467.61f, 634.29f)
                    curveTo(479.18f, 654.33f, 443.01f, 696.86f, 386.82f, 729.31f)
                    curveTo(330.65f, 761.73f, 275.72f, 771.81f, 264.15f, 751.77f)
                    curveTo(252.6f, 731.75f, 288.75f, 689.22f, 344.95f, 656.77f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFEA832)), stroke = null, fillAlpha = 0.2f,
                        strokeAlpha = 0.2f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                        strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                    moveTo(354.73f, 673.72f)
                    curveTo(399.43f, 647.91f, 440.67f, 635.66f, 446.81f, 646.31f)
                    curveTo(452.98f, 656.98f, 421.73f, 686.55f, 377.03f, 712.36f)
                    curveTo(332.33f, 738.15f, 291.11f, 750.43f, 284.95f, 739.78f)
                    curveTo(278.79f, 729.1f, 310.03f, 699.53f, 354.73f, 673.72f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFC85929)), stroke = null, fillAlpha = 0.2f,
                        strokeAlpha = 0.2f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                        strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                    moveTo(463.42f, 690.56f)
                    lineTo(473.67f, 708.3f)
                    curveTo(476.11f, 705.3f, 478.29f, 702.34f, 480.18f, 699.48f)
                    lineTo(472.13f, 685.53f)
                    lineTo(463.42f, 690.56f)
                    close()
                    moveTo(454.68f, 727.89f)
                    curveTo(452.19f, 730.12f, 449.62f, 732.33f, 446.92f, 734.53f)
                    lineTo(437.16f, 717.64f)
                    lineTo(445.87f, 712.65f)
                    lineTo(454.68f, 727.89f)
                    close()
                    moveTo(425.72f, 750.32f)
                    curveTo(422.96f, 752.18f, 420.16f, 754.02f, 417.3f, 755.83f)
                    lineTo(407.99f, 739.7f)
                    lineTo(416.7f, 734.69f)
                    lineTo(425.72f, 750.32f)
                    close()
                    moveTo(394.55f, 768.95f)
                    curveTo(391.56f, 770.52f, 388.55f, 772.04f, 385.58f, 773.49f)
                    lineTo(376.56f, 757.85f)
                    lineTo(385.24f, 752.81f)
                    lineTo(394.55f, 768.95f)
                    close()
                    moveTo(361.32f, 783.98f)
                    curveTo(358.06f, 785.19f, 354.84f, 786.31f, 351.69f, 787.36f)
                    lineTo(342.87f, 772.07f)
                    lineTo(351.56f, 767.09f)
                    lineTo(361.32f, 783.98f)
                    close()
                    moveTo(325.22f, 794.0f)
                    curveTo(321.39f, 794.63f, 317.74f, 795.02f, 314.33f, 795.23f)
                    lineTo(306.28f, 781.28f)
                    lineTo(314.96f, 776.24f)
                    lineTo(325.22f, 794.0f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFE9923)), stroke = null, fillAlpha = 0.14f,
                        strokeAlpha = 0.14f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                        strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                    moveTo(234.36f, 591.39f)
                    curveTo(232.42f, 588.04f, 238.48f, 580.92f, 247.89f, 575.48f)
                    curveTo(257.29f, 570.06f, 266.48f, 568.37f, 268.42f, 571.73f)
                    curveTo(269.49f, 573.58f, 270.56f, 575.44f, 271.64f, 577.3f)
                    curveTo(273.58f, 580.65f, 267.52f, 587.77f, 258.11f, 593.2f)
                    curveTo(248.71f, 598.63f, 239.52f, 600.32f, 237.58f, 596.97f)
                    curveTo(236.51f, 595.11f, 235.43f, 593.25f, 234.36f, 591.39f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFECD3D)), stroke = null, fillAlpha = 0.14f,
                        strokeAlpha = 0.14f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                        strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                    moveTo(247.89f, 575.48f)
                    curveTo(257.29f, 570.06f, 266.48f, 568.37f, 268.42f, 571.73f)
                    curveTo(270.36f, 575.08f, 264.3f, 582.2f, 254.9f, 587.63f)
                    curveTo(245.49f, 593.06f, 236.3f, 594.74f, 234.36f, 591.39f)
                    curveTo(232.42f, 588.04f, 238.48f, 580.92f, 247.89f, 575.48f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFEA832)), stroke = null, fillAlpha = 0.14f,
                        strokeAlpha = 0.14f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                        strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                    moveTo(249.52f, 578.32f)
                    curveTo(257.01f, 574.01f, 263.91f, 571.95f, 264.94f, 573.73f)
                    curveTo(265.97f, 575.52f, 260.74f, 580.47f, 253.26f, 584.79f)
                    curveTo(245.77f, 589.11f, 238.87f, 591.17f, 237.84f, 589.38f)
                    curveTo(236.81f, 587.59f, 242.04f, 582.64f, 249.52f, 578.32f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFC85929)), stroke = null, fillAlpha = 0.14f,
                        strokeAlpha = 0.14f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                        strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                    moveTo(267.72f, 581.14f)
                    lineTo(269.43f, 584.11f)
                    curveTo(269.84f, 583.61f, 270.21f, 583.12f, 270.52f, 582.64f)
                    lineTo(269.17f, 580.3f)
                    lineTo(267.72f, 581.14f)
                    close()
                    moveTo(266.25f, 587.4f)
                    curveTo(265.84f, 587.76f, 265.4f, 588.14f, 264.96f, 588.51f)
                    lineTo(263.32f, 585.67f)
                    lineTo(264.78f, 584.84f)
                    lineTo(266.25f, 587.4f)
                    close()
                    moveTo(261.4f, 591.15f)
                    curveTo(260.95f, 591.46f, 260.48f, 591.77f, 260.0f, 592.07f)
                    lineTo(258.44f, 589.37f)
                    lineTo(259.89f, 588.53f)
                    lineTo(261.4f, 591.15f)
                    close()
                    moveTo(256.19f, 594.26f)
                    curveTo(255.69f, 594.53f, 255.18f, 594.78f, 254.69f, 595.03f)
                    lineTo(253.17f, 592.41f)
                    lineTo(254.63f, 591.57f)
                    lineTo(256.19f, 594.26f)
                    close()
                    moveTo(250.63f, 596.78f)
                    curveTo(250.08f, 596.99f, 249.54f, 597.17f, 249.01f, 597.35f)
                    lineTo(247.54f, 594.79f)
                    lineTo(248.99f, 593.95f)
                    lineTo(250.63f, 596.78f)
                    close()
                    moveTo(244.58f, 598.46f)
                    curveTo(243.94f, 598.56f, 243.33f, 598.63f, 242.76f, 598.67f)
                    lineTo(241.41f, 596.33f)
                    lineTo(242.87f, 595.49f)
                    lineTo(244.58f, 598.46f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFE9923)), stroke = null, fillAlpha = 0.14f,
                        strokeAlpha = 0.14f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                        strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                    moveTo(291.81f, 550.85f)
                    curveTo(292.83f, 549.08f, 289.64f, 545.34f, 284.69f, 542.48f)
                    curveTo(279.74f, 539.62f, 274.9f, 538.73f, 273.88f, 540.5f)
                    curveTo(273.32f, 541.48f, 272.76f, 542.45f, 272.19f, 543.43f)
                    curveTo(271.17f, 545.2f, 274.36f, 548.95f, 279.31f, 551.8f)
                    curveTo(284.26f, 554.66f, 289.1f, 555.55f, 290.12f, 553.78f)
                    curveTo(290.68f, 552.8f, 291.25f, 551.83f, 291.81f, 550.85f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFECD3D)), stroke = null, fillAlpha = 0.14f,
                        strokeAlpha = 0.14f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                        strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                    moveTo(284.69f, 542.48f)
                    curveTo(279.74f, 539.62f, 274.9f, 538.73f, 273.88f, 540.5f)
                    curveTo(272.86f, 542.26f, 276.05f, 546.01f, 281.0f, 548.87f)
                    curveTo(285.95f, 551.73f, 290.79f, 552.61f, 291.81f, 550.85f)
                    curveTo(292.83f, 549.08f, 289.64f, 545.34f, 284.69f, 542.48f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFEA832)), stroke = null, fillAlpha = 0.14f,
                        strokeAlpha = 0.14f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                        strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                    moveTo(283.83f, 543.97f)
                    curveTo(279.89f, 541.7f, 276.26f, 540.62f, 275.72f, 541.55f)
                    curveTo(275.17f, 542.5f, 277.93f, 545.1f, 281.86f, 547.38f)
                    curveTo(285.8f, 549.65f, 289.44f, 550.73f, 289.98f, 549.79f)
                    curveTo(290.52f, 548.85f, 287.77f, 546.24f, 283.83f, 543.97f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFC85929)), stroke = null, fillAlpha = 0.14f,
                        strokeAlpha = 0.14f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                        strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                    moveTo(274.25f, 545.46f)
                    lineTo(273.35f, 547.02f)
                    curveTo(273.14f, 546.75f, 272.94f, 546.49f, 272.78f, 546.24f)
                    lineTo(273.49f, 545.01f)
                    lineTo(274.25f, 545.46f)
                    close()
                    moveTo(275.03f, 548.75f)
                    curveTo(275.24f, 548.94f, 275.47f, 549.14f, 275.71f, 549.33f)
                    lineTo(276.57f, 547.84f)
                    lineTo(275.8f, 547.4f)
                    lineTo(275.03f, 548.75f)
                    close()
                    moveTo(277.58f, 550.72f)
                    curveTo(277.82f, 550.88f, 278.07f, 551.05f, 278.32f, 551.21f)
                    lineTo(279.14f, 549.78f)
                    lineTo(278.37f, 549.34f)
                    lineTo(277.58f, 550.72f)
                    close()
                    moveTo(280.32f, 552.36f)
                    curveTo(280.59f, 552.5f, 280.85f, 552.63f, 281.11f, 552.76f)
                    lineTo(281.91f, 551.39f)
                    lineTo(281.14f, 550.94f)
                    lineTo(280.32f, 552.36f)
                    close()
                    moveTo(283.25f, 553.68f)
                    curveTo(283.54f, 553.79f, 283.82f, 553.89f, 284.1f, 553.98f)
                    lineTo(284.88f, 552.64f)
                    lineTo(284.11f, 552.2f)
                    lineTo(283.25f, 553.68f)
                    close()
                    moveTo(286.43f, 554.57f)
                    curveTo(286.77f, 554.62f, 287.09f, 554.66f, 287.39f, 554.68f)
                    lineTo(288.1f, 553.45f)
                    lineTo(287.33f, 553.01f)
                    lineTo(286.43f, 554.57f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFE9923)), stroke = null, fillAlpha = 0.2f,
                        strokeAlpha = 0.2f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                        strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                    moveTo(185.97f, 647.48f)
                    curveTo(194.48f, 640.34f, 185.12f, 615.17f, 165.05f, 591.3f)
                    curveTo(145.0f, 567.42f, 121.83f, 553.86f, 113.32f, 561.01f)
                    curveTo(108.6f, 564.97f, 103.89f, 568.95f, 99.16f, 572.91f)
                    curveTo(90.65f, 580.05f, 100.01f, 605.22f, 120.08f, 629.09f)
                    curveTo(140.14f, 652.97f, 163.3f, 666.53f, 171.81f, 659.38f)
                    curveTo(176.54f, 655.42f, 181.25f, 651.45f, 185.97f, 647.48f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFECD3D)), stroke = null, fillAlpha = 0.2f,
                        strokeAlpha = 0.2f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                        strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                    moveTo(165.05f, 591.3f)
                    curveTo(145.0f, 567.42f, 121.83f, 553.86f, 113.32f, 561.01f)
                    curveTo(104.81f, 568.17f, 114.18f, 593.32f, 134.23f, 617.19f)
                    curveTo(154.3f, 641.08f, 177.47f, 654.63f, 185.97f, 647.48f)
                    curveTo(194.48f, 640.34f, 185.12f, 615.17f, 165.05f, 591.3f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFEA832)), stroke = null, fillAlpha = 0.2f,
                        strokeAlpha = 0.2f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                        strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                    moveTo(157.85f, 597.35f)
                    curveTo(141.9f, 578.35f, 125.28f, 566.04f, 120.74f, 569.85f)
                    curveTo(116.21f, 573.65f, 125.47f, 592.15f, 141.43f, 611.14f)
                    curveTo(157.4f, 630.14f, 174.01f, 642.46f, 178.55f, 638.64f)
                    curveTo(183.07f, 634.84f, 173.81f, 616.35f, 157.85f, 597.35f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFC85929)), stroke = null, fillAlpha = 0.2f,
                        strokeAlpha = 0.2f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                        strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                    moveTo(105.98f, 587.11f)
                    lineTo(98.45f, 593.44f)
                    curveTo(97.84f, 591.69f, 97.33f, 590.01f, 96.95f, 588.41f)
                    lineTo(102.88f, 583.41f)
                    lineTo(105.98f, 587.11f)
                    close()
                    moveTo(103.81f, 605.43f)
                    curveTo(104.56f, 606.83f, 105.37f, 608.26f, 106.21f, 609.7f)
                    lineTo(113.4f, 603.67f)
                    lineTo(110.3f, 599.98f)
                    lineTo(103.81f, 605.43f)
                    close()
                    moveTo(113.21f, 620.32f)
                    curveTo(114.14f, 621.59f, 115.11f, 622.89f, 116.12f, 624.18f)
                    lineTo(122.97f, 618.41f)
                    lineTo(119.86f, 614.73f)
                    lineTo(113.21f, 620.32f)
                    close()
                    moveTo(124.22f, 633.84f)
                    curveTo(125.33f, 635.05f, 126.43f, 636.23f, 127.55f, 637.37f)
                    lineTo(134.19f, 631.79f)
                    lineTo(131.08f, 628.08f)
                    lineTo(124.22f, 633.84f)
                    close()
                    moveTo(136.79f, 646.08f)
                    curveTo(138.06f, 647.17f, 139.32f, 648.21f, 140.59f, 649.19f)
                    lineTo(147.07f, 643.74f)
                    lineTo(143.96f, 640.06f)
                    lineTo(136.79f, 646.08f)
                    close()
                    moveTo(151.45f, 656.55f)
                    curveTo(153.09f, 657.45f, 154.65f, 658.23f, 156.17f, 658.89f)
                    lineTo(162.1f, 653.91f)
                    lineTo(159.0f, 650.21f)
                    lineTo(151.45f, 656.55f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFE9923)), stroke = null, fillAlpha = 0.14f,
                        strokeAlpha = 0.14f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                        strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                    moveTo(108.57f, 493.28f)
                    curveTo(111.58f, 488.07f, 102.16f, 476.98f, 87.52f, 468.54f)
                    curveTo(72.89f, 460.09f, 58.58f, 457.47f, 55.57f, 462.68f)
                    curveTo(53.9f, 465.57f, 52.24f, 468.47f, 50.56f, 471.36f)
                    curveTo(47.55f, 476.57f, 56.97f, 487.66f, 71.61f, 496.1f)
                    curveTo(86.24f, 504.55f, 100.54f, 507.17f, 103.56f, 501.96f)
                    curveTo(105.23f, 499.07f, 106.9f, 496.17f, 108.57f, 493.28f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFECD3D)), stroke = null, fillAlpha = 0.14f,
                        strokeAlpha = 0.14f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                        strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                    moveTo(87.52f, 468.54f)
                    curveTo(72.89f, 460.09f, 58.58f, 457.47f, 55.57f, 462.68f)
                    curveTo(52.56f, 467.9f, 61.99f, 478.98f, 76.62f, 487.42f)
                    curveTo(91.25f, 495.88f, 105.56f, 498.49f, 108.57f, 493.28f)
                    curveTo(111.58f, 488.07f, 102.16f, 476.98f, 87.52f, 468.54f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFEA832)), stroke = null, fillAlpha = 0.14f,
                        strokeAlpha = 0.14f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                        strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = EvenOdd) {
                    moveTo(84.97f, 472.95f)
                    curveTo(73.34f, 466.23f, 62.59f, 463.03f, 60.99f, 465.81f)
                    curveTo(59.38f, 468.58f, 67.52f, 476.29f, 79.16f, 483.01f)
                    curveTo(90.81f, 489.73f, 101.55f, 492.93f, 103.15f, 490.15f)
                    curveTo(104.75f, 487.38f, 96.62f, 479.67f, 84.97f, 472.95f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFC85929)), stroke = null, fillAlpha = 0.14f,
                        strokeAlpha = 0.14f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                        strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                    moveTo(56.66f, 477.33f)
                    lineTo(54.0f, 481.95f)
                    curveTo(53.36f, 481.17f, 52.79f, 480.41f, 52.3f, 479.66f)
                    lineTo(54.4f, 476.02f)
                    lineTo(56.66f, 477.33f)
                    close()
                    moveTo(58.94f, 487.06f)
                    curveTo(59.59f, 487.64f, 60.26f, 488.22f, 60.96f, 488.79f)
                    lineTo(63.5f, 484.39f)
                    lineTo(61.24f, 483.08f)
                    lineTo(58.94f, 487.06f)
                    close()
                    moveTo(66.49f, 492.9f)
                    curveTo(67.2f, 493.38f, 67.93f, 493.86f, 68.68f, 494.33f)
                    lineTo(71.11f, 490.13f)
                    lineTo(68.84f, 488.83f)
                    lineTo(66.49f, 492.9f)
                    close()
                    moveTo(74.6f, 497.76f)
                    curveTo(75.38f, 498.17f, 76.16f, 498.56f, 76.94f, 498.94f)
                    lineTo(79.29f, 494.86f)
                    lineTo(77.02f, 493.55f)
                    lineTo(74.6f, 497.76f)
                    close()
                    moveTo(83.26f, 501.66f)
                    curveTo(84.11f, 501.98f, 84.94f, 502.28f, 85.77f, 502.55f)
                    lineTo(88.07f, 498.57f)
                    lineTo(85.8f, 497.27f)
                    lineTo(83.26f, 501.66f)
                    close()
                    moveTo(92.66f, 504.28f)
                    curveTo(93.66f, 504.44f, 94.61f, 504.55f, 95.5f, 504.6f)
                    lineTo(97.6f, 500.97f)
                    lineTo(95.34f, 499.65f)
                    lineTo(92.66f, 504.28f)
                    close()
                }
            }
        }
        .build()
        return _backgroundDefaultQr!!
    }

private var _backgroundDefaultQr: ImageVector? = null