package com.ilham.common.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ilham.common.theme.FlashTypography

@Composable
fun ConfirmationDialog(
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
            TextButton(onClick = onConfirm) {
                Text(text = "Oke")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        text = {
            Text(
                text = message,
                style = FlashTypography.bodyMedium
            )
        },
        title = {
            Text(
                text = title,
                style = FlashTypography.titleMedium
            )
        }
    )

}
