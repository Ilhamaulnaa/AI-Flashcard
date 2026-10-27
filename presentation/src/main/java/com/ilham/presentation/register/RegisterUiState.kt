package com.ilham.presentation.register

data class RegisterUiState(

    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isRegisterSuccess: Boolean = false

)
