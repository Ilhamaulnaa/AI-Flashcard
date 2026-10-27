package com.ilham.presentation.study_mode

import com.ilham.domain.model.Deck

data class StudyModeUiState(
    val isLoading: Boolean = false,
    val deck: Deck? = null,
    val errorMessage: String? = null,

    )