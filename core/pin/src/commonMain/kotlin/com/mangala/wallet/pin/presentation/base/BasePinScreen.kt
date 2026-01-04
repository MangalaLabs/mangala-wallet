package com.mangala.wallet.pin.presentation.base

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.mangala.wallet.biometry.presentation.BiometryByDevice
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Faceid
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.KeyDelete
import com.mangala.wallet.ui.NumberButton
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform

//
//abstract class BasePinScreen(): Screen{
//
//    lateinit var screenModel: BasePinScreenModel
//
//    @Composable
//    override fun Content() {
//        val globalNavigator = LocalGlobalNavigator.current
//        val homeScreen = rememberScreen(SharedScreen.HomeScreen())
//
//        val state by screenModel.pinScreenFlowState.collectAsStateMultiplatform()
//        if (state == PinScreenFlow.ShowHomeScreen) {
//            globalNavigator.replaceAll(homeScreen)
//        }
//    }
//}

@Composable
fun SixDigits(screenModel: BasePinScreenModel){
    val pinEntered by screenModel.pinEntered.collectAsStateMultiplatform()

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (i in 1..PinConstants.PIN_LENGTH) {
            Oval(
                fill = if (i <= pinEntered.length) Color.Black else Color.Transparent,
                stroke = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.size(Spacing.SMALL)
            )
            if(i < PinConstants.PIN_LENGTH){
                Spacer(modifier = Modifier.width(Spacing.SMALL))
            }
//            Spacer(modifier = Modifier.width(Spacing.SMALL))
        }
    }
}

@Composable
fun KeyPad(screenModel: BasePinScreenModel, enableBiometric: Boolean, biometryByDevice: BiometryByDevice){
    val marginHorizontalKeyPad = Spacing.SMALL
    val marginVerticalKeyPad = 2.dp

//    val enabled = remember { mutableStateOf(false) }
//    enabled.value = true

    val enabled by screenModel.keyPadEnabled.collectAsStateMultiplatform()

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(
            modifier = Modifier.padding(bottom = marginVerticalKeyPad),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NumberButton(number = "1", enabled = enabled, onClick = { screenModel.onDigitPressed("1") })
            NumberButton(
                number = "2",
                enabled = enabled,
                onClick = { screenModel.onDigitPressed("2") },
                modifier = Modifier.padding(horizontal = marginHorizontalKeyPad)
            )
            NumberButton(number = "3", enabled = enabled, onClick = { screenModel.onDigitPressed("3") })
        }
        Row(
            modifier = Modifier.padding(bottom = marginVerticalKeyPad),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NumberButton(number = "4", enabled = enabled, onClick = { screenModel.onDigitPressed("4") })
            NumberButton(
                number = "5",
                enabled = enabled,
                onClick = { screenModel.onDigitPressed("5") },
                modifier = Modifier.padding(horizontal = marginHorizontalKeyPad)
            )
            NumberButton(number = "6", enabled = enabled, onClick = { screenModel.onDigitPressed("6") })
        }
        Row(
            modifier = Modifier.padding(bottom = marginVerticalKeyPad),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NumberButton(number = "7", enabled = enabled, onClick = { screenModel.onDigitPressed("7") })
            NumberButton(
                number = "8",
                enabled = enabled,
                onClick = { screenModel.onDigitPressed("8") },
                modifier = Modifier.padding(horizontal = marginHorizontalKeyPad)
            )
            NumberButton(number = "9", enabled = enabled, onClick = { screenModel.onDigitPressed("9") })
        }
        Row(
            modifier = Modifier.padding(bottom = marginVerticalKeyPad),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            if(enableBiometric){
                val icon = when(biometryByDevice){
                    BiometryByDevice.IOS_TOUCH_ID -> MangalaWalletPack.Faceid
                    BiometryByDevice.IOS_FACE_ID -> MangalaWalletPack.Faceid
                    BiometryByDevice.ANDROID_FINGERPRINT -> MangalaWalletPack.Faceid
                    BiometryByDevice.ANDROID_FACE_ID -> MangalaWalletPack.Faceid
                    else -> MangalaWalletPack.Faceid
                }
                IconButton(
                    onClick = { screenModel.onClickBiometry() },
                    modifier = Modifier.size(72.dp).padding(12.dp),
                    enabled = enabled
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                    )
                }
            }else{
                Spacer(modifier = Modifier.size(72.dp))
            }

            NumberButton(
                number = "0",
                enabled = enabled,
                onClick = { screenModel.onDigitPressed("0") },
                modifier = Modifier.padding(horizontal = marginHorizontalKeyPad)
            )
            IconButton(
                onClick = { screenModel.onDelete() },
                modifier = Modifier.size(72.dp).padding(2.dp),
                enabled = enabled
            ) {
                Icon(
                    imageVector = MangalaWalletPack.KeyDelete,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
fun Oval(
    fill: Color,
    stroke: BorderStroke,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(color = fill, shape = CircleShape)
            .border(stroke, shape = CircleShape)
    )
}

@Composable
fun ShakeAnimation(
    targetValue: Float,
    onAnimationFinish: (Float) -> Unit = {},
    content: @Composable () -> Unit
) {
//    var shakeOffset by remember { mutableStateOf(0f) }
//    val shakeOffsetState = animateFloatAsState(
//        targetValue = targetValue,
//        animationSpec = repeatable(
//            iterations = 5,
//            animation = tween(durationMillis = 100, easing = LinearEasing)
//        ),
//        finishedListener = onAnimationFinish
//    )
//    shakeOffset = shakeOffsetState.value

    val shakeOffset = animateFloatAsState(
        targetValue = 10f,
        animationSpec = tween(durationMillis = 3000, easing = LinearOutSlowInEasing)
    )

    Box(modifier = Modifier.offset {
        IntOffset(shakeOffset.value.toInt(), 0)
    }){
        content()
    }


}
