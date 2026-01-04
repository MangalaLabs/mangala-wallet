package com.mangala.wallet.features.addressbook.presentation.contact.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.presentation.avatar.AvatarIcon
import com.mangala.wallet.features.addressbook.utils.stringToColor
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun ContactHeader(
    name: String,
    tags: List<TagEntity>,
    icon: String?
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
        color = MaterialTheme.mangalaColors.bgInnerCard,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            AvatarIcon(
                name = name,
                iconString = icon,
                modifier = Modifier.size(96.dp),
                size = 96.dp,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Contact Name
            Text(
                text = name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tags section - only show if there are tags
            if (tags.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 10,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Tags
                    tags.forEach { tag ->
                        TagChip(
                            label = tag.name,
                            color = stringToColor(
                                tag.color,
                                MaterialTheme.mangalaColors.iconSecondary
                            ),
                            modifier = Modifier.padding(
                                end = if (tag != tags.last()) 12.dp else 0.dp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TagChip(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.mangalaColors.bgTagLight)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        BasicText(
            text = label,
            style = MangalaTypography.Size13Medium().copy(
                color = MaterialTheme.mangalaColors.textTag
            )
        )
    }
}