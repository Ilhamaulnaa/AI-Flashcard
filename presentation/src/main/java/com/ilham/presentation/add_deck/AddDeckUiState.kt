package com.ilham.presentation.add_deck

data class AddDeckUiState(
    val isLoading: Boolean = false,
    val isLoadingDeck: Boolean = false,
    val isDeleting: Boolean = false,
    val isSuccess: Boolean = false,
    val deckId: String? = null,
    val title: String = "",
    val description: String? = null,
    val emoji: String = "",
    val color: String = "",
    val errorMessage: String? = null
){
    val isEditMode: Boolean
        get() = deckId != null
}
