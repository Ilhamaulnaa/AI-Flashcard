package com.ilham.presentation.login

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
class LoginViewModel @Inject constructor(
    private val useCaseAuth: AuthUseCase
) : ViewModel(){

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _showSnackBar = Channel<String>()
    val showSnackBar = _showSnackBar.receiveAsFlow()

    fun onEmailChanged(email: String){
        viewModelScope.launch {
            _uiState.update {
                it.copy(email = email)
            }
        }
    }

    fun onPasswordChanged(password: String){
        viewModelScope.launch {
            _uiState.update {
                it.copy(password = password)
            }
        }
    }

    fun login(){
        viewModelScope.launch {
            val state = _uiState.value
            if (state.email.isBlank() || state.password.length < 6){
                _showSnackBar.send("Email wajib diisi dan password minimal 6 karakter.")
                return@launch
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches()){
                _showSnackBar.send("Email tidak valid")
                return@launch
            }

            _uiState.update {
                it.copy(isLoading = true)
            }

            runCatching {
                useCaseAuth.signIn(
                    email = state.email,
                    password = state.password
                )
            }.onFailure { throwable ->
                Log.e(TAG, "Error on login : ${throwable.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                    )
                }
                _showSnackBar.send(throwable.message ?: "Gagal masuk. Coba lagi.")
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoginSuccess = true
                    )
                }
            }
        }
    }

    private companion object{
        const val TAG = "LoginViewModel"
    }

}