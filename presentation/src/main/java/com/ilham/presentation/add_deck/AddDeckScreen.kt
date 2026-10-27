package com.ilham.presentation.add_deck

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ilham.common.theme.FlashColors
import com.ilham.common.theme.FlashShape
import com.ilham.common.theme.FlashSpacing
import com.ilham.common.theme.FlashTypography
import com.ilham.common.theme.FlashcardTheme
import com.ilham.common.ui.ConfirmationDialog
import com.ilham.common.ui.FlashButton
import com.ilham.common.ui.LoadingScreen
import com.ilham.common.utils.DeckColor
import com.ilham.common.utils.DeckIcon
import com.ilham.common.utils.deckColors
import com.ilham.common.utils.deckEmojis
import com.ilham.component.AuthTextField
import com.ilham.component.FlashCardTopBar
import com.ilham.component.FlashTextField
import com.ilham.component.HeaderDeckDetail
import com.ilham.domain.model.Deck
import kotlin.time.Duration.Companion.nanoseconds

@ExperimentalMaterial3Api
@Composable
fun AddDeckScreen(
    deckId: String,
    modifier: Modifier = Modifier,
    viewModel: AddDeckViewModel = hiltViewModel<AddDeckViewModel, AddDeckViewModel.Factory>(key = deckId){
        factory -> factory.create(deckId)
    },
    navigateUp: () -> Unit,
) = with(viewModel){

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        showSnackbar.collect { message ->
            snackBarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navigateUp()
        }
    }

    AddDeckContentScreen(
        modifier = modifier,
        navigateUp = navigateUp,
        uiState = uiState,
        snackbarHostState = snackBarHostState,
        onTitleChanged = viewModel::onTitleChanged,
        onDescriptionChanged = viewModel::onDecriptionChanged,
        onIconSelected = viewModel::onEmojiChanged,
        onColorSelected = viewModel::onColorChanged,
        onAddDeck = viewModel::addDeck,
        onDeleteDeck = viewModel::deleteDeck
    )

}

@ExperimentalMaterial3Api
@Composable
fun AddDeckContentScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    uiState: AddDeckUiState = AddDeckUiState(),
    navigateUp: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onIconSelected: (String) -> Unit,
    onColorSelected: (String) -> Unit,
    onAddDeck: () -> Unit,
    onDeleteDeck: () -> Unit
) {

    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            FlashCardTopBar(
                title = if (uiState.isEditMode) "Edit deck" else "Add your deck",
                onBack = navigateUp
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddingValues ->

        if (uiState.isLoadingDeck){
            LoadingScreen()
            return@Scaffold
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = FlashSpacing.md)
                .padding(paddingValues)
        ) {
            Text(
                text = "Nama Deck",
                style = FlashTypography.labelMedium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(FlashSpacing.xs))
            FlashTextField(
                value = uiState.title,
                onValueChange = onTitleChanged,
                placeholder = "Contoh: Biologi Dasar"
            )
            Spacer(modifier = Modifier.height(FlashSpacing.sm))
            Text(
                text = "Deskripsi(optional)",
                style = FlashTypography.labelMedium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(FlashSpacing.xs))
            FlashTextField(
                value = uiState.description ?: "",
                onValueChange = onDescriptionChanged,
                placeholder = "Apa yang akan kamu pelajari hari ini ?" ,
            )
            Spacer(modifier = Modifier.height(FlashSpacing.sm))
            Text(
                text = "Pilih icon deck",
                style = FlashTypography.labelMedium,
                color = Color.Black
            )
            DeckIconComposer(
                selectedIconName = uiState.emoji.ifBlank { deckEmojis.first().name },
                onIconSelected = onIconSelected
            )
            Spacer(modifier = Modifier.height(FlashSpacing.sm))
            Text(
                text = "Warna tema deck",
                style = FlashTypography.labelMedium,
                color = Color.Black
            )
            DeckColorComposer(
                selectedColorName = uiState.color.ifBlank { deckColors.first().name },
                onColorSelected = onColorSelected
            )
            Spacer(modifier = Modifier.height(FlashSpacing.md))
            FlashButton(
                text = "Buat deck sekarang",
                onClick = onAddDeck,
                isLoading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            if (uiState.isEditMode){
                Spacer(Modifier.height(12.dp))
                FlashButton(
                    text = "Hapus Deck",
                    onClick = { showDeleteConfirmation = true },
                    isLoading = uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
    if (showDeleteConfirmation){
        ConfirmationDialog(
            onDismiss = { showDeleteConfirmation = false },
            onConfirm = {
                showDeleteConfirmation = false
                onDeleteDeck()
            },
            title = "anda yakin ingin menghapus deck ?",
            message = "Deck kartu yang ada didalamnya akan ikut terhapus."
        )
    }
    
}

@Composable
fun DeckIconComposer(
    modifier: Modifier = Modifier,
    selectedIconName: String,
    onIconSelected: (String) -> Unit
) {

    LazyRow(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        items(deckEmojis, key = { it.name }) { icon ->
            val isSelected = icon.name == selectedIconName
            Surface(
                modifier = modifier.clickable { onIconSelected(icon.name) },
                shape = FlashShape.small,
                color = if (isSelected) FlashColors.Indigo50 else FlashColors.Surface,
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) FlashColors.DeckBlue else Color.Transparent
                )
            ) {
                Box(contentAlignment = Alignment.Center){
                    Text(
                        text = icon.emoji,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)
                    )
                }
            }
        }
    }

}

@Composable
fun DeckColorComposer(
    modifier: Modifier = Modifier,
    selectedColorName: String,
    onColorSelected: (String) -> Unit
) {

    LazyRow(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        items(deckColors, key = {it.name}){ deckColor ->
            val isColorSelected = deckColor.name == selectedColorName
            Surface(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color = deckColor.color)
                    .clickable { onColorSelected(deckColor.name) },
                shape = CircleShape,
                border = BorderStroke(
                    width = if (isColorSelected) 2.dp else 1.dp,
                    color = if (isColorSelected) deckColor.color else FlashColors.Surface
                )
            ) {
                if (isColorSelected) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(26.dp))
                            .background(Color.White)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(deckColor.color)
                    )
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        color = Color.Transparent,
                        border = BorderStroke(3.dp, deckColor.color),
                    ) {}
                } else {
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = RoundedCornerShape(22.dp),
                        color = deckColor.color,
                        border = BorderStroke(2.dp, Color.White),
                        shadowElevation = 2.dp,
                    ) {}
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun AddDeckContentScreenPreview() {
    FlashcardTheme {
        AddDeckContentScreen(
            navigateUp = {} ,
            onTitleChanged = {},
            onDescriptionChanged = {},
            onIconSelected = {},
            onColorSelected = {},
            onAddDeck = {}
        ) { }
    }
}

@Preview
@Composable
private fun DeckIconComposerPreview() {
    FlashcardTheme {
        DeckIconComposer(
            selectedIconName = "laptop",
            onIconSelected = {}
        )
    }
}

@Preview
@Composable
private fun DeckColorComposerPreview() {
    FlashcardTheme {
        DeckColorComposer(
            selectedColorName = "orange",
            onColorSelected = {}
        )
    }
}