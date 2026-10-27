package com.ilham.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ilham.common.theme.FlashColors
import com.ilham.common.theme.FlashSpacing
import com.ilham.common.theme.FlashTypography
import com.ilham.common.ui.FlashButton

@Composable
fun MissingDeckScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FlashColors.Background)
            .padding(FlashSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Deck tidak ditemukan",
            style = FlashTypography.titleLarge,
            color = FlashColors.Gray900,
        )
        Text(
            text = "Kembali ke halaman sebelumnya.",
            style = FlashTypography.bodyMedium,
            color = FlashColors.Gray400,
        )
        FlashButton(text = "Kembali", onClick = onBack)
    }
}