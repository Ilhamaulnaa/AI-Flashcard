package com.ilham.presentation.aigenerate

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

@HiltViewModel(assistedFactory = GenerateAIViewModel.Factory::class)
class GenerateAIViewModel @AssistedInject constructor(
//    savedStateHandle: SavedStateHandle,
    @Assisted private val deckId: String,
    private val useCaseGenerateAI: GenerateAIFlashCardUseCase
): ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(deckId: String): GenerateAIViewModel
    }

//    private val deckId: String = savedStateHandle.get<String>("deckId") ?: ""

    private val _uiState = MutableStateFlow(GenerateAIUiState())
    val uiState = _uiState.asStateFlow()

    private val _showSnackbar = Channel<String>()
    val showSnackbar = _showSnackbar.receiveAsFlow()

    private val _saveSuccess = Channel<String>()
    val saveSuccess = _saveSuccess.receiveAsFlow()

    init {
        viewModelScope.launch {
            useCaseGenerateAI.observerDeck(deckId)
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

    fun onTopicChanged(topic: String){
        _uiState.update {
            it.copy(topic = topic)
        }
    }

    fun onCardCountChanged(value: Int){
        _uiState.update {
            it.copy(cardCount = value)
        }
    }

    fun generateFlashCard(){
        viewModelScope.launch {

            val state = _uiState.value
            val deck = state.deck

            if (deck == null){
                _showSnackbar.send("Deck tidak ditemukan.")
                return@launch
            }

            val topic = state.topic.ifBlank { state.topic }
            if (topic.isBlank()){
                _showSnackbar.send("Topic tidak boleh kosong.")
                return@launch
            }

            _uiState.update {
                it.copy(isGenerating = true, generatedCards = emptyList(), errorMessage = null)
            }

            runCatching { 
                useCaseGenerateAI.generateCards(
                    deckName = deck.name,
                    topic = topic,
                    cardCount = state.cardCount,
                )
            }.onSuccess { cards ->
                _uiState.update {
                    it.copy(isLoading = false, generatedCards = cards, isGenerating = false)
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isGenerating = false,
                        errorMessage = throwable.message ?: "Gagal generate flashcard."
                    )
                }
                _showSnackbar.send(throwable.message ?: "Gagal generate flashcard.")
            }
        }

    }

    fun saveGenerateCards(){
        viewModelScope.launch {

            val cards = _uiState.value.generatedCards
            if (cards.isEmpty()){
                _showSnackbar.send("card tidak ditemukan")
                return@launch
            }

            _uiState.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null
                )
            }

            runCatching {
                useCaseGenerateAI.saveCards(
                    deckId = deckId,
                    cards = cards,
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(isSaving = false, isSaveSuccess = true)
                    _saveSuccess.send("${cards.size} kartu ditambahkan ke deck")
                    return@launch
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = throwable.message ?: "Gagal menyimpan flashcard."
                    )
                }
                _showSnackbar.send("Gagal menyimpan flashcard.")
            }
        }
    }

}