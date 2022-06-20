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
