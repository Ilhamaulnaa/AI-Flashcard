package com.ilham.presentation.study_mode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilham.domain.usecase.DeckUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel(assistedFactory = StudyModeViewModel.Factory::class)
class StudyModeViewModel @AssistedInject constructor(
    @Assisted private val deckId: String,
    private val useCaseDeck: DeckUseCase
): ViewModel(){

    private val _uiState = MutableStateFlow(StudyModeUiState())
    val uiState = _uiState.asStateFlow()

    @AssistedFactory
    interface Factory {
        fun create(deckId: String): StudyModeViewModel
    }

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
                        it.copy(isLoading = false, deck = deck, errorMessage = null)
                    }
                }
        }
    }

}