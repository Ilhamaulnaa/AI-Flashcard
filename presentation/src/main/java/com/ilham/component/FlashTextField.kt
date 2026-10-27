package com.ilham.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ilham.common.theme.FlashColors
import com.ilham.common.theme.FlashShape
import com.ilham.common.theme.FlashSpacing
import com.ilham.common.theme.FlashTypography

@Composable
fun FlashTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1
) {

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(FlashShape.medium)
            .border(BorderStroke(width = 1.dp, color = FlashColors.Gray200), shape = FlashShape.medium)
            .background(FlashColors.Gray100)
            .padding(FlashSpacing.md),
        textStyle = FlashTypography.bodyLarge.copy(color = FlashColors.Gray900),
        maxLines = minLines,
        decorationBox = { innerTextField ->
            Box {
                if(value.isEmpty()){
                    Text(
                        text = placeholder,
                        style = FlashTypography.bodyMedium,
                        color = FlashColors.Gray400
                    )
                }
            }
            innerTextField()
        }
    )

}