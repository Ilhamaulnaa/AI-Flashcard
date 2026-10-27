package com.ilham.component

import android.graphics.Color
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ilham.common.theme.FlashColors
import com.ilham.common.theme.FlashShape
import com.ilham.common.theme.FlashSpacing
import com.ilham.common.theme.FlashTypography
import com.ilham.domain.model.FlashCard

@Composable
fun CardItem(
    card: FlashCard,
    deckColor: androidx.compose.ui.graphics.Color,
    onEdit: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = FlashShape.medium,
        colors = CardDefaults.cardColors(containerColor = FlashColors.Surface),
        border = BorderStroke(1.dp, FlashColors.Gray100),
    ) {
        Row(modifier = Modifier.padding(FlashSpacing.md)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .clip(FlashShape.full)
                    .background(deckColor),
            )
            Spacer(modifier = Modifier.width(FlashSpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Q: ${card.question}",
                    style = FlashTypography.bodyLarge,
                    color = FlashColors.Gray900,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(FlashSpacing.xs))
                Text(
                    text = "A: ${card.answer}",
                    style = FlashTypography.bodyMedium,
                    color = FlashColors.Gray400,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = FlashColors.Gray400,
                )
            }
        }
    }
}