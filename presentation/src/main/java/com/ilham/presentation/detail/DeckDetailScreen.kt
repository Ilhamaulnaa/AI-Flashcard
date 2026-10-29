package com.ilham.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ilham.common.theme.FlashColors
import com.ilham.common.theme.FlashShape
import com.ilham.common.theme.FlashSpacing
import com.ilham.common.theme.FlashTypography
import com.ilham.common.theme.flashShadow
import com.ilham.common.ui.FlashButton
import com.ilham.common.utils.rememberSnackBarHostState
import com.ilham.component.AIGenerateButton
import com.ilham.component.CardItem
import com.ilham.component.EmptyState
import com.ilham.component.FlashCardTopBar
import com.ilham.component.HeaderDeckDetail
import com.ilham.component.MissingDeckScreen
import com.ilham.domain.model.Deck
import com.ilham.domain.model.FlashCard

@ExperimentalMaterial3Api
@Composable
fun DeckDetailScreen(
    deckId: String,
    modifier: Modifier = Modifier,
    viewModel: DeckDetailViewModel =
        hiltViewModel<DeckDetailViewModel, DeckDetailViewModel.Factory>(
            key = deckId
        ) { factory -> factory.create(deckId) },
    navigateUp: () -> Unit,
    onAddFlashCardClick: () -> Unit,
    onStudyClick: () -> Unit,
    onGenerateClick: () -> Unit,
    onEditFlashCardClick: (String) -> Unit,
    onEditDeckClick: (String) -> Unit,
) = with(viewModel){

    val uiState by uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        showSnackBar.collect { message ->
            snackBarHostState.showSnackbar(message)
        }
    }

    DeckDetailScreenContent(
        modifier = modifier,
        uiState = uiState,
        navigateUp = navigateUp,
        onStudyClick = onStudyClick,
        onGenerateClick = onGenerateClick,
        onAddFlashCardClick = onAddFlashCardClick,
        onEditFlashCardClick = onEditFlashCardClick,
        onEditDeckClick = onEditDeckClick,
        snackbarHostState = snackBarHostState
    )

}

@ExperimentalMaterial3Api
@Composable
fun DeckDetailScreenContent(
    modifier: Modifier = Modifier,
    uiState: DeckDetailUiState = DeckDetailUiState(),
    navigateUp: () -> Unit = {},
    onStudyClick: () -> Unit = {},
    onGenerateClick: () -> Unit = {},
    onAddFlashCardClick: () -> Unit = {},
    onEditFlashCardClick: (String) -> Unit = {},
    onEditDeckClick: (String) -> Unit = {},
    snackbarHostState: SnackbarHostState = rememberSnackBarHostState()
) {

    if (uiState.deck == null) {
        MissingDeckScreen(onBack = navigateUp, modifier = modifier)
        return
    }

    Scaffold(
        topBar = {
            FlashCardTopBar(
                title = "Detail Deck",
                onBack = navigateUp,
                action = {
                    IconButton(
                        onClick = { onEditDeckClick(uiState.deck.id) }
                    ) {
                        Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            FlashButton(
                text = "+ Tambah",
                onClick = onAddFlashCardClick,
                color = uiState.deck.color
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FlashColors.Surface)
                    .navigationBarsPadding()
                    .padding(FlashSpacing.md),
                horizontalArrangement = Arrangement.spacedBy(FlashSpacing.sm),
            ) {
                AIGenerateButton(
                    onClick = onGenerateClick,
                    modifier = Modifier.weight(1f),
                )
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddingValues ->
        LazyColumn (
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = FlashSpacing.md, vertical = FlashSpacing.lg)
                .padding(paddingValues)
        ) {
            item {
                DeckHeader(
                    deck = uiState.deck,
                    onStudyClick = onStudyClick
                )
            }
            item { Spacer(Modifier.height(FlashSpacing.md)) }
            item {
                Text("Daftar Kartu", style = FlashTypography.titleMedium)
            }
            item {
                if (uiState.deck.cards.isEmpty()) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .fillParentMaxHeight(0.5f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EmptyState(text = "Flashcard masih kosong.")
                    }
                }
            }
            items(uiState.deck.cards, key = { it.id }) { card ->
                CardItem(
                    card = card,
                    deckColor = uiState.deck.color,
                    onEdit = { onEditFlashCardClick(card.id) },
                )
            }
        }
    }

}

@Composable
private fun DeckHeader(
    deck: Deck,
    onStudyClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .flashShadow(
                color = deck.color.copy(alpha = 0.25f),
                borderRadius = 24.dp,
                blurRadius = 32.dp,
            )
            .clip(FlashShape.large)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        deck.color,
                        deck.color.copy(alpha = 0.78f)
                    )
                )
            )
            .padding(FlashSpacing.md),
    ) {
        Column {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(FlashShape.full)
                    .background(color = Color.White.copy(alpha = 0.3f))
            ) {
                Text(
                    text = "${deck.cardCount} kartu",
                    style = FlashTypography.bodyMedium,
                    color = Color.White.copy(alpha = 0.78f),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = deck.name,
                    style = FlashTypography.titleLarge,
                    color = Color.White,
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onStudyClick) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Mulai belajar",
                        tint = Color.White,
                    )
                }
            }
            Spacer(modifier = Modifier.height(FlashSpacing.sm))
            deck.description?.let { description ->
                Text(
                    text = description,
                    style = FlashTypography.bodyMedium,
                    color = Color.White,
                )
            }
        }
    }
}
