package com.mangala.wallet.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.TextTiny
import dev.icerock.moko.resources.compose.fontFamilyResource

@Composable
fun ShowAccountInfo(
    account: String,
    accountBalance: @Composable () -> Unit,
    onClickAccountInfo: () -> Unit,
    address: String
) {
    Column(
        modifier = Modifier.fillMaxWidth().background(color = MaterialTheme.colors.primary)
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(CornerRadius.Tiny)
            ).clickable(onClick = onClickAccountInfo)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.SMALL),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = account,
                    color = MaterialTheme.colors.onSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = FontType.SMALL_18,
                    fontFamily = fontFamilyResource(MR.fonts.sfpro)
                )
                TextTiny(address)
            }
        }
        Divider(
            color = Color.LightGray,
            modifier = Modifier.height(1.dp)
                .padding(start = Spacing.SMALL, end = Spacing.SMALL)
        )
        accountBalance()
    }
}
