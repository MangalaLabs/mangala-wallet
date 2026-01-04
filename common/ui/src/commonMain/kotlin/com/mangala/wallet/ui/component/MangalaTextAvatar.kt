package com.mangala.wallet.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.Dimensions.ButtonIconSize
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.ui.TextIconAvatar

@Composable
fun MangalaTextAvatar(modifier: Modifier, name: String) {
    TextIconAvatar(
        modifier = Modifier
            .size(ButtonIconSize)
            .background(
                color = getAvatarColor(name.firstOrNull()),
                shape = CircleShape
            )
            .then(modifier),
        text = name.firstOrNull()?.uppercase().toString(),
        color = ColorsNew.white,
        style = TextStyle(
            fontSize = FontType.SMALL,
            fontWeight = FontWeight.Normal,
            lineHeight = FontType.SMALL,
            textAlign = TextAlign.Center
        )
    )
}

private fun getAvatarColor(firstChar: Char?): Color {
    return when (firstChar?.lowercaseChar()) {
        'a' -> ColorsNew.avatarA
        'b' -> ColorsNew.avatarB
        'c' -> ColorsNew.avatarC
        'd' -> ColorsNew.avatarD
        'e' -> ColorsNew.avatarE
        'f' -> ColorsNew.avatarF
        'g' -> ColorsNew.avatarG
        'h' -> ColorsNew.avatarH
        'i' -> ColorsNew.avatarI
        'j' -> ColorsNew.avatarJ
        'k' -> ColorsNew.avatarK
        'l' -> ColorsNew.avatarL
        'm' -> ColorsNew.avatarM
        'n' -> ColorsNew.avatarN
        'o' -> ColorsNew.avatarO
        'p' -> ColorsNew.avatarP
        'q' -> ColorsNew.avatarQ
        'r' -> ColorsNew.avatarR
        's' -> ColorsNew.avatarS
        't' -> ColorsNew.avatarT
        'u' -> ColorsNew.avatarU
        'v' -> ColorsNew.avatarV
        'w' -> ColorsNew.avatarW
        'x' -> ColorsNew.avatarX
        'y' -> ColorsNew.avatarY
        'z' -> ColorsNew.avatarZ
        else -> ColorsNew.avatarA // Default to Colors.avatarA as fallback
    }
}