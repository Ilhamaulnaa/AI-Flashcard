package com.ilham.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ilham.common.theme.FlashColors
import com.ilham.common.theme.FlashShape
import com.ilham.common.theme.FlashSpacing
import com.ilham.common.theme.FlashTypography
import com.ilham.common.theme.FlashcardTheme

@Composable
fun AuthTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String? = null,
    icon: ImageVector,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions()
) {

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderWidth = if(isFocused) 2.dp else 1.dp
    val borderColor = if(isFocused) FlashColors.Gray400 else FlashColors.Gray100

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = FlashTypography.labelLarge,
        )
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(borderWidth, borderColor), shape = FlashShape.medium)
                .clip(FlashShape.medium)
                .background(color = FlashColors.Gray100)
                .padding(FlashSpacing.md),
            value = value,
            onValueChange = onValueChange,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            interactionSource = interactionSource,
            decorationBox = { innerTextField ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = FlashColors.Gray400
                    )
                    Spacer(modifier = Modifier.width(FlashSpacing.xs))
                    Box {
                        if (value.isEmpty() && placeholder?.isEmpty() == false){
                            Text(
                                text = placeholder,
                                style = FlashTypography.bodySmall,
                                color = FlashColors.Gray400
                            )
                        }
                        innerTextField()
                    }
                }
            },
        )
    }
    
}

@Preview
@Composable
private fun AuthTextFieldPreview() {
    FlashcardTheme {
        AuthTextField(
            label = "email",
            value = "asd",
            onValueChange = {},
            placeholder = "masukan emial",
            icon = Icons.Outlined.Email,
            isPassword = false
        )
    }
    
}