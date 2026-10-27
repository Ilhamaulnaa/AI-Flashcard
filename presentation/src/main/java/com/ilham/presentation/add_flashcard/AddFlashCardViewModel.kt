package com.ilham.presentation.add_flashcard

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

@HiltViewModel(assistedFactory = AddFlashCardViewModel.Factory::class)
class AddFlashCardViewModel @AssistedInject constructor(
    @Assisted("deckId") private val deckId: String,
    @Assisted("cardId") cardId: String,
    private val useCaseDeck: DeckUseCase
): ViewModel() {

    @AssistedFactory
    interface Factory{
        fun create(
            @Assisted("deckId") deckId: String,
            @Assisted("cardId") cardId: String,
        ) : AddFlashCardViewModel
    }

    private val editableCardId = cardId.takeIf { it.isNotBlank() }
    private var hasLoadedCard = false

    private val _uiState = MutableStateFlow(
        AddFlashCardUiState(
            isLoadingCard = editableCardId != null,
            cardId = editableCardId
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _showSnackBar = Channel<String>()
    val showSnackBar = _showSnackBar.receiveAsFlow()

    init {
        editableCardId?.let(::observeCard)
    }

    fun onQuestionChanged(value: String){
        _uiState.update { it.copy(question = value) }
    }

    fun onAnswerChanged(value: String){
        _uiState.update { it.copy(answer = value) }
    }

    fun submit(){
        viewModelScope.launch {
            val state = _uiState.value

            if (state.question.isEmpty()){
                _showSnackBar.send("Pertanyaan tidak boleh kosong.")
                return@launch
            }

            if (state.answer.isEmpty()){
                _showSnackBar.send("Jawaban tidak boleh kosong.")
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }

            runCatching {
                if (state.isEditMode && state.cardId != null){
                    useCaseDeck.updateFlashCard(
                        deckId = deckId,
                        card = FlashCard(
                            id = state.cardId,
                            question = state.question,
                            answer = state.answer
                        )
                    )
                }else{
                    useCaseDeck.addFlashCard(
                        deckId = deckId,
                        question = state.question,
                        answer = state.answer
                    )
                }
            }
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false) }
                    _showSnackBar.send(throwable.message ?: "Gagal membuat flash card")
                }
        }
    }

    fun deleteFlashCard(){
        viewModelScope.launch {
            val cardId = _uiState.value.cardId
            if (cardId == null){
                _showSnackBar.send("Gagal menghapus flash card")
                return@launch
            }

            _uiState.update { it.copy(isDeleting = true) }
            runCatching {
                useCaseDeck.deleteFlashCard(deckId,cardId)
            }.onSuccess {
                _uiState.update { it.copy(isDeleting = false, isSuccess = true) }
            }.onFailure { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                _showSnackBar.send(throwable.message ?: "Gagal menghapus flash card")
            }
        }
    }

    private fun observeCard(cardId: String){
        viewModelScope.launch {
            useCaseDeck.observerDeck(deckId)
                .catch { throwable ->
                    _uiState.update { it.copy(isLoadingCard = false) }
                    _showSnackBar.send(throwable.message ?: "Gagal memuat flash card")
                }
                .collect { deck ->
                    if (hasLoadedCard) return@collect

                    val card = deck?.cards?.firstOrNull { it.id == cardId }
                    if (card != null){
                        hasLoadedCard = true
                        _uiState.update {
                            it.copy(
                                isLoadingCard = false,
                                question = card.question,
                                answer = card.answer
                            )
                        }
                    }
                }
        }
    }

}