package com.mangala.wallet.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasskeyBottomSheet(
    showBottomSheet: Boolean,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onGotItClick: () -> Unit = onDismiss
) {
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color(0xFF1D263E),
            contentColor = Color.White,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .background(
                            Color.White.copy(alpha = 0.3f),
                            RoundedCornerShape(2.dp)
                        )
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Title with emoji
                Text(
                    text = "🔐 What is a Passkey?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontFamily = getInterFontFamily()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = "Passkey helps you log in without passwords.",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFD1D1D1),
                    textAlign = TextAlign.Center,
                    lineHeight = 23.8.sp,
                    fontFamily = getInterFontFamily()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Benefits list
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PasskeyBenefitItem("✅ Super secure (even safer than OTP)")
                    PasskeyBenefitItem("✅ No need to remember anything")
                    PasskeyBenefitItem("✅ Works with your fingerprint or Face ID")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Security note
                Text(
                    text = "Your private key stays on your phone. We can't see it.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFD1D1D1),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.4.sp,
                    fontFamily = getInterFontFamily()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Got it button
                OnboardingButton(
                    text = "Got it",
                    onClick = onGotItClick,
                    isPrimary = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun PasskeyBenefitItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White,
            lineHeight = 22.4.sp,
            fontFamily = getInterFontFamily()
        )
    }
}