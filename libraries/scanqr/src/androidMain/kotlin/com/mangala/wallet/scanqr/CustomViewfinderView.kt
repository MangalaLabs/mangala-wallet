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
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.util.AttributeSet
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.ViewfinderView
import com.mangala.wallet.libraries.scanqr.R
import com.mangala.wallet.utils.dpToPx

class CustomViewfinderView(context: Context?, attrs: AttributeSet?) :
    ViewfinderView(context, attrs) {

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override fun onDraw(canvas: Canvas) {
        val borderRadius = getBorderRadius()

        refreshSizes()
        if (framingRect == null || previewSize == null) {
            return
        }

        paint.color = maskColor

        val frame = framingRect
        val previewFrame = previewSize

        val frameTop = frame.top.toFloat()
        val frameBottom = frame.bottom.toFloat()
        val frameLeft = frame.left.toFloat()
        val frameRight = frame.right.toFloat()

        val width = width.toFloat()
        val height = height.toFloat()

        canvas?.apply {
            val saveCount = canvas.saveLayer(0f, 0f, width, height, null)

            paint.color = maskColor
            drawPaint(paint) // Fill whole view with gray
            drawRoundRect(
                frameLeft,
                frameTop,
                frameRight,
                frameBottom,
                borderRadius,
                borderRadius,
                clearPaint
            ) // Clip out the scanning area
            canvas.restoreToCount(saveCount)
        }

        canvas?.let { canvas ->
            if (resultBitmap != null) {
                paint.alpha = CURRENT_POINT_OPACITY
                canvas.drawBitmap(resultBitmap, null, frame, paint)
            } else {
                //Vẽ viền
                drawFrameBounds(canvas, frame)

                val scaleX = frame.width() / previewFrame.width.toFloat()
                val scaleY = frame.height() / previewFrame.height.toFloat()

                val currentPossible = possibleResultPoints
                val currentLast = lastPossibleResultPoints

                if (currentPossible.isEmpty()) {
                    lastPossibleResultPoints = null
                } else {
                    possibleResultPoints = ArrayList<ResultPoint>(5)
                    lastPossibleResultPoints = currentPossible

                    paint.apply {
                        alpha = CURRENT_POINT_OPACITY
                        color = resultPointColor
                    }
                    currentPossible.forEach { point ->
                        canvas.drawCircle(
                            frameLeft + (point.x * scaleX),
                            frameTop + (point.y * scaleY),
                            POINT_SIZE.toFloat(), paint
                        )
                    }
                }

                currentLast?.let {
                    paint.apply {
                        alpha = CURRENT_POINT_OPACITY / 2
                        color = resultPointColor
                    }

                    val radius = POINT_SIZE / 2.0f

                    currentLast.forEach { point ->
                        canvas.drawCircle(
                            frameLeft + (point.x * scaleX),
                            frameTop + (point.y * scaleY),
                            radius, paint
                        )
                    }
                }
            }
        }
    }

    private fun drawFrameBounds(canvas: Canvas, frame: Rect) {
        val borderRadius = getBorderRadius()

        val frameTop = frame.top.toFloat()
        val frameBottom = frame.bottom.toFloat()
        val frameLeft = frame.left.toFloat()
        val frameRight = frame.right.toFloat()

        val strokeWidth = 15f

        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE  // Changed to STROKE
        paint.strokeWidth = strokeWidth  // Increased stroke width

        val corLength = borderRadius * 2

        canvas.apply {
            // Top left
            drawArc(
                frameLeft + strokeWidth / 2,
                frameTop + strokeWidth / 2,
                frameLeft + corLength,
                frameTop + corLength,
                180f,
                90f,
                false,
                paint
            )
            // Top right
            drawArc(
                frameRight - corLength,
                frameTop + strokeWidth / 2,
                frameRight - strokeWidth / 2,
                frameTop + corLength,
                -90f,
                90f,
                false,
                paint
            )
            // Bottom left
            drawArc(
                frameLeft + strokeWidth / 2,
                frameBottom - corLength,
                frameLeft + corLength,
                frameBottom - strokeWidth / 2,
                90f,
                90f,
                false,
                paint
            )
            // Bottom right
            drawArc(
                frameRight - corLength,
                frameBottom - corLength,
                frameRight - strokeWidth / 2,
                frameBottom - strokeWidth / 2,
                0f,
                90f,
                false,
                paint
            )
        }
    }

    private fun getBorderRadius(): Float {
        return context.resources.getDimension(R.dimen.radius_qr_scanner_border)
    }
}