package com.mangala.wallet.pin.presentation.base

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.biometry.presentation.BiometryByDevice
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Faceid
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.KeyDelete
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform

@Composable
fun SixDigitsV2(screenModel: BasePinScreenModel) {
    val pinEntered by screenModel.pinEntered.collectAsStateMultiplatform()

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..PinConstants.PIN_LENGTH) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        color = Color.Transparent,
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFFF1F5F9),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (i <= pinEntered.length) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                color = Color.White,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun KeyPadV2(screenModel: BasePinScreenModel, enableBiometric: Boolean, biometryByDevice: BiometryByDevice) {
    val enabled by screenModel.keyPadEnabled.collectAsStateMultiplatform()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Row 1: 1, 2, 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NumberButtonV2(number = "1", enabled = enabled, onClick = { screenModel.onDigitPressed("1") })
            NumberButtonV2(number = "2", enabled = enabled, onClick = { screenModel.onDigitPressed("2") })
            NumberButtonV2(number = "3", enabled = enabled, onClick = { screenModel.onDigitPressed("3") })
        }
        
        // Row 2: 4, 5, 6
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NumberButtonV2(number = "4", enabled = enabled, onClick = { screenModel.onDigitPressed("4") })
            NumberButtonV2(number = "5", enabled = enabled, onClick = { screenModel.onDigitPressed("5") })
            NumberButtonV2(number = "6", enabled = enabled, onClick = { screenModel.onDigitPressed("6") })
        }
        
        // Row 3: 7, 8, 9
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NumberButtonV2(number = "7", enabled = enabled, onClick = { screenModel.onDigitPressed("7") })
            NumberButtonV2(number = "8", enabled = enabled, onClick = { screenModel.onDigitPressed("8") })
            NumberButtonV2(number = "9", enabled = enabled, onClick = { screenModel.onDigitPressed("9") })
        }
        
        // Row 4: Biometric/Empty, 0, Delete
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (enableBiometric) {
                val icon = when (biometryByDevice) {
                    BiometryByDevice.IOS_TOUCH_ID -> MangalaWalletPack.Faceid
                    BiometryByDevice.IOS_FACE_ID -> MangalaWalletPack.Faceid
                    BiometryByDevice.ANDROID_FINGERPRINT -> MangalaWalletPack.Faceid
                    BiometryByDevice.ANDROID_FACE_ID -> MangalaWalletPack.Faceid
                    else -> MangalaWalletPack.Faceid
                }
                IconButton(
                    onClick = { screenModel.onClickBiometry() },
                    modifier = Modifier.size(64.dp),
                    enabled = enabled
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            } else {
                // Empty space for alignment - transparent button
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.Transparent, CircleShape)
                )
            }

            NumberButtonV2(number = "0", enabled = enabled, onClick = { screenModel.onDigitPressed("0") })
            
            // Delete button with special styling
            DeleteButtonV2(
                enabled = enabled,
                onClick = { screenModel.onDelete() }
            )
        }
    }
}

@Composable
fun NumberButtonV2(
    number: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF3B90FF).copy(alpha = 0.25f), // hsla(214, 100%, 62%, 1) with 25% opacity
            Color(0xFFC27DFF).copy(alpha = 0.25f)  // hsla(272, 100%, 75%, 1) with 25% opacity
        )
    )

    Box(
        modifier = modifier
            .size(56.dp)
            .shadow(
                elevation = 1.dp,
                shape = CircleShape,
                ambientColor = Color(0x26484848),
                spotColor = Color(0x26484848)
            )
            .background(gradient, CircleShape)
            .border(
                width = 1.dp,
                color = Color(0xFFF1F5F9),
                shape = CircleShape
            )
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number,
            color = Color(0xFFF1F5F9),
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.24).sp,
            lineHeight = 33.6.sp,
            textAlign = TextAlign.Center,
            fontFamily = getInterFontFamily()
        )
    }
}

@Composable
fun DeleteButtonV2(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(64.dp)
            .shadow(
                elevation = 1.dp,
                shape = CircleShape,
                ambientColor = Color(0x26484848),
                spotColor = Color(0x26484848)
            )
            .background(Color.Transparent, CircleShape)
            .border(
                width = 1.dp,
                color = Color.Transparent,
                shape = CircleShape
            )
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Delete icon container
        Box(
            modifier = Modifier.size(32.dp),
            contentAlignment = Alignment.Center
        ) {
            // Delete icon paths - simplified version based on Figma
            Icon(
                imageVector = MangalaWalletPack.KeyDelete,
                contentDescription = "Delete",
                tint = Color(0xFFF1F5F9),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}