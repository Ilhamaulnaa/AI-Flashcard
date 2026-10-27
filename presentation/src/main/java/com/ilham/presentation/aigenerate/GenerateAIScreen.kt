package com.ilham.presentation.aigenerate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ilham.common.theme.FlashColors
import com.ilham.common.theme.FlashShape
import com.ilham.common.theme.FlashSpacing
import com.ilham.common.theme.FlashTypography
import com.ilham.common.ui.FlashButton
import com.ilham.common.utils.rememberSnackBarHostState
import com.ilham.component.AIGenerateButton
import com.ilham.component.CardItem
import com.ilham.component.FlashCardTopBar
import com.ilham.component.FlashTextField
import com.ilham.component.MissingDeckScreen

@Composable
fun GenerateAIScreen(
    deckId: String,
    viewModel: GenerateAIViewModel = hiltViewModel<GenerateAIViewModel, GenerateAIViewModel.Factory>(
        key = deckId
    ){ factory -> factory.create(deckId) },
    navigateUp: () -> Unit
) = with(viewModel){

    val uiState by uiState.collectAsStateWithLifecycle()
    val snackbarHostState = rememberSnackBarHostState()

    GenerateAIContent(
        uiState = uiState,
        navigateUp = navigateUp,
        onEditClick = {},
        onGenerateChanged = ::onTopicChanged,
        onCardCountChanged = ::onCardCountChanged,
        onGenerate = ::generateFlashCard,
        onSaveAll = ::saveGenerateCards,
        snackbarHostState = snackbarHostState
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateAIContent(
    modifier: Modifier = Modifier,
    uiState: GenerateAIUiState = GenerateAIUiState(),
    navigateUp: () -> Unit,
    onEditClick: () -> Unit,
    onGenerateChanged: (String) -> Unit,
    onCardCountChanged: (Int) -> Unit,
    onGenerate: () -> Unit = {},
    onSaveAll: () -> Unit = {},
    snackbarHostState: SnackbarHostState = rememberSnackBarHostState()
){

    val deck = uiState.deck
    if (!uiState.isLoading && deck == null){
        MissingDeckScreen(navigateUp, modifier = modifier)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FlashColors.Background,
        topBar = {
            FlashCardTopBar(
                title = "Generate dengan AI",
                onBack = navigateUp,
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }

    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = FlashColors.Background)
                .padding(paddingValues),
            contentPadding = PaddingValues(FlashSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(FlashSpacing.md)
        ) {
            if (uiState.isLoading){
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = "Memuat deck...",
                            style = FlashTypography.bodyLarge,
                            color = FlashColors.Gray400
                        )
                    }
                }
                return@LazyColumn
            }
            item {
                Column(modifier = Modifier) {
                    Text(
                        text = deck?.name ?: "--",
                        style = FlashTypography.titleLarge,
                        color = FlashColors.Gray900,
                    )
                    Spacer(modifier = Modifier.height(FlashSpacing.xs))
                    Text(
                        text = "Masukkan topik, lalu AI akan membuat flashcard untuk deck ini.",
                        style = FlashTypography.bodyMedium,
                        color = FlashColors.Gray400,
                    )
                }
            }
            item { 
                FlashTextField(
                    value = uiState.topic,
                    onValueChange = onGenerateChanged,
                    placeholder = "example: Pemrograman Android untuk Pemula",
                    minLines = 3
                )
            }
            item {
                Column{
                    Text(text = "Jumlah Kartu:", style = FlashTypography.labelMedium, color = FlashColors.Gray900)
                    Row(horizontalArrangement = Arrangement.spacedBy(FlashSpacing.sm)) {
                        listOf(5, 10).forEach { count ->
                            FilterChip(
                                selected = uiState.cardCount == count,
                                onClick = { onCardCountChanged(count) },
                                label = { Text(text = count.toString(), style = FlashTypography.labelLarge) },
                                shape = FlashShape.medium,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = FlashColors.Indigo100,
                                    selectedLabelColor = FlashColors.Indigo700
                                )
                            )
                        }
                    }
                }
            }
            item {
                AIGenerateButton(
                    onClick = onGenerate,
                    isLoading = uiState.isGenerating,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if(uiState.generatedCards.isNotEmpty()){
                item {
                    Text(
                        text = "Hasil (${uiState.generatedCards.size} Kartu)",
                        style = FlashTypography.titleMedium,
                        color = FlashColors.Gray900
                    )
                }
                items(uiState.generatedCards, key = {it.id}){ card ->
                    CardItem(
                        card = card,
                        deckColor = deck?.color ?: FlashColors.Indigo500,
                        onEdit = { onEditClick() }
                    )
                }
                item { 
                    FlashButton(
                        text = "Simpan semua ke deck",
                        onClick = onSaveAll,
                        modifier = Modifier.fillMaxWidth(),
                        color = deck?.color ?: FlashColors.Indigo500,
                        isLoading = uiState.isSaving
                    )
                }
            }
        }
    }

}