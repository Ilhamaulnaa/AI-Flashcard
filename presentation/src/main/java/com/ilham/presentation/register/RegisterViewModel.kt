package com.ilham.presentation.register

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilham.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val useCaseAuth: AuthUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val _showSnackBar = Channel<String>()
    val showSnackBar = _showSnackBar.receiveAsFlow()

    fun onRegisterEmailChanged(email: String){
        viewModelScope.launch {
            _uiState.update {
                it.copy(email = email)
            }
        }
    }

    fun onRegisterPasswordChanged(password: String){
        viewModelScope.launch {
            _uiState.update {
                it.copy(password = password)
            }
        }
    }

    fun register(){
        viewModelScope.launch {
            val state = _uiState.value

            if (state.email.isBlank() || state.password.length < 6){
                _showSnackBar.send("Email wajib diisi dan password minimal 6 karakter.")
                return@launch
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches()){
                _showSnackBar.send("Email tidak valid.")
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                useCaseAuth.signUp(
                    email = state.email,
                    password = state.password
                )
            }.onFailure { throwable ->
                Log.e(TAG, "Error on register: ${throwable.message}" )
                _uiState.update { it.copy(isLoading = false) }
                _showSnackBar.send(throwable.message ?: "Gagal mendaftarkan akun.")
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false, isRegisterSuccess = true) }
            }
        }
    }

    private companion object {
        const val TAG = "RegisterViewModel"
    }
}