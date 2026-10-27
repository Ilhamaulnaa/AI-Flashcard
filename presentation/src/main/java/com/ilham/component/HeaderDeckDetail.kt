package com.ilham.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.ilham.common.theme.FlashColors
import com.ilham.common.theme.FlashShape
import com.ilham.common.theme.FlashSpacing
import com.ilham.common.theme.FlashTypography
import com.ilham.common.theme.FlashcardTheme
import com.ilham.domain.model.Deck

@Composable
fun HeaderDeckDetail(
    modifier: Modifier = Modifier,
    deck: Deck,
    onStudyClick: () -> Unit
) {

    Card(
        onClick = {
            onStudyClick()
        },
        shape = FlashShape.medium,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(FlashSpacing.sm)
        ) {
            Surface(
                modifier = Modifier
                    .padding(horizontal = FlashSpacing.sm, vertical = FlashSpacing.xs)
                    .clip(FlashShape.medium)
                    .background(Color.Transparent)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${deck.cardCount} Kartu",
                        color = Color.White
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = deck.name,
                    color = Color.White,
                    style = FlashTypography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@Preview
@Composable
private fun HeaderDeckDetailPreview() {
    FlashcardTheme {
        val decks = Deck(
            id = "",
            name = "Matematika",
            emoji = "",
            description = "",
            color = Color.White,
            cards = emptyList(),
            cardCount = 2
        )
        HeaderDeckDetail(
            deck = decks,
            onStudyClick = {}
        )
    }
}