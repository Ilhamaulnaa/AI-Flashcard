package com.ilham.common.utils

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberSnackBarHostState(): SnackbarHostState {
    return remember { SnackbarHostState() }
}