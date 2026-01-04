package com.mangala.wallet.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily

@Composable
fun GradientTermsCheckbox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onTermsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF3B90FF),
            Color(0xFFC27DFF)
        )
    )
    
    val annotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = Color(0xFFF1F5F9),
                fontSize = 14.sp,
                fontFamily = getInterFontFamily()
            )
        ) {
            append("I agree to the ")
        }
        pushStringAnnotation(tag = "TERMS", annotation = "terms")
        withStyle(
            style = SpanStyle(
                color = Color(0xFF3B90FF),
                fontSize = 14.sp,
                fontFamily = getInterFontFamily()
            )
        ) {
            append("Terms of Service")
        }
        pop()
    }
    
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .then(
                    if (isChecked) {
                        Modifier.background(gradientBrush)
                    } else {
                        Modifier.border(
                            width = 2.dp,
                            brush = gradientBrush,
                            shape = CircleShape
                        )
                    }
                )
                .clickable { onCheckedChange(!isChecked) },
            contentAlignment = Alignment.Center
        ) {
            if (isChecked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Checked",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        ClickableText(
            text = annotatedString,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                letterSpacing = (-0.14).sp,
                lineHeight = 19.6.sp,
                fontFamily = getInterFontFamily()
            ),
            onClick = { offset ->
                annotatedString.getStringAnnotations(
                    tag = "TERMS",
                    start = offset,
                    end = offset
                ).firstOrNull()?.let {
                    onTermsClick()
                }
            }
        )
    }
}