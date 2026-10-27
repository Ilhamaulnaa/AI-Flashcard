package com.ilham.presentation.add_flashcard

import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ilham.common.theme.FlashColors
import com.ilham.common.theme.FlashShape
import com.ilham.common.theme.FlashSpacing
import com.ilham.common.theme.FlashTypography
import com.ilham.common.ui.ConfirmationDialog
import com.ilham.common.ui.FlashButton
import com.ilham.common.ui.LoadingScreen
import com.ilham.common.utils.rememberSnackBarHostState
import com.ilham.component.FlashCardTopBar
import com.ilham.component.FlashTextField

@Composable
fun AddFlashCardScreen(
    deckId: String,
    cardId: String,
    modifier: Modifier = Modifier,
    viewModel: AddFlashCardViewModel =
        hiltViewModel<AddFlashCardViewModel, AddFlashCardViewModel.Factory>(
            key = "$deckId-$cardId"
        ) { factory -> factory.create(deckId, cardId) },
    navigateUp: () -> Unit
) = with(viewModel) {

    val uiState by uiState.collectAsStateWithLifecycle()
    val snackBarHostState = rememberSnackBarHostState()

    LaunchedEffect(Unit) {
        showSnackBar.collect { message ->
            snackBarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess){
            navigateUp()
        }
    }

    AddFlashCardScreenContent(
        modifier = modifier,
        uiState = uiState,
        onBack = navigateUp,
        onSave = ::submit,
        onDelete = ::deleteFlashCard,
        onQuestionChange = ::onQuestionChanged,
        onAnswerChange = ::onAnswerChanged,
        snackbarHostState = snackBarHostState
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlashCardScreenContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
    onDelete: () -> Unit = {},
    onQuestionChange: (String) -> Unit = {},
    onAnswerChange: (String) -> Unit = {},
    snackbarHostState: SnackbarHostState = rememberSnackBarHostState(),
    uiState: AddFlashCardUiState = AddFlashCardUiState()
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        modifier,
        topBar = {
            FlashCardTopBar(
                title = if (uiState.isEditMode) "Edit Flashcard" else "Tambah Flashcard",
                onBack = onBack
            )
        }, snackbarHost = {
            SnackbarHost(snackbarHostState)
        }) { padding ->
        if (uiState.isLoadingCard) {
            LoadingScreen()
            return@Scaffold
        }

        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(FlashSpacing.md)
        ) {
            item {
                Surface(
                    Modifier.fillMaxWidth(),
                    color = FlashColors.Cyan200,
                    shape = FlashShape.medium
                ) {
                    Row(Modifier.padding(16.dp)) {
                        Icon(Icons.Default.AutoGraph, null, tint = FlashColors.Teal200)
                        Spacer(Modifier.width(FlashSpacing.md))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Siap membuat kartu baru?",
                                color = FlashColors.Teal200,
                                style = FlashTypography.bodyLarge,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Buat pertanyaan yang menantang untuk memperkuat ingatanmu.",
                                style = FlashTypography.bodySmall
                            )
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(FlashSpacing.lg)) }
            item {
                Surface(
                    border = BorderStroke(color = FlashColors.Gray200, width = 1.dp),
                    shape = FlashShape.medium
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Pertanyaan", style = FlashTypography.bodyLarge)
                        Spacer(Modifier.height(FlashSpacing.md))
                        FlashTextField(
                            modifier = Modifier,
                            value = uiState.question,
                            onValueChange = onQuestionChange,
                            placeholder = "Apa ibukota indonesia?",
                            minLines = 3
                        )
                        Spacer(Modifier.height(FlashSpacing.lg))
                        Text("Jawaban", style = FlashTypography.bodyLarge)
                        Spacer(Modifier.height(FlashSpacing.md))
                        FlashTextField(
                            modifier = Modifier,
                            value = uiState.answer,
                            onValueChange = onAnswerChange,
                            placeholder = "Ibukota Indonesia adalah Jakarta (saat ini) dan sedang dalam proses transisi ke Nusantara",
                            minLines = 4
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(FlashSpacing.lg)) }
            item {
                FlashButton(
                    text = if (uiState.isEditMode) "Simpan Perubahan" else "Simpan Kartu",
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = uiState.isLoading
                )
            }
            if (uiState.isEditMode) {
                item { Spacer(Modifier.height(FlashSpacing.md)) }
                item {
                    FlashButton(
                        text = "Hapus Flashcard",
                        onClick = { showDeleteConfirmation = true },
                        modifier = Modifier.fillMaxWidth(),
                        color = FlashColors.DeckPink,
                        isLoading = uiState.isDeleting,
                    )
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        ConfirmationDialog(
            title = "Hapus flashcard?",
            message = "Flashcard ini akan dihapus dari deck.",
            onDismiss = { showDeleteConfirmation = false },
            onConfirm = {
                showDeleteConfirmation = false
                onDelete()
            },
        )
    }
}