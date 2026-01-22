package com.mangala.wallet.wallet.presentation.create

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.getSfProFamilyFont
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow

class CreateWalletScreen(
    private val blockchainUid: String? = null,
    private val antelopeAccountName: String? = null,
    private val listString: List<String>? = null,
    private val name: String? = null,
    private val createWalletCase: SharedScreen.CreateWalletScreen.CreateWalletScreenCase
) : BaseScreen<CreateWalletScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_CREATE_WALLET
    override val screenClassName: String = CreateWalletScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible = false

    @Composable
    override fun createScreenModel(): CreateWalletScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: CreateWalletScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val globalNavigator = LocalGlobalNavigator.current
        val homeScreen = rememberScreen(SharedScreen.HomeScreen())
        val textButton =
            MR.strings.using_mangala_wallet.desc().localized().toUpperCase(Locale.current)

        when (createWalletCase) {
            SharedScreen.CreateWalletScreen.CreateWalletScreenCase.CREATE_NEW_WALLET -> {
                val blockchainUid = blockchainUid ?: ""
                val antelopeAccountName = antelopeAccountName

                screenModel.createWallet(blockchainUid, antelopeAccountName)

                val backupWalletAlertScreen = rememberScreen(
                    SharedScreen.BackupWalletAlertScreen(
                        blockchainUid,
                        antelopeAccountName
                    )
                )
                GenerateWalletAnimation(textButton) {
                    navigator.replaceAll(backupWalletAlertScreen)
                }
            }

            SharedScreen.CreateWalletScreen.CreateWalletScreenCase.IMPORT_NEW_ACCOUNT -> {
                val blockchainUid = blockchainUid ?: ""
                val antelopeAccountName = antelopeAccountName

                screenModel.createWallet(blockchainUid, antelopeAccountName)

                GenerateWalletAnimation(textButton) {
                    navigator.replaceAll(homeScreen)
                }
            }

            SharedScreen.CreateWalletScreen.CreateWalletScreenCase.IMPORT_WALLET -> {
                val listString = listString ?: return
                val name = name ?: ""

                GenerateWalletAnimation(textButton) {
                    screenModel.restoreWallet(listString, name)
                }
                LaunchedEffect(true) {
                    screenModel.onCreateDone.receiveAsFlow().collectLatest {
                        navigator.replaceAll(homeScreen)
                    }
                }
            }
        }
        //
//        val backupWalletAlertScreen =
//            rememberScreen(SharedScreen.BackupWalletAlertScreen(blockchainUid, antelopeAccountName))


//        val wallets = screenModel.wallets.collectAsStateMultiplatform()
//        val testString = screenModel.testString.collectAsStateMultiplatform()

//        Column(Modifier.background(color = Color.White)) {
//            Text(wallets.value.map { it.words }.toString())
//            Text(testString.value)
//            screenModel.createWallet()
////            Button({
////                screenModel.createWallet()
////            }, colors = ButtonDefaults.buttonColors()) {
////                Text("Create wallet")
////            }
//
////            GenerateWalletScreen()
//        }
//        val textButton =
//            MR.strings.using_mangala_wallet.desc().localized().toUpperCase(Locale.current)
//        screenModel.createWallet()
//        GenerateWalletAnimation(textButton) {
////            screenModel.createWallet()
//            when (createWalletCase) {
//                is SharedScreen.CreateWalletScreen.CreateWalletScreenCase.CreateNewWallet -> {
//
//                    globalNavigator.replaceAll(backupWalletAlertScreen)
//
//                }
//
//                is SharedScreen.CreateWalletScreen.CreateWalletScreenCase.ImportNewAccount -> {
//                    globalNavigator.replaceAll(homeScreen)
//                }
//            }
////            navigator.push(backupWalletAlertScreen)
//        }

//        GenerateWalletScreen()

    }

}

