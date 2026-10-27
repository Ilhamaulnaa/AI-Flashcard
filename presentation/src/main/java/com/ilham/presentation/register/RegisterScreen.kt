package com.ilham.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ilham.common.theme.FlashColors
import com.ilham.common.theme.FlashShape
import com.ilham.common.theme.FlashSpacing
import com.ilham.common.theme.FlashTypography
import com.ilham.common.ui.FlashButton
import com.ilham.common.utils.rememberSnackBarHostState
import com.ilham.component.AuthTextField
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel(),
    onNavigateLogin: () -> Unit,
) = with(viewModel) {

    val uiState by uiState.collectAsStateWithLifecycle()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        showSnackBar.collect { message ->
            focusManager.clearFocus(false)
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(uiState.isRegisterSuccess) {
        if (uiState.isRegisterSuccess){
            onNavigateLogin()
        }
    }

    RegisterScreenContent(
        modifier = modifier.padding(horizontal = FlashSpacing.md),
        uiState = uiState,
        onEmailChange = {
            onRegisterEmailChanged(it)
        },
        onPasswordChange = {
            onRegisterPasswordChanged(it)
        },
        onRegisterClick = { register() },
        onNavigateLogin = onNavigateLogin,
        snackbarHostState = snackbarHostState

    )

}

@Composable
fun RegisterScreenContent(
    modifier: Modifier = Modifier,
    uiState: RegisterUiState = RegisterUiState(),
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateLogin: () -> Unit,
    snackbarHostState: SnackbarHostState = rememberSnackBarHostState()
) {

    val emailBringIntoRequest = remember { BringIntoViewRequester() }
    val passwordBringIntoRequest = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Mulai Belajar Sekarang!",
                style = FlashTypography.titleLarge
            )
            Spacer(modifier = Modifier.height(FlashSpacing.xs))
            Text(
                text = "Gabung dengan ribuan pelajar pintar lainnya",
                style = FlashTypography.bodyMedium
            )
            Spacer(modifier = Modifier.height(FlashSpacing.md))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = FlashShape.medium)
                    .clip(FlashShape.medium)
                    .padding(FlashSpacing.md)
            ) {
                Column(modifier = Modifier.fillMaxWidth()){
                    AuthTextField(
                        modifier = Modifier
                            .bringIntoViewRequester(emailBringIntoRequest)
                            .onFocusEvent{ focusState ->
                                if (focusState.isFocused){
                                    coroutineScope.launch {
                                        delay(KEYBOARD_SCROLL_DELAY)
                                        emailBringIntoRequest.bringIntoView()
                                    }
                                }
                            },
                        label = "Email",
                        value = uiState.email,
                        placeholder = "example@gmail.com",
                        onValueChange = onEmailChange,
                        icon = Icons.Outlined.Email
                    )
                    Spacer(modifier = Modifier.height(FlashSpacing.xs))
                    AuthTextField(
                        modifier = Modifier
                            .bringIntoViewRequester(passwordBringIntoRequest)
                            .onFocusEvent{ focusState ->
                                if (focusState.isFocused){
                                    coroutineScope.launch {
                                        delay(KEYBOARD_SCROLL_DELAY)
                                        passwordBringIntoRequest.bringIntoView()
                                    }
                                }
                            },
                        label = "Password",
                        value = uiState.password,
                        placeholder = "Password",
                        onValueChange = onPasswordChange,
                        icon = Icons.Outlined.Lock
                    )
                    Spacer(modifier = Modifier.height(FlashSpacing.lg))
                    FlashButton(
                        text = "Daftar",
                        onClick = onRegisterClick
                    )
                }
            }
            Spacer(modifier = Modifier.height(FlashSpacing.sm))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Sudah punya akun? ",
                    style = FlashTypography.bodyMedium
                )
                Spacer(modifier = Modifier.width(FlashSpacing.xs))
                Text(
                    text = "Login",
                    style = FlashTypography.bodyMedium,
                    color = FlashColors.DeckBlue,
                    modifier = Modifier.clickable{ onNavigateLogin() }
                )
            }
        }
    }

}

private const val KEYBOARD_SCROLL_DELAY = 250L