package com.mangala.wallet.features.receive.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.Screen
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Delete
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcEdit
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlin.jvm.Transient

class EditReceiveAmountScreen(
    @Transient private val onEditAmount: () -> Unit,
    @Transient private val onRemoveAmount: () -> Unit,
) : Screen {

    @Composable
    override fun Content() {
        OnboardingGradientBackground(modifier = Modifier.fillMaxWidth()) {
            MaxWidthColumn(
                modifier = Modifier
                    .padding(
                        vertical = Dimensions.Padding.half,
                        horizontal = Dimensions.Padding.default
                    )
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(Dimensions.BottomSheetDragHandleWidth)
                        .height(Dimensions.BottomSheetDragHandleHeight)
                        .background(
                            color = MaterialTheme.mangalaColors.border,
                            shape = RoundedCornerShape(CornerRadius.Medium)
                        )
                )

                Spacer(modifier = Modifier.height(Spacing.XXBASE))

                Spacer(modifier = Modifier.height(Spacing.BASE))

                OptionRow(
                    title = MR.strings.message_receiveToken_editAmount_editAmountOption.desc()
                        .localized(),
                    icon = MangalaWalletPack.IcEdit,
                    onClick = onEditAmount
                )

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth()
                        .padding(vertical = Dimensions.Padding.default),
                    color = MaterialTheme.mangalaColors.border,
                )

                OptionRow(
                    title = MR.strings.message_receiveToken_editAmount_removeAmountOption.desc()
                        .localized(),
                    icon = MangalaWalletPack.Delete,
                    onClick = onRemoveAmount
                )

                Spacer(modifier = Modifier.height(Spacing.SMALL))
            }
        }
    }

    @Composable
    private fun OptionRow(
        title: String,
        icon: ImageVector,
        onClick: () -> Unit
    ) {
        MaxWidthRow(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(vertical = Dimensions.Padding.half)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.mangalaColors.iconPrimary,
                modifier = Modifier.size(Dimensions.IconSizeNextToText),
            )

            Spacer(modifier = Modifier.width(Spacing.XSMALL))

            Text(
                text = title,
                style = MangalaTypography.Size14Medium(),
                color = MaterialTheme.mangalaColors.textPrimary,
            )
        }
    }
}