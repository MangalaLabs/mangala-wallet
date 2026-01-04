package com.mangala.eticket.presentation.category

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.eticket.presentation.error.GeneralError
import com.mangala.eticket.presentation.event.list.EventListScreen
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics

class CategoryScreen: BaseScreen<CategoryScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ETICKET_CATEGORY
    override val screenClassName: String = CategoryScreen::class.simpleName.orEmpty()
    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): CategoryScreenModel {
        return getScreenModel()
    }

    @Composable
    override fun ScreenContent(screenModel: CategoryScreenModel) {
        val localNavigator = LocalNavigator.currentOrThrow
        val hasUnknownError = screenModel.unknownError.collectAsStateMultiplatform().value
        val categories = screenModel.categories.collectAsStateMultiplatform().value

        if (hasUnknownError) {
            GeneralError()
        } else {
            LazyColumn {
                items(categories.size) {
                    Button(onClick = {
                        localNavigator.push(
                            EventListScreen(categories[it].id)
                        )
                    }) {
                        categories[it].name?.let { it1 -> Text(text = it1) }
                    }
                }
            }
        }
    }
}