/*
 * Copyright (c) 2022, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.wifi.provisioner.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.DismissEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.PasswordDialogEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.PasswordSetDialogEvent

@Composable
fun PasswordDialog(onEvent: (PasswordDialogEvent) -> Unit) {
    val passwordField = rememberSaveable { mutableStateOf("") }
    val isError = rememberSaveable { mutableStateOf(false) }
    val passwordVisible = rememberSaveable { mutableStateOf(false) }

    val visualTransformation = if (passwordVisible.value) {
        VisualTransformation.None
    } else {
        PasswordVisualTransformation()
    }

    val image = if (passwordVisible.value) {
        Icons.Filled.Visibility
    } else {
        Icons.Filled.VisibilityOff
    }

    val description = if (passwordVisible.value) {
        stringResource(id = R.string.hide_password)
    } else {
        stringResource(id = R.string.show_password)
    }

    AlertDialog(
        onDismissRequest = { onEvent(DismissEvent) },
        title = { Text(stringResource(id = R.string.set_password)) },
        text = {
            OutlinedTextField(
                value = passwordField.value,
                label = { Text(text = stringResource(id = R.string.password)) },
                visualTransformation = visualTransformation,
                onValueChange = {
                    passwordField.value = it
                    isError.value = false
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                        Icon(imageVector = image, description)
                    }
                }
            )
        },
        confirmButton = {
            TextButton(onClick = {
                passwordField.value.let {
                    if (it.isBlank()) {
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
