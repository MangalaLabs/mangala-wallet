package com.mangala.wallet.wallet.presentation.backup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.OnboardingButton
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.bip39.BIP39_WORDLIST_ENGLISH
import kotlin.random.Random

class VerifyRecoveryPhraseScreen : BaseScreen<ShowRecoveryPhraseScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_VERIFY_RECOVERY_PHRASE
    override val screenClassName: String = VerifyRecoveryPhraseScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible = false

    @Composable
    override fun createScreenModel(): ShowRecoveryPhraseScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: ShowRecoveryPhraseScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val backupWalletDoneScreen = rememberScreen(SharedScreen.BackupWalletDoneScreen)
        val uiState by screenModel.uiState.collectAsStateMultiplatform()

        if (uiState.recoveryPhrase.isNotEmpty()) {
            val words = uiState.recoveryPhrase
            val words2 = BIP39_WORDLIST_ENGLISH.filterNot { it in words }

            VerifyPhraseContent(
                words = words,
                wrongWords = words2,
                onComplete = {
                    navigator.push(backupWalletDoneScreen)
                },
                onBack = {
                    navigator.pop()
                }
            )
        }
    }
}

@Composable
private fun VerifyPhraseContent(
    words: List<String>,
    wrongWords: List<String>,
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    val quizzes = remember { generateQuizzes(words, wrongWords) }
    val selectedWords = remember { mutableStateMapOf<Int, String>() }
    val allAnswersCorrect by derivedStateOf {
        selectedWords.size == 3 && selectedWords.all { (index, word) ->
            quizzes[index].correctAnswer == word
        }
    }

    OnboardingGradientBackground(
        circleBackgroundEnabled = true,
        afterBackgroundModifier = Modifier.navigationBarsPadding().imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onBack() }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = MangalaWalletPack.ArrowLeft,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Title and Description
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "You saved it, right?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    letterSpacing = (-0.2).sp,
                    lineHeight = 28.sp,
                    fontFamily = getInterFontFamily()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Verify that you saved your secret recovery phrase by tapping the words below that correspond to their numbers correctly.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFA5B4CB),
                    textAlign = TextAlign.Start,
                    letterSpacing = (-0.14).sp,
                    lineHeight = 19.6.sp,
                    fontFamily = getInterFontFamily()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Quiz sections
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                quizzes.forEachIndexed { index, quiz ->
                    QuizSection(
                        quiz = quiz,
                        selectedWord = selectedWords[index],
                        onWordSelected = { word ->
                            selectedWords[index] = word
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Confirm Button
            OnboardingButton(
                text = "Confirm",
                onClick = onComplete,
                isPrimary = allAnswersCorrect,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun QuizSection(
    quiz: Quiz,
    selectedWord: String?,
    onWordSelected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Select word ${quiz.number}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFFA5B4CB),
            fontFamily = getInterFontFamily()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            quiz.wordCards.forEach { word ->
                WordOption(
                    word = word,
                    isSelected = word == selectedWord,
                    onClick = { onWordSelected(word) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun WordOption(
    word: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientBrush = if (isSelected) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF3B90FF),
                Color(0xFFC27DFF)
            )
        )
    } else null

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (isSelected) {
                    Modifier
                        .background(gradientBrush!!)
                        .border(
                            width = 1.dp,
                            brush = gradientBrush,
                            shape = RoundedCornerShape(16.dp)
                        )
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = Color(0xFF2A3E6C),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            )
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = word,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFFF1F5F9),
            textAlign = TextAlign.Center,
            fontFamily = getInterFontFamily()
        )
    }
}

private data class Quiz(
    val number: Int,
    val wordCards: List<String>,
    val correctAnswer: String
)

private fun generateQuizzes(
    correctWords: List<String>,
    wrongWords: List<String>
): List<Quiz> {
    val random = Random.Default
    val quizzes = mutableListOf<Quiz>()

    // Select 3 random positions from the 12 words
    val positions = listOf(1, 8, 12) // As shown in the Figma design

    positions.forEach { position ->
        val index = position - 1 // Convert to 0-based index
        val correctWord = correctWords[index]

        // Get 2 random wrong words
        val randomWrongWords = wrongWords.shuffled(random).take(2)

        // Combine correct and wrong words, then shuffle
        val wordCards = listOf(correctWord, *randomWrongWords.toTypedArray()).shuffled(random)

        quizzes.add(Quiz(position, wordCards, correctWord))
    }

    return quizzes
}
