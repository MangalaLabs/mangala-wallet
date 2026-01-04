package com.mangala.wallet.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun MangalaGradientButton(
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    size: MangalaButtonSize = MangalaButtonSize.Big,
    modifier: Modifier = Modifier,
    buttonStyle: MangalaButtonStyle = MangalaButtonStyle.GRADIENT,
    style: TextStyle = MangalaTypography.Size17SemiBold()
) {
    MangalaGradientButton(
        onClick = onClick,
        enabled = enabled,
        size = size,
        buttonStyle = buttonStyle,
        modifier = modifier
    ) {
        Text(
            text = label,
            style = style,
            fontSize = size.fontSize,
            color = when (buttonStyle) {
                MangalaButtonStyle.GRADIENT -> if (enabled) Color.White else Color(0xFF94A3B8)
                MangalaButtonStyle.TRANSPARENT -> if (enabled) Color.White else Color(0xFF94A3B8)
                MangalaButtonStyle.OUTLINED -> if (enabled) MaterialTheme.mangalaColors.textPrimary else Color(0xFF94A3B8)
                MangalaButtonStyle.SOLID_GRAY -> if (enabled) Color.White else Color(0xFF94A3B8)
            },
            textAlign = TextAlign.Center,
            letterSpacing = (-0.17).sp,
            lineHeight = 23.8.sp,
        )
    }
}

@Composable
fun MangalaGradientButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    size: MangalaButtonSize = MangalaButtonSize.Big,
    modifier: Modifier = Modifier.wrapContentSize(),
    buttonStyle: MangalaButtonStyle = MangalaButtonStyle.GRADIENT,
    content: @Composable () -> Unit
) {
    val backgroundBrush = if (enabled) {
        MaterialTheme.mangalaColors.bgHighlight
    } else {
        MaterialTheme.mangalaColors.bgHighlightDisabled
    }

    Box(
        modifier
            .defaultMinSize(minHeight = size.height)
            .then(
                when (buttonStyle) {
                    MangalaButtonStyle.GRADIENT -> {
                        Modifier.background(
                            brush = backgroundBrush,
                            shape = RoundedCornerShape(1000.dp)
                        )
                    }
                    MangalaButtonStyle.TRANSPARENT -> {
                        Modifier
                    }
                    MangalaButtonStyle.OUTLINED -> {
                        Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.mangalaColors.border,
                            shape = RoundedCornerShape(1000.dp)
                        )
                    }
                    MangalaButtonStyle.SOLID_GRAY -> {
                        Modifier.background(
                            color = if (enabled) MaterialTheme.mangalaColors.bgInnerCard else MaterialTheme.mangalaColors.bgInnerCard.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(1000.dp)
                        )
                    }
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Button(
            modifier = Modifier.matchParentSize(),
            onClick = onClick,
            enabled = enabled,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                hoveredElevation = 0.dp,
                focusedElevation = 0.dp
            ),
            shape = RoundedCornerShape(1000.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color(0xFF94A3B8)
            ),
            contentPadding = PaddingValues(
                horizontal = size.horizontalPadding,
                vertical = size.verticalPadding
            ),
        ) {
            content()
        }
    }
}