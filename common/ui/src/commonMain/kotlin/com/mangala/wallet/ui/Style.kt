package com.mangala.wallet.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.InfoCircle
import dev.icerock.moko.resources.compose.fontFamilyResource

//TEXT
@Composable
fun TextTopBar(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = FontType.REGULAR,
    color: Color = MaterialTheme.colors.onPrimary,
    fontWeight: FontWeight? = null
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        modifier = modifier,
        fontFamily = getSfProFamilyFont(FontWeight.SemiBold),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        fontWeight = fontWeight
    )
}

@Composable
fun TextTab(text: String) {
    Text(
        text = text,
        fontFamily = fontFamilyResource(MR.fonts.sfpro),
        fontSize = FontType.MICRO_10,
    )
}

@Composable
@Deprecated(
    "This button doesn't have padding to ensure the touch target is large enough. Use MangalaWrappedTextButton instead.",
    replaceWith = ReplaceWith(
        "MangalaWrappedTextButton",
        "com.mangala.wallet.ui.MangalaWrappedTextButton"
    )
)
fun MangalaTextButton(
    text: String,
    fontSize: TextUnit = FontType.SMALL,
    fontWeight: FontWeight = FontWeight.Medium,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    isEnabled: Boolean = true,
    color: Color = if (isEnabled) MaterialTheme.colors.onPrimary else MaterialTheme.colors.secondary,
    onClick: () -> Unit
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        modifier = modifier.then(if (isEnabled) Modifier.clickable { onClick() } else Modifier),
        fontFamily = getSfProFamilyFont(fontWeight),
        textAlign = textAlign
    )
}

// Text button with larger touch target
@Composable
fun MangalaWrappedTextButton(
    text: String,
    fontSize: TextUnit = FontType.SMALL,
    fontWeight: FontWeight = FontWeight.Medium,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    isEnabled: Boolean = true,
    color: Color = if (isEnabled) MaterialTheme.colors.onPrimary else MaterialTheme.colors.secondary,
    onClick: () -> Unit
) {
    TextButton(
        onClick,
        modifier = modifier,
        enabled = isEnabled
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = textAlign,
            color = color,
            fontFamily = getSfProFamilyFont(fontWeight)
        )
    }
}

@Composable
fun TextTitle1(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colors.onPrimary,
        fontSize = FontType.TITLE_1,
        modifier = modifier,
        fontFamily = getSfProFamilyFont(FontWeight.SemiBold)
    )
}

@Composable
fun TextTitle2(text: String, modifier: Modifier = Modifier, color: Color = MaterialTheme.colors.onPrimary, fontWeight: FontWeight = FontWeight.SemiBold) {
    Text(
        text = text,
        color = color,
        fontSize = FontType.TITLE_2,
        modifier = modifier,
        fontFamily = getSfProFamilyFont(fontWeight)
    )
}

@Composable
fun TextTitle2_36(text: String, modifier: Modifier = Modifier, color: Color = MaterialTheme.colors.onPrimary) {
    Text(
        text = text,
        color = color,
        fontSize = FontType.TITLE_2_36,
        modifier = modifier,
        fontFamily = getSfProFamilyFont(FontWeight.SemiBold)
    )
}

@Composable
fun TextTitle3(text: String, modifier: Modifier = Modifier, color: Color = MaterialTheme.colors.onPrimary, fontWeight: FontWeight = FontWeight.SemiBold, textAlign: TextAlign? = TextAlign.Start) {
    Text(
        text = text,
        color = color,
        fontSize = FontType.TITLE_3_28,
        modifier = modifier,
        fontFamily = getSfProFamilyFont(fontWeight),
        textAlign = textAlign
    )
}

@Composable
fun TextTitle3_34(text: String, modifier: Modifier = Modifier, color: Color = MaterialTheme.colors.onPrimary, fontWeight: FontWeight = FontWeight.SemiBold, textAlign: TextAlign? = TextAlign.Start) {
    Text(
        text = text,
        color = color,
        fontSize = FontType.TITLE_3_34,
        modifier = modifier,
        fontFamily = getSfProFamilyFont(fontWeight),
        textAlign = textAlign
    )
}

