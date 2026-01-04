package com.mangala.wallet.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily

/**
 * Reusable component for displaying text containing "Mangala" with gradient branding
 * @param fullText The complete text to display
 * @param fontSize The font size to use
 * @param fontWeight The font weight to use
 * @param lineHeight The line height to use
 * @param letterSpacing The letter spacing to use
 * @param textAlign The text alignment
 * @param modifier Optional modifier
 */
@Composable
fun MangalaBrandText(
    fullText: String,
    fontSize: TextUnit = 28.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    lineHeight: TextUnit = 39.2.sp,
    letterSpacing: TextUnit = (-0.28).sp,
    textAlign: TextAlign = TextAlign.Center,
    modifier: Modifier = Modifier
) {
    val mangalaGradient = Brush.linearGradient(
        colorStops = arrayOf(
            0.0f to Color(0xFF227BFF),
            0.32692307f to Color(0xFF1C8DF9),
            0.66346156f to Color(0xFFB988EE),
            1.0f to Color(0xFFEE4D5D)
        )
    )
    
    if (fullText.contains("Mangala")) {
        val parts = fullText.split("Mangala", limit = 2)
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (parts.isNotEmpty() && parts[0].isNotEmpty()) {
                Text(
                    text = parts[0],
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    color = Color.White,
                    textAlign = textAlign,
                    letterSpacing = letterSpacing,
                    lineHeight = lineHeight,
                    fontFamily = getInterFontFamily()
                )
            }
            
            Text(
                text = "Mangala",
                fontSize = fontSize,
                fontWeight = fontWeight,
                textAlign = textAlign,
                letterSpacing = letterSpacing,
                lineHeight = lineHeight,
                fontFamily = getInterFontFamily(),
                style = TextStyle(
                    brush = mangalaGradient
                )
            )
            
            if (parts.size > 1 && parts[1].isNotEmpty()) {
                Text(
                    text = parts[1],
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    color = Color.White,
                    textAlign = textAlign,
                    letterSpacing = letterSpacing,
                    lineHeight = lineHeight,
                    fontFamily = getInterFontFamily()
                )
            }
        }
    } else {
        // Fallback for text without "Mangala"
        Text(
            text = fullText,
            fontSize = fontSize,
            fontWeight = fontWeight,
            color = Color.White,
            textAlign = textAlign,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight,
            fontFamily = getInterFontFamily(),
            modifier = modifier
        )
    }
}