@Composable
fun GenerateWalletAnimation(textButton: String, onClickStart: () -> Unit) {
    val animateState = remember { Animatable(0f) }
    val cursorVisible = remember { mutableStateOf(true) }
    val textState = remember { mutableStateOf("") }
    val showButton = remember { mutableStateOf(false) }

    val generateText = MR.strings.message_create_wallet_animation.desc().localized()
    val totalLength = generateText.length

    LaunchedEffect(Unit) {
        animateState.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 15000)
        )
        showButton.value = true
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            cursorVisible.value = !cursorVisible.value
        }
    }

    val currentIndex = (animateState.value * totalLength).toInt()
    val currentText = generateText.substring(0, currentIndex.coerceIn(0, totalLength))

    LaunchedEffect(currentIndex) {
        textState.value = currentText
    }

    OnboardingGradientBackground(
        afterBackgroundModifier = Modifier.safeDrawingPadding().padding(16.dp),
    ) {
        Column {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = textState.value,
                fontFamily = getSfProFamilyFont(FontWeight.SemiBold),
                fontSize = FontType.TITLE_2,
                textAlign = TextAlign.Start,
                lineHeight = FontType.TITLE_2_36,
                color = MaterialTheme.mangalaColors.textPrimary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        if (showButton.value && currentIndex >= totalLength) {
            Column(
                modifier = Modifier.padding(
                    bottom = Spacing.SMALL,
                    top = Spacing.SMALL
                ).align(Alignment.BottomCenter)
            ) {
                MangalaGradientButton(
                    label = textButton,
                    onClick = onClickStart,
                    enabled = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


//@Composable
//fun GenerateWalletAnimation(textButton: String, onClickStart: () -> Unit) {
//    val animateState = remember { Animatable(0f) }
//    val cursorBlinkState = remember { Animatable(1f) }
//    val textState = remember { mutableStateOf("") }
//    val showButton = remember { mutableStateOf(false) }
//
//    val generateText = "Generating Mangala wallet. Encrypting your private key using your PIN. Saving your encrypted keys to a local secure vault on this device. You can use BTC, ETH, MATIC, EOS EVM... All done! Your wallet is now ready."
//    val charArray = generateText.toCharArray()
//    val totalLength = charArray.size
//
//    LaunchedEffect(Unit) {
//        animateState.animateTo(
//            targetValue = 1f,
//            animationSpec = tween(durationMillis = 5000)
//        )
//        showButton.value = true
//    }
//
//    LaunchedEffect(Unit) {
//        while (true) {
//            cursorBlinkState.animateTo(
//                targetValue = if (cursorBlinkState.value == 1f) 0f else 1f,
//                animationSpec = tween(durationMillis = 500)
//            )
//        }
//    }
//
//    val currentIndex = (animateState.value * totalLength).toInt()
//    val currentText = generateText.substring(0, currentIndex.coerceIn(0, totalLength))
//    val cursorVisibility = cursorBlinkState.value
//
//    LaunchedEffect(currentIndex) {
//        textState.value = currentText
//    }
//
//    Scaffold(
//        backgroundColor = Color.White,
//        modifier = Modifier.fillMaxSize()
//    ) {
//        Box(
//            modifier = Modifier.fillMaxSize().padding(16.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = textState.value + if (currentIndex < totalLength) "|" else "",
//                fontFamily = FontFamily.Monospace,
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold,
//                textAlign = TextAlign.Center,
//                modifier = Modifier.alpha(cursorVisibility)
//            )
//            if (showButton.value && currentIndex >= totalLength) {
//                Column(
//                    modifier = Modifier.padding(
//                        bottom = Spacing.SMALL,
//                        top = Spacing.SMALL
//                    ).align(Alignment.BottomCenter)
//                ) {
//                    ButtonNormal(textButton, enabled = true, modifier = Modifier.fillMaxWidth()) {
//                        onClickStart()
//                    }
//                }
//            }
//        }
//    }
//}


//@Composable
//fun GenerateWalletAnimation(textButton: String, onClickStart: () -> Unit) {
//    val animateState = remember { Animatable(0f) }
//    val textState = remember { mutableStateOf("") }
//    val showButton = remember { mutableStateOf(false) }
//
//    val generateText = "Generating Mangala wallet. Encrypting your private key using your PIN. Saving your encrypted keys to a local secure vault on this device. You can use BTC, ETH, MATIC, EOS EVM... All done! Your wallet is now ready."
//    val lines = generateText.split("\n")
//    val charArrays = lines.map { it.toCharArray() }
//
//    val totalLength = charArrays.sumBy { it.size }
//
//    LaunchedEffect(Unit) {
//        animateState.animateTo(
//            targetValue = 1f,
//            animationSpec = tween(durationMillis = 5000)
//        )
//        showButton.value = true
//    }
//
//    val opacity = animateState.value.coerceIn(0f, 1f)
//    var currentIndex = (opacity * totalLength).toInt()
//
//    val textWithCursor = buildString {
//        for (charArray in charArrays) {
//            val lineLength = charArray.size
//            if (currentIndex <= lineLength) {
//                val currentLine = charArray.take(currentIndex)
//                append(currentLine.joinToString(separator = ""))
//                append("|")
//                append(charArray.drop(currentIndex).joinToString(separator = ""))
//                break
//            } else {
//                append(charArray)
//                currentIndex -= lineLength
//            }
//        }
//    }
//
//    LaunchedEffect(textWithCursor) {
//        textState.value = textWithCursor
//    }
//
//    Scaffold(
//        backgroundColor = Color.White,
//        modifier = Modifier.fillMaxSize()
//    ) {
//        Box(
//            modifier = Modifier.fillMaxSize().padding(16.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = textState.value,
//                fontFamily = FontFamily.Monospace,
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold,
//                textAlign = TextAlign.Center,
//                modifier = Modifier.alpha(opacity)
//            )
//            val currentLine = textState.value.substringBefore("|")
//            val cursorVisibility = if (currentIndex % 2 == 0) {
//                Modifier.alpha(1f)
//            } else {
//                Modifier.alpha(0f)
//            }
//            Text(
//                text = "|",
//                fontFamily = FontFamily.Monospace,
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold,
//                textAlign = TextAlign.Center,
//                modifier = Modifier
//                    .align(Alignment.CenterStart)
//                    .offset(x = (currentLine.length * 15).dp)
//                    .padding(end = 8.dp)
//                    .then(cursorVisibility)
//            )
//            if (showButton.value) {
//                Column(
//                    modifier = Modifier.padding(
//                        bottom = Spacing.SMALL,
//                        top = Spacing.SMALL
//                    ).align(Alignment.BottomCenter)
//                ) {
//                    ButtonNormal(textButton, enabled = true, modifier = Modifier.fillMaxWidth()) {
//                        onClickStart()
//                    }
//                }
//            }
//        }
//    }
//}





