package com.ilham.common.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@ExperimentalMaterial3Api
@Composable
fun ConfirmationButton(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onConfirm) {
                Text(text = "Ya")
            }
        },
        dismissButton = {
            TextButton(onDismiss) {
                Text(text = "Batal")
            }
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = message)
        }
    )

}