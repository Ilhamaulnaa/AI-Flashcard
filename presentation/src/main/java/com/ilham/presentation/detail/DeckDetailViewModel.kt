package com.ilham.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilham.domain.model.FlashCard
import com.ilham.domain.usecase.DeckUseCase
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

@HiltViewModel(assistedFactory = DeckDetailViewModel.Factory::class)
class DeckDetailViewModel @AssistedInject constructor(
    @Assisted private val deckId: String,
    private val useCaseDeck: DeckUseCase,
): ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(deckId: String): DeckDetailViewModel
    }

    private val _uiState = MutableStateFlow(DeckDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _showSnackBar = Channel<String>()
    val showSnackBar = _showSnackBar.receiveAsFlow()

    init {
        viewModelScope.launch {
            useCaseDeck.observerDeck(deckId)
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Gagal memuat deck."
                        )
                    }
                }
                .collect { deck ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            deck = deck,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun addFlashCard(question: String, answer: String){
        if (question.isBlank() || answer.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                useCaseDeck.addFlashCard(
                    deckId = deckId ,
                    question = question,
                    answer = answer
                )
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Gagal menambahkan kartu."
                    )
                }
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false
                    )
                }

            }
        }
    }

    fun deleteFlashCard(cardId: String){
        viewModelScope.launch {
            runCatching {
                useCaseDeck.deleteFlashCard(deckId = deckId, cardId = cardId)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Gagal menghapus kartu."
                    )
                }
            }
        }
    }

    fun updateFlashCard(card: FlashCard){
        viewModelScope.launch {
            runCatching {
                useCaseDeck.updateFlashCard(deckId = deckId, card = card)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Gagal memperbarui kartu."
                    )
                }
            }
        }
    }


}