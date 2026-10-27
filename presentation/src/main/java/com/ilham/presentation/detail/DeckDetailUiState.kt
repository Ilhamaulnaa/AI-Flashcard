package com.ilham.presentation.detail

import com.ilham.domain.model.Deck

data class DeckDetailUiState(

    val isLoading: Boolean = false,
    val deck: Deck? = null,
    val errorMessage: String? = null

)
