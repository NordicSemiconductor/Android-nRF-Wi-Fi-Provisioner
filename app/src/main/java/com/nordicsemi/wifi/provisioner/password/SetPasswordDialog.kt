package com.nordicsemi.wifi.provisioner.password

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import com.nordicsemi.wifi.provisioner.R

sealed interface PasswordDialogEvent

data class PasswordSetDialogEvent(val password: String) : PasswordDialogEvent

object DismissEvent : PasswordDialogEvent

@Composable
fun PasswordDialog(password: String?, onEvent: (PasswordDialogEvent) -> Unit) {
    val passwordField = rememberSaveable { mutableStateOf(password) }
    val isError = rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onEvent(DismissEvent) },
        title = { Text(stringResource(id = R.string.set_password)) },
        text = {
            OutlinedTextField(
                value = passwordField.value ?: "",
                label = { Text(text = stringResource(id = R.string.password)) },
                onValueChange = {
                    passwordField.value = it
                    isError.value = false
                }
            )
        },
        confirmButton = {
            TextButton(onClick = {
                passwordField.value.let {
                    if (it == null) {
                        isError.value = true
                    } else {
                        onEvent(PasswordSetDialogEvent(it))
                    }
                }
            }) {
                Text(stringResource(id = R.string.accept))
            }
        },
        dismissButton = {
            TextButton(onClick = { onEvent(DismissEvent) }) {
                Text(stringResource(id = R.string.dismiss))
            }
        }
    )
}
