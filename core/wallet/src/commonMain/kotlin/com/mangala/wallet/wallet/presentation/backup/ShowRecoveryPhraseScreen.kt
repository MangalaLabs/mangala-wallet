package com.mangala.wallet.wallet.presentation.backup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.*
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

class ShowRecoveryPhraseScreen : BaseScreen<ShowRecoveryPhraseScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_SHOW_RECOVERY_PHRASE
    override val screenClassName: String = ShowRecoveryPhraseScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible = false

    @Composable
    override fun createScreenModel(): ShowRecoveryPhraseScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: ShowRecoveryPhraseScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val verifyRecoveryPhraseScreen = rememberScreen(SharedScreen.VerifyRecoveryPhraseScreen)

        val title = MR.strings.title_show_recovery_phrase.desc().localized()
        val description1 = MR.strings.description_show_recovery_phrase.desc().localized()
        val textButton = MR.strings.next.desc().localized().toUpperCase(Locale.current)

        val wallets = screenModel.wallets.collectAsStateMultiplatform()
        if(wallets.value.isNotEmpty()){
            val index = wallets.value.size - 1
            val words = wallets.value[index].words.split(" ")
//            println("words $words")
            BaseShowRecoveryPhraseScreen(title, description1, textButton, words, {
                navigator.pop()
            }, {
                navigator.push(verifyRecoveryPhraseScreen)
            })
        }


    }

    @Composable
    private fun BaseShowRecoveryPhraseScreen(
        title: String,
        description1: String,
        textButton: String,
        words: List<String>,
        onBackClicked: () -> Unit,
        onClickNext: (Boolean) -> Unit,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.background)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(Spacing.TINY))

            MangalaWalletTopBar(text= "", onBackClicked = onBackClicked)

            TextTitle3(
                text = title,
                modifier = Modifier.padding(start = Spacing.SMALL, end = Spacing.SMALL)
            )

            TextDescription1(
                text = description1,
                modifier = Modifier.padding(16.dp)
            )

            WordsArea(words)

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.padding(
                    start = Spacing.SMALL,
                    end = Spacing.SMALL,
                    bottom = Spacing.SMALL,
                    top = Spacing.SMALL
                )
            ) {
                ButtonNormal(
                    MR.strings.button_keep_it_safe.desc().localized(),
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = FontType.REGULAR,
                    onClick = { onClickNext(true) }
                )
            }

        }
    }

    @Composable
    fun WordsArea(words: List<String>) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            val maxLeft = words.size / 2 - 1
            val maxRight = words.size - 1
            Column(modifier = Modifier.weight(1f)) {
                for (i in 0..maxLeft) {
                    RecoveryPhraseText(
                        index = i,
                        text = words[i]
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                for (i in maxLeft + 1..maxRight) {
                    RecoveryPhraseText(
                        index = i,
                        text = words[i]
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    @Composable
    fun RecoveryPhraseText(index: Int, text: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val order = if (index + 1 < 10) {
                "0${index + 1}"
            } else {
                "${index + 1}"
            }
            TextDescription2(
                text = order,
                modifier = Modifier
                    .width(32.dp)
            )
            TextDescription1(
                text = text,
            )
        }
    }


}