@Composable
fun TextTitle4(
    text: String,
    color: Color = MaterialTheme.colors.onPrimary,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.SemiBold
) {
    Text(
        text = text,
        color = color,
        fontSize = FontType.TITLE_3,
        modifier = modifier,
        fontFamily = getSfProFamilyFont(fontWeight)
    )
}

@Composable
fun TextSubTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onPrimary,
    fontWeight: FontWeight = FontWeight.SemiBold,
) {
    Text(
        text = text,
        color = color,
        fontSize = FontType.LARGE_24,
        modifier = modifier,
        fontFamily = getSfProFamilyFont(fontWeight)
    )
}

@Composable
fun TextDescription1(
    text: String,
    textAlign: TextAlign? = null,
    color: Color = MaterialTheme.colors.secondary,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        text = text,
        color = color,
        fontSize = FontType.SMALL_18,
        modifier = modifier,
        textAlign = textAlign,
        fontFamily = getSfProFamilyFont(FontWeight.Normal),
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun TextDescription2(
    text: String,
    textAlign: TextAlign? = null,
    color: Color = MaterialTheme.colors.secondary,
    fontWeight: FontWeight = FontWeight.Normal,
    fontStyle: FontStyle = FontStyle.Normal,
    fontSize: TextUnit = FontType.SMALL,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    lineHeight: TextUnit = TextUnit.Unspecified
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        modifier = modifier,
        textAlign = textAlign,
        fontStyle = fontStyle,
        fontFamily = getSfProFamilyFont(fontWeight, fontStyle),
        maxLines = maxLines,
        overflow = overflow,
        lineHeight = lineHeight
    )
}

@Composable
fun TextIconAvatar(
    text: String,
    textAlign: TextAlign? = null,
    color: Color = MaterialTheme.colors.secondary,
    fontWeight: FontWeight = FontWeight.Normal,
    fontStyle: FontStyle = FontStyle.Normal,
    fontSize: TextUnit = FontType.SMALL,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    style: TextStyle = LocalTextStyle.current
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        modifier = modifier,
        textAlign = textAlign,
        fontStyle = fontStyle,
        fontFamily = getSfProFamilyFont(fontWeight, fontStyle),
        maxLines = maxLines,
        overflow = overflow,
        style = style
    )
}

@Composable
fun TextDescription2(
    text: AnnotatedString,
    textAlign: TextAlign? = null,
    color: Color = MaterialTheme.colors.secondary,
    fontWeight: FontWeight = FontWeight.Normal,
    fontStyle: FontStyle = FontStyle.Normal,
    fontSize: TextUnit = FontType.SMALL,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        modifier = modifier,
        textAlign = textAlign,
        fontStyle = fontStyle,
        fontFamily = getSfProFamilyFont(fontWeight, fontStyle),
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
fun TextDescription2WithInfoCircle(text: String, color: Color = Colors.main1Text){
    Row(verticalAlignment = Alignment.CenterVertically) {
        TextDescription2(
            text,
            color = color
        )

        Spacer(modifier = Modifier.width(Spacing.XTINY))

        Icon(
            imageVector = MangalaWalletPack.InfoCircle,
            contentDescription = null,
            tint = Colors.stroke,
        )
    }
}

@Composable
fun TextTiny(
    text: String,
    color: Color = MaterialTheme.colors.secondary,
    textAlign: TextAlign? = null,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text(
        text = text,
        color = color,
        fontWeight = fontWeight,
        fontSize = FontType.TINY,
        modifier = modifier,
        textAlign = textAlign,
        fontFamily = fontFamilyResource(MR.fonts.sfpro)
    )
}

@Composable
fun TextTiny(
    text: AnnotatedString,
    color: Color = MaterialTheme.colors.secondary,
    textAlign: TextAlign? = null,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text(
        text = text,
        color = color,
        fontWeight = fontWeight,
        fontSize = FontType.TINY,
        modifier = modifier,
        textAlign = textAlign,
        fontFamily = fontFamilyResource(MR.fonts.sfpro)
    )
}

@Composable
fun TextMicro(text: String, textAlign: TextAlign? = null, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colors.secondary,
        fontWeight = FontWeight.Normal,
        fontSize = FontType.MICRO,
        modifier = modifier,
        textAlign = textAlign,
        fontFamily = fontFamilyResource(MR.fonts.sfpro)
    )
}

