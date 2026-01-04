package com.mangala.wallet.features.addressbook.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.wallet.features.addressbook.icon.AllIcons
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen

class AllContactIconScreen : BaseScreen<StartScreenModel>() {

    @Composable
    override fun createScreenModel(): StartScreenModel {
        return getScreenModel()
    }

    override val screenName: String = "Test"
    override val screenClassName: String = "Test"
    override val isBottomBarVisible: Boolean
        get() = false

    @Composable
    override fun ScreenContent(screenModel: StartScreenModel) {
        LazyColumn(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
            item {
                Text("All icons")
            }

            items(
                items = ContactIcon.AllIcons
            ) { icon ->
                MaxWidthRow {
                    Image(
                        imageVector = icon,
                        contentDescription = icon.name,
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = icon.name,
                    )
                }
            }
        }
    }
}