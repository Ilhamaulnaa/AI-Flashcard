package com.ilham.presentation.home

import com.ilham.domain.model.Deck

data class HomeUiState(
    val isLoading: Boolean = true,
    val isLoggedOut: Boolean = false,
    val decks: List<Deck> = emptyList(),
)
