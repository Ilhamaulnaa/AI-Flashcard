package com.ilham.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ilham.common.theme.FlashColors
import com.ilham.common.theme.FlashTypography

@ExperimentalMaterial3Api
@Composable
fun FlashCardTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    action: @Composable (RowScope. () -> Unit) = {}
) {

    TopAppBar(
        title = {
            Text(text = title, color = FlashColors.Indigo500)
        },
        navigationIcon = {
            if (onBack != null){
                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null,
                        tint = FlashColors.Indigo500
                    )
                }
            }
        },
        actions = action
    )

}