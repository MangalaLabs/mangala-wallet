package com.mangala.wallet.features.addressbook.presentation.contact.qr

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf

class ShowTagQrScreen(
    private val tagId: String,
    private val onBackClick: () -> Unit = {}
) : Screen, KoinComponent {

    @Composable
    override fun Content() {
        com.mangala.wallet.ui.LocalBottomNavigationVisibility.current.value = false
        
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<ShowContactQrScreenModel>(
            parameters = { parametersOf(QrDataType.Tag(tagId)) }
        )
        val qrDisplayData by screenModel.qrDisplayData.collectAsState()
        val isLoading by screenModel.isLoading.collectAsState()
        val errorState by screenModel.errorState.collectAsState()
        val qrCodeImage by screenModel.qrCodeImage.collectAsState()

        ShowQrScreenContent(
            isLoading = isLoading,
            qrDisplayData = qrDisplayData,
            error = errorState,
            qrCodeImage = qrCodeImage,
            onRetry = { screenModel.retryLoadContact() },
            onBackClick = { onBackClick(); navigator.pop() },
            generateQrContent = screenModel::generateQrContent
        )
    }
}