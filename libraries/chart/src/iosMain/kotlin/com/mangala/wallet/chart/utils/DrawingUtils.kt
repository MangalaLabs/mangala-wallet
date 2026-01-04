/*
 * Copyright 2022 Himanshu Singh
 * Copyright 2023-2024 Mangala Wallet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file has been modified from the original Charty library.
 */
package com.mangala.wallet.chart.utils

import androidx.compose.ui.graphics.Color
import platform.UIKit.*

actual class DrawingUtils: IDrawingUtils {
    actual override fun drawText(text: String, x: Float, y: Float, textSize: Float, textColor: Color) {
//        val label = UILabel(frame = CGRect(x.toDouble(), y.toDouble(), 0.0, 0.0))
//        label.text = text
//        label.textColor = UIColor(color = textColor)
//        label.textAlignment = NSTextAlignmentCenter
//        label.font = label.font.fontWithSize(textSize.toDouble())
//        label.sizeToFit()

//        val labelWidth: CGFloat = 100.0 // Provide a desired width for the label
//        val labelHeight: CGFloat = 30.0 // Provide a desired height for the label
//        val labelFrame = CGRect(x.toDouble(), y.toDouble(), labelWidth, labelHeight)

        val label = UILabel().apply {
            this.text = text
            this.textColor = UIColor(red = textColor.red.toDouble(), green = textColor.green.toDouble(), blue = textColor.blue.toDouble(), alpha = textColor.alpha.toDouble())
            textAlignment = NSTextAlignmentCenter
            font = font.fontWithSize(textSize.toDouble())
            sizeToFit()
        }

    }
}