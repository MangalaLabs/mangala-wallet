package com.mangala.wallet.features.chains.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.contact.ContactEntity
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.imageloader.LocalImage
import dev.icerock.moko.resources.compose.fontFamilyResource

fun LazyListScope.ContactItems(
    modifier: Modifier = Modifier,
    allContacts: List<ContactEntity>,
    onContactSelected: (ContactEntity) -> Unit,
    filter: String
) {
    item {
        Spacer(modifier = Modifier.height(Spacing.TINY))
        Spacer(modifier = Modifier.height(Spacing.LARGE))
    }
    items(allContacts.size) { index ->
        val contact = allContacts[index]

        Box(
            modifier = modifier
                .clickable {
                onContactSelected(contact)
            }.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.height(72.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LocalImage(
                    modifier = Modifier.size(32.dp).clip(CircleShape),
                    BlockchainType.fromUid(contact.blockchainUid).localImage,
                    isLoading = false,
                    placeholderModifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(8.dp))
                val text = buildAnnotatedString {
                    val name = contact.name
                    val defaultColor = MaterialTheme.colors.onPrimary
                    val highlightColor = MaterialTheme.colors.onSecondary

                    var startIndex = 0
                    val pattern = filter.toRegex(RegexOption.IGNORE_CASE)

                    pattern.findAll(name).forEach { result ->
                        val matchStart = result.range.first
                        val matchEnd = result.range.last
                        if (matchStart > startIndex) {
                            withStyle(style = SpanStyle(color = defaultColor)) {
                                append(name.substring(startIndex, matchStart))
                            }
                        }
                        withStyle(style = SpanStyle(color = highlightColor)) {
                            append(name.substring(matchStart, matchEnd + 1))
                        }
                        startIndex = matchEnd + 1
                    }

                    if (startIndex < name.length) {
                        withStyle(style = SpanStyle(color = defaultColor)) {
                            append(name.substring(startIndex))
                        }
                    }
                }

                Column {
                    Text(
                        text = text,
                        fontWeight = FontWeight.Normal,
                        fontSize = FontType.SMALL,
                        fontFamily = fontFamilyResource(MR.fonts.sfpro)
                    )
                    Text(
                        text = contact.address ?: "",
                        fontWeight = FontWeight.Thin,
                        fontSize = FontType.TINY,
                        fontFamily = fontFamilyResource(MR.fonts.sfpro)
                    )
                }

//                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
//                            Text(
//                                BlockchainType.fromUid(contact.blockchainUid).name,
//                                fontSize = FontType.SMALL,
//                                fontWeight = FontWeight(510),
//                                color = Colors.darkGray,
//                                textAlign = TextAlign.End,
//                            )
//                        }
            }
        }
    }
}