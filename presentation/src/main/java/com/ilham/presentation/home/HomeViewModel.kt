package com.ilham.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilham.common.utils.deckColors
import com.ilham.common.utils.deckEmojis
import com.ilham.domain.model.Deck
import com.ilham.domain.model.FlashCard
import com.ilham.domain.usecase.AuthUseCase
import com.ilham.domain.usecase.GenerateAIFlashCardUseCase
import com.ilham.domain.usecase.HomeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCaseHome: HomeUseCase
): ViewModel() {

    val cardsDummy = listOf(
        FlashCard(
            id = "1",
            question = "lorem ipsum",
            answer = "lorem ipsum"
        ),
        FlashCard(
            id = "2",
            question = "lorem ipsum",
            answer = "lorem ipsum"
        ),
        FlashCard(
            id = "3",
            question = "lorem ipsum",
            answer = "lorem ipsum"
        ),
        FlashCard(
            id = "4",
            question = "lorem ipsum",
            answer = "lorem ipsum"
        )
    )

//    val decksDummy = listOf(
//        Deck(
//            id = "1",
//            name = "Bahasa Inggris",
//            emoji = deckEmojis[0].emoji,
//            description = "belajar bahasa inggris",
//            color = deckColors[0].color,
//            cards = cardsDummy,
//            cardCount = 4
//        ),
//        Deck(
//            id = "2",
//            name = "Bahasa Inggris",
//            emoji = deckEmojis[1].emoji,
//            description = "belajar bahasa inggris",
//            color = deckColors[1].color,
//            cards = cardsDummy,
//            cardCount = 3
//        ),
//        Deck(
//            id = "3",
//            name = "Bahasa Inggris",
//            emoji = deckEmojis[2].emoji,
//            description = "belajar bahasa inggris",
//            color = deckColors[2].color,
//            cards = cardsDummy,
//            cardCount = 3
//        ),
//
//        )

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _showSnackbar = Channel<String>()
    val showSnackbar = _showSnackbar.receiveAsFlow()


    init {
        viewModelScope.launch {
            useCaseHome.observeDecks()
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                    _showSnackbar.send(throwable.message ?: "Gagal memuat deck.")
                }
                .collect { decks ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            decks = decks
                        )
                    }
                }
        }
    }

    fun deleteDeck(deckId: String){
        viewModelScope.launch {
            runCatching {
                useCaseHome.deleteDeck(deckId)
            }.onSuccess {
                _showSnackbar.send("deck berhasil dihapus.")
            }.onFailure { throwable ->
                _showSnackbar.send(throwable.message ?: "deck gagal dihapus.")
            }
        }
    }

    fun signOut(){
        viewModelScope.launch {
            useCaseHome.signOut()
            _uiState.update {
                it.copy(isLoggedOut = true)
            }
        }
    }

}