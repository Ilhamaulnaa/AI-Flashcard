package com.ilham.presentation.login

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ilham.common.theme.FlashColors
import com.ilham.common.theme.FlashShape
import com.ilham.common.theme.FlashSpacing
import com.ilham.common.theme.FlashTypography
import com.ilham.common.theme.FlashcardTheme
import com.ilham.common.ui.FlashButton
import com.ilham.component.AuthTextField
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onNavigateRegister: () -> Unit,
    onNavigateHome: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) = with(viewModel) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        showSnackBar.collect { message ->
            focusManager.clearFocus(force = false)
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess){
            onNavigateHome()
        }
    }

    LoginScreenContent(
        modifier = modifier.padding(horizontal = FlashSpacing.md),
        uiState = uiState,
        onEmailChange = { onEmailChanged(it) },
        onPasswordChange = { onPasswordChanged(it) },
        onLogin = { login() },
        snackHostState = snackbarHostState,
        onNavigateRegister = onNavigateRegister
    )

}

@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    onNavigateRegister: () -> Unit,
    uiState: LoginUiState = LoginUiState(),
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    snackHostState: SnackbarHostState = remember { SnackbarHostState() }
) {

    val coroutineScope = rememberCoroutineScope()
    val emailBringIntoViewRequester = remember { BringIntoViewRequester() }
    val passwordBringIntoViewRequester = remember { BringIntoViewRequester() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Selamat Datang!!",
                style = FlashTypography.displayLarge
            )
            Text(
                text = "Siap untuk sesi belajar kali ini?",
                style = FlashTypography.bodyMedium
            )
            Spacer(modifier = Modifier.padding(FlashSpacing.sm))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White, shape = FlashShape.medium)
                    .clip(FlashShape.medium)
                    .padding(FlashSpacing.md)
            ){
                Column(modifier = Modifier.fillMaxWidth()) {
                    AuthTextField(
                        modifier = Modifier
                            .bringIntoViewRequester(emailBringIntoViewRequester)
                            .onFocusEvent { focusState ->
                                if (focusState.isFocused) {
                                    coroutineScope.launch {
                                        delay(KEYBOARD_SCROLL_DELAY_MS)
                                        emailBringIntoViewRequester.bringIntoView()
                                    }
                                }
                            },
                        label = "Email",
                        value = uiState.email,
                        onValueChange = onEmailChange,
                        placeholder = "contoh@email.com",
                        icon = Icons.Outlined.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    Spacer(modifier = Modifier.height(FlashSpacing.xs))
                    AuthTextField(
                        modifier = Modifier
                            .bringIntoViewRequester(passwordBringIntoViewRequester)
                            .onFocusEvent{ focusState ->
                                if (focusState.isFocused){
                                    coroutineScope.launch{
                                        delay(KEYBOARD_SCROLL_DELAY_MS)
                                        passwordBringIntoViewRequester.bringIntoView()
                                    }
                                }
                            },
                        label = "Password",
                        value = uiState.password ,
                        onValueChange = onPasswordChange,
                        placeholder = "Password",
                        icon = Icons.Outlined.Lock,
                        isPassword = true,
                    )
                    Spacer(modifier = Modifier.height(FlashSpacing.sm))
                    FlashButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Login",
                        onClick = onLogin,
                        isLoading = uiState.isLoading
                    )
                }
            }
            Spacer(modifier = Modifier.height(FlashSpacing.sm))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Belum punya akun? ",
                    style = FlashTypography.bodyLarge
                )
                Text(
                    text = "Daftar sekarang",
                    style = FlashTypography.bodyLarge,
                    color = FlashColors.DeckBlue,
                    modifier = Modifier.clickable(onClick = onNavigateRegister)
                )

            }
        }
    }

}

private const val KEYBOARD_SCROLL_DELAY_MS = 250L

@Preview
@Composable
private fun LoginScreenContentPreview() {
    FlashcardTheme {
        LoginScreenContent(
            onEmailChange = {},
            onPasswordChange = {},
            onLogin = {},
            onNavigateRegister = {}
        )
    }
    
}