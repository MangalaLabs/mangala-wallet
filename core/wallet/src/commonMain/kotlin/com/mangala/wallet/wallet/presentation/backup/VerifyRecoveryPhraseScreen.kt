package com.mangala.wallet.wallet.presentation.backup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription1
import com.mangala.wallet.ui.TextSwitch
import com.mangala.wallet.ui.TextTitle3
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.bip39.BIP39_WORDLIST_ENGLISH
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlin.random.Random

class VerifyRecoveryPhraseScreen: BaseScreen<ShowRecoveryPhraseScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_VERIFY_RECOVERY_PHRASE
    override val screenClassName: String = VerifyRecoveryPhraseScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible = false

    @Composable
    override fun createScreenModel(): ShowRecoveryPhraseScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: ShowRecoveryPhraseScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val backupWalletDoneScreen = rememberScreen(SharedScreen.BackupWalletDoneScreen)

        val title = MR.strings.title_verify_recovery_phrase.desc().localized()
        val textButton = MR.strings.finish.desc().localized().toUpperCase(Locale.current)

        val wallets = screenModel.wallets.collectAsStateMultiplatform()
        if(wallets.value.isNotEmpty()) {
            val index = wallets.value.size - 1
            val words = wallets.value[index].words.split(" ")
            val words2 = BIP39_WORDLIST_ENGLISH.filterNot { it in words }
            BaseVerifyPhraseScreen(words, words2, title, textButton, {
                navigator.push(backupWalletDoneScreen)
            },{
                navigator.pop()
            })
        }
    }

    @Composable
    fun BaseVerifyPhraseScreen(
        arrayA: List<String>,
        arrayB: List<String>,
        title: String,
        textButton: String,
        onClickFinish: (Boolean) -> Unit,
        onBackClicked: (Boolean) -> Unit,
    ) {

        val quizzes = remember { generateQuizzes(arrayA, arrayB) }
        val selectedWords = remember { mutableMapOf<Int, String>() }
        val allAnswersCorrect = remember { mutableStateOf(false) }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colors.background)
                .windowInsetsPadding(WindowInsets.safeDrawing),
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            IconButton(onClick = {
                onBackClicked(true)
            }) {
                Icon(
                    imageVector = MangalaWalletPack.ArrowLeft,
                    contentDescription = "Back"
                )
            }

            TextTitle3(
                text = title,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )

            val spaceTop = 36.dp

            Spacer(modifier = Modifier.height(spaceTop))

            Column(modifier = Modifier.fillMaxSize()) {
                quizzes.forEachIndexed { index, quiz ->
                    Quiz(
                        quiz = quiz,
                        onWordSelected = { word ->
                            selectedWords[index] = word
                            allAnswersCorrect.value = selectedWords.size == 4 && selectedWords.all { (i, w) -> quizzes[i].correctAnswer == w }
                        }
                    )
                }

                Column(modifier = Modifier.padding(start = Spacing.SMALL, end = Spacing.SMALL, bottom = Spacing.SMALL, top = Spacing.LARGE)) {
                    ButtonNormal(
                        textButton,
                        enabled = allAnswersCorrect.value,
                        modifier = Modifier.fillMaxWidth(),
                        disabledBackgroundColor = Colors.grayWhite,
                        backgroundColor = Colors.darkGray,
                        onClick = { onClickFinish(true) })
                }
            }
        }
    }

    data class Quiz(val number: Int, val wordCards: List<String>, val correctAnswer: String)

    @Composable
    private fun Quiz(quiz: Quiz, onWordSelected: (String) -> Unit) {
        val selectedWord = remember { mutableStateOf<String?>(null) }
        val selectWordText = StringDesc.ResourceFormatted(MR.strings.select_word,  quiz.number.toString()).localized()

        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)) {
            TextDescription1(selectWordText)
            Spacer(modifier = Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                quiz.wordCards.forEach { word ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .fillMaxWidth()
                            .clickable {
                                selectedWord.value = word
                                onWordSelected(word)
                            }
                            .background(if (word == selectedWord.value) MaterialTheme.colors.onSecondary else MaterialTheme.colors.secondary, RoundedCornerShape(8.dp))
                            .height(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        TextSwitch(word, color = (if (word == selectedWord.value) MaterialTheme.colors.background else MaterialTheme.colors.background), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }

    private fun generateQuizzes(arrayA: List<String>, arrayB: List<String>): List<Quiz> {
        val random = Random.Default
        val quizzes = mutableListOf<Quiz>()
        val usedIndices = mutableSetOf<Int>()

        repeat(4) {
            var numberX: Int
            do {
                numberX = random.nextInt(arrayA.size)
            } while (usedIndices.contains(numberX))
            usedIndices.add(numberX)

            val correctWord = arrayA[numberX]
            val wrongWords = arrayB.filter { it != correctWord }.shuffled(random).take(2)

            val wordCards = listOf(correctWord, *wrongWords.toTypedArray()).shuffled(random)

            quizzes.add(Quiz(numberX + 1, wordCards, correctWord))
        }

        return quizzes
    }
}


