/*
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
 */
package com.mangala.wallet.scanqr

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.util.AttributeSet
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.Size
import com.mangala.wallet.utils.dpToPx
import kotlin.math.roundToInt

class CustomBarcodeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BarcodeView(context, attrs, defStyleAttr) {

//    override fun getFramingRectSize(): Size {
//        return Size(Resources.getSystem().displayMetrics.widthPixels, dpToPx(212))
//    }

    override fun calculateFramingRect(
        container: Rect?,
        surface: Rect?
    ): Rect {
        val marginTop = dpToPx(90)
        // create new rect instance that hold the container.
        val intersection = Rect(container)
        // specify the position of left direction.
        intersection.left = (width - framingRectSize.width) / 2
        // specify the position of top direction.
        intersection.top = marginTop
        // specify the position of right direction.
        intersection.right = (width - framingRectSize.width) / 2 + framingRectSize.width
        // specify the position of bottom direction.
        intersection.bottom =
            framingRectSize.height + marginTop
        return intersection
    }
}