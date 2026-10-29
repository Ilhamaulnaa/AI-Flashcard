package com.ilham.presentation.add_deck

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilham.common.utils.deckColors
import com.ilham.common.utils.deckEmojis
import com.ilham.domain.usecase.DeckUseCase
import com.ilham.domain.usecase.GenerateAIFlashCardUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel(assistedFactory = AddDeckViewModel.Factory::class)
class AddDeckViewModel @AssistedInject constructor(
    @Assisted private val deckId: String,
    private val useCaseDeck: DeckUseCase,
): ViewModel() {

//    private val deckId: String = savedStateHandle.get<String>("deckId") ?: ""
    @AssistedFactory
    interface Factory{
        fun create(deckId: String) : AddDeckViewModel
    }

    val editableDeckId = deckId.takeIf { it.isNotBlank() }

    private var hasLoadedDeck = false

    private val _uiState = MutableStateFlow(
        AddDeckUiState(
            isLoadingDeck = editableDeckId != null,
            deckId = editableDeckId,
            color = deckColors[0].name,
            emoji = deckEmojis[0].name
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _showSnackbar = Channel<String>()
    val showSnackbar = _showSnackbar.receiveAsFlow()

    init {
        editableDeckId?.let(::observeDeck)
    }

    fun onTitleChanged(value: String){
        _uiState.update { it.copy(title = value) }
    }

    fun onDecriptionChanged(value: String){
        _uiState.update { it.copy(description = value) }
    }

    fun onEmojiChanged(value: String){
        _uiState.update { it.copy(emoji = value) }
    }

    fun onColorChanged(value: String){
        _uiState.update { it.copy(color = value) }
    }

    fun addDeck() {
        Log.d("SaveCheck", "submit dipanggil")
        viewModelScope.launch {

            val state = _uiState.value
            if (state.title.isBlank()) {
                _showSnackbar.send("Nama deck tidak boleh kosong.")
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                Log.d("SaveCheck", "mulai disimpan")
                if (state.isEditMode && state.deckId != null) {
                    useCaseDeck.updateDeck(
                        deckId = state.deckId,
                        name = state.title,
                        description = state.description,
                        color = state.color,
                        emoji = state.emoji,
                    )
                } else {
                    useCaseDeck.createDeck(
                        name = state.title,
                        description = state.description,
                        color = state.color,
                        emoji = state.emoji,
                    )
                }
                Log.d("SaveCheck", "repo selesai")
            }.onFailure { throwable ->
                Log.d("SaveCheck", "submit failure", throwable)
                _uiState.update { it.copy(isLoading = false) }
                _showSnackbar.send(throwable.message ?: submitErrorMessage(state.isEditMode))
            }.onSuccess {
                Log.d("SaveCheck", "submit sukses")
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }
        }
    }

    fun deleteDeck(){
        viewModelScope.launch {

            val deckId = uiState.value.deckId
            if (deckId == null){
                _showSnackbar.send("deck belum bisa dihapus.")
                return@launch
            }

            _uiState.update { it.copy(isDeleting = true) }
            runCatching {
                useCaseDeck.deleteDeck(deckId)
            }.onFailure { throwable ->
                _uiState.update { it.copy(isDeleting = false) }
                _showSnackbar.send(throwable.message ?: "Gagal menghapus deck.")
            }.onSuccess {
                _uiState.update { it.copy(isDeleting = false, isSuccess = true) }
            }
        }
    }

    fun observeDeck(deckId: String){
        viewModelScope.launch {
            useCaseDeck.observerDeck(deckId)
                .catch { throwable ->
                    _uiState.update { it.copy(isLoadingDeck = false) }
                    _showSnackbar.send(throwable.message ?: "Gagal memuat decks")
                }
                .collect { deck ->
                    if (hasLoadedDeck) return@collect

                    if (deck == null){
                        _uiState.update { it.copy(isLoadingDeck = false, errorMessage = "Deck tidak ditemukan.") }
                        return@collect
                    }

                    hasLoadedDeck = true
                    _uiState.update {
                        it.copy(
                            isLoadingDeck = false,
                            title = deck.name,
                            description = deck.description,
                            emoji = deckEmojis.firstOrNull{ option -> option.emoji == deck.emoji }?.name
                                ?: deckEmojis[0].name,
                            color = deckColors.firstOrNull { option -> option.color == deck.color }?.name
                                ?: deckColors[0].name
                        )
                    }
                }
        }
    }

    fun submitErrorMessage(isEditMode: Boolean) : String =
        if (isEditMode) "Gagal menyimpan perubahan deck." else "Gagal membuat deck."

}