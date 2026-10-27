package com.ilham.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ilham.common.R
import com.ilham.common.theme.FlashColors
import com.ilham.common.theme.FlashSpacing
import com.ilham.common.theme.FlashTypography
import com.ilham.common.theme.FlashcardTheme

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    title: String,
    description: String
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo_empty),
            contentDescription = "empty",
            modifier = modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.height(FlashSpacing.sm))
        Text(
            text = title,
            style = FlashTypography.displayLarge
        )
        Text(
            text = description,
            fontSize = 12.sp,
            color = FlashColors.Gray400,
            style = FlashTypography.bodyMedium
        )
    }

}

@Composable
private fun EmptyStatePreview() {
    FlashcardTheme {
        EmptyState(title = "empty list", description = "please create deck")
    }
}