@Composable
fun TextNormal(
    text: String,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = MaterialTheme.colors.secondary,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = FontType.REGULAR,
    style: TextStyle = LocalTextStyle.current
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        modifier = modifier,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines,
        fontFamily = getSfProFamilyFont(fontWeight),
        style = style
    )
}

@Composable
fun TextNormal(
    text: AnnotatedString,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = MaterialTheme.colors.secondary,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = FontType.REGULAR,
    style: TextStyle = LocalTextStyle.current
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        modifier = modifier,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines,
        fontFamily = getSfProFamilyFont(fontWeight),
        style = style
    )
}

@Composable
fun TextNormal2(
    text: String,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = MaterialTheme.colors.secondary,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = color,
        fontSize = FontType.REGULAR_22,
        modifier = modifier,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines,
        fontFamily = getSfProFamilyFont(fontWeight)
    )
}

@Composable
fun TextSwitch(
    text: String,
    textAlign: TextAlign? = null,
    color: Color = MaterialTheme.colors.secondary,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = color,
        fontSize = FontType.SMALL,
        modifier = modifier,
        textAlign = textAlign,
        fontFamily = getSfProFamilyFont(FontWeight.Normal)
    )
}

@Composable
fun TextUnderLine(text: AnnotatedString, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colors.secondary,
        fontSize = FontType.SMALL,
        modifier = modifier,
        fontFamily = getSfProFamilyFont(FontWeight.Normal)
    )
}

@Composable
fun TextError(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colors.error,
        fontSize = FontType.SMALL,
        modifier = modifier,
        fontFamily = getSfProFamilyFont(FontWeight.Normal)
    )
}

@Composable
fun TextCanClick(
    text: String,
    textAlign: TextAlign? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = FontType.LARGE,
) {
    Text(
        text = text,
        color = if (enabled) {
            MaterialTheme.colors.onPrimary
        } else {
            MaterialTheme.colors.secondary
        },
        fontSize = fontSize,
        modifier = modifier,
        textAlign = textAlign,
        fontFamily = getSfProFamilyFont(if (enabled) {
            FontWeight.Bold
        } else {
            FontWeight.Normal
        })
    )
}

@Composable
fun TextInButton(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colors.onPrimary,
        fontSize = FontType.TITLE_3,
        modifier = modifier,
        fontFamily = getSfProFamilyFont(FontWeight.Bold)
    )
}

@Composable
fun TextInButtonTiny(text: String, fontWeight: FontWeight = FontWeight.Bold, modifier: Modifier = Modifier, lineHeight: TextUnit = TextUnit.Unspecified) {
    Text(
        text = text,
        color = MaterialTheme.colors.onPrimary,
        fontSize = FontType.TINY,
        lineHeight = lineHeight,
        modifier = modifier,
        fontFamily = getSfProFamilyFont(fontWeight)
    )
}

@Composable
fun TextInButtonSmall(text: String, fontWeight: FontWeight = FontWeight.Bold, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colors.onPrimary,
        fontSize = FontType.TINY,
        modifier = modifier,
        fontFamily = getSfProFamilyFont(fontWeight)
    )
}


//BUTTON
@Composable
fun ButtonNormal(
    text: String,
    icon: ImageVector? = null,
    fontSize: TextUnit = FontType.LARGE,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    buttonModifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.onPrimary,
    shape: Shape = RoundedCornerShape(8.dp),
    elevation: ButtonElevation? = null,
    textColor: Color = MaterialTheme.colors.primary,
    disabledBackgroundColor: Color = Colors.neutralGray,
    buttonMinSizeDefault: Dp = 56.dp,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            disabledBackgroundColor = disabledBackgroundColor
        ),
        shape = shape,
        enabled = enabled,
        modifier = buttonModifier.defaultMinSize(minHeight = buttonMinSizeDefault),
        elevation = elevation
    ) {
        icon?.let {
            Image(
                imageVector = icon,
                contentDescription = null,
                modifier = modifier
            )

            Spacer(modifier = Modifier.width(Spacing.XTINY))
        }

        Text(
            text = text,
            color = textColor,
            modifier = modifier,
            fontFamily = getSfProFamilyFont(FontWeight.SemiBold),
            fontSize = fontSize,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ButtonNormalIcon(
    icon: ImageVector,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    buttonModifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.onPrimary,
    shape: Shape = RoundedCornerShape(8.dp),
    elevation: ButtonElevation? = null,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            disabledBackgroundColor = MaterialTheme.colors.secondary
        ),
        shape = shape,
        enabled = enabled,
        modifier = buttonModifier,
        elevation = elevation
    ) {
        Image(
            imageVector = icon,
            contentDescription = null,
            modifier = modifier
        )
    }
}

@Composable
fun NumberButton(number: String, enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier
            .size(72.dp)
            .padding(2.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
        enabled = enabled
    ) {
        TextInButton(text = number)
    }
}

//TEXT FIELD
@Composable
fun TextFieldNormal(
    value: String,
    placeHolder: String,
    state: TextFieldState,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done
    ),
    keyboardActions: KeyboardActions,
    onValueChange: (String) -> Unit,
) {

    var textFieldState by remember { mutableStateOf(state) }
    val borderColor = when (textFieldState) {
        TextFieldState.Empty -> MaterialTheme.colors.secondary
        TextFieldState.Correct -> MaterialTheme.colors.secondary
        TextFieldState.Wrong -> MaterialTheme.colors.error

    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(Spacing.SMALL),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = borderColor,
            unfocusedIndicatorColor = borderColor,
            cursorColor = borderColor
        ),
        textStyle = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = FontType.SMALL,
            fontFamily = fontFamilyResource(MR.fonts.sfpro)
        ),
        placeholder = {
            TextDescription2(
                text = placeHolder,
            )
        },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        isError = textFieldState == TextFieldState.Wrong,
    )
}

@Composable
fun TextFieldSelect(items: List<String>, selectedItem: String, onItemSelected: (String) -> Unit) {
    TextFieldWithIcon(selectedItem, "", TextFieldState.Empty, false, onItemSelected,null
    ) {
        Row {
            IconButton(onClick = { onItemSelected }, modifier = Modifier.size(Dimensions.IconButtonSize)) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(Dimensions.IconSize))
            }
        }
    }
}

@Composable
fun TextFieldWithIcon(
    value: String,
    placeHolder: String,
    state: TextFieldState,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {

    var textFieldState by remember { mutableStateOf(state) }
    val borderColor = when (textFieldState) {
        TextFieldState.Empty -> MaterialTheme.colors.secondary
        TextFieldState.Correct -> MaterialTheme.colors.secondary
        TextFieldState.Wrong -> MaterialTheme.colors.error

    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(Spacing.SMALL),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = borderColor,
            unfocusedIndicatorColor = borderColor,
            cursorColor = borderColor,
            disabledLabelColor = borderColor
        ),
        textStyle = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = FontType.SMALL,
            fontFamily = fontFamilyResource(MR.fonts.sfpro)
        ),
        placeholder = {
            TextDescription2(
                text = placeHolder,
            )
        },
        leadingIcon = leadingIcon,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        isError = textFieldState == TextFieldState.Wrong,
        trailingIcon = trailingIcon,
        enabled = enabled
    )
}

@Composable
fun TextDialogButton(
    text: String,
    textAlign: TextAlign? = null,
    modifier: Modifier = Modifier,
    isPositiveAction: Boolean,
    fontSize: TextUnit = FontType.REGULAR,
) {
    Text(
        text = text,
        color = if(isPositiveAction) Colors.second else Colors.third,
        fontSize = fontSize,
        modifier = modifier,
        textAlign = textAlign,
        fontFamily = getSfProFamilyFont(FontWeight.SemiBold)
    )
}

@Composable
fun TransactionText(text: String, color: Color, fontSize: TextUnit, fontWeight: FontWeight = FontWeight.Normal) {
    Text(
        text = text,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        fontSize = fontSize,
        fontWeight = fontWeight
    )
}


enum class TextFieldState {
    Empty,
    Correct,
    Wrong
}
