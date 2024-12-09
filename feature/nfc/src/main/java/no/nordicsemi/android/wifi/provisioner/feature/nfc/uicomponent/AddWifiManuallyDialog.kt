/*
 * Copyright (c) 2024, Nordic Semiconductor
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

package no.nordicsemi.android.wifi.provisioner.feature.nfc.uicomponent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.wifi.provisioner.feature.nfc.R
import no.nordicsemi.android.wifi.provisioner.feature.nfc.mapping.authListToDisplay
import no.nordicsemi.android.wifi.provisioner.feature.nfc.mapping.toAuthenticationMode
import no.nordicsemi.android.wifi.provisioner.feature.nfc.mapping.toDisplayString
import no.nordicsemi.android.wifi.provisioner.feature.nfc.mapping.toEncryptionMode
import no.nordicsemi.android.wifi.provisioner.nfc.domain.EncryptionMode
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiData

/**
 * Composable function to show the dialog to add Wi-Fi manually.
 *
 * @param onCancelClick The lambda to be called when the cancel button is clicked.
 * @param onConfirmClick The lambda to be called when the confirm button is clicked.
 */
@Composable
internal fun AddWifiManuallyDialog(
    onCancelClick: () -> Unit,
    onConfirmClick: (WifiData) -> Unit,
) {
    var ssid by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf<String?>(null) }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var isPasswordEmpty by rememberSaveable { mutableStateOf(false) }
    var authMode by rememberSaveable { mutableStateOf("WPA2-Personal") } // default to WPA2-Personal.
    var encryptionMode by rememberSaveable { mutableStateOf(EncryptionMode.AES.toDisplayString()) } // default to AES.
    var isSsidEmpty by rememberSaveable { mutableStateOf(false) }
    var macAddress by remember { mutableStateOf(TextFieldValue(text = "")) }
    var isMacAddressError by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { },
        icon = {
            Icon(imageVector = Icons.Outlined.Wifi, contentDescription = null)
        },
        title = {
            Text(
                text = stringResource(id = R.string.setup_wifi_title),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // Show the SSID field.
                TextInputField(
                    input = ssid,
                    label = stringResource(id = R.string.ssid_label),
                    placeholder = stringResource(id = R.string.ssid_placeholder),
                    errorState = isSsidEmpty && ssid.trim().isEmpty(),
                    errorMessage = stringResource(id = R.string.ssid_error),
                    onUpdate = {
                        ssid = it
                        isSsidEmpty = ssid.isEmpty()
                    }
                )

                // Show the authentication dropdown.
                DropdownView(
                    items = authListToDisplay(),
                    label = stringResource(id = R.string.authentication),
                    placeholder = stringResource(id = R.string.authentication_placeholder),
                    defaultSelectedItem = authMode
                ) { authMode = it }

                // Show the password field only if the authentication mode is not open.
                if (authMode.lowercase() != "open") {
                    // Show the password field.
                    PasswordInputField(
                        modifier = Modifier.fillMaxWidth(),
                        input = password ?: "",
                        label = stringResource(id = R.string.password),
                        placeholder = stringResource(id = R.string.password_placeholder),
                        error = if (isPasswordEmpty)
                            stringResource(id = R.string.password_error)
                        else
                            null,
                        showPassword = showPassword,
                        onShowPassChange = { showPassword = !showPassword },
                        onUpdate = {
                            password = it
                            isPasswordEmpty = password?.isEmpty() == true
                        },
                    )
                } else {
                    // Clear the password if the authentication mode is open.
                    password = null
                }

                // Show the MAC address field.
                TextInputField(
                    input = macAddress,
                    label = stringResource(id = R.string.mac_address_label),
                    placeholder = stringResource(id = R.string.mac_address_placeholder),
                    errorState = isMacAddressError && macAddress.text.isNotEmpty(),
                    errorMessage = stringResource(id = R.string.mac_address_error),
                    onUpdate = {
                        val value = addColonToMacAddress(it.text.uppercase())
                        macAddress = TextFieldValue(
                            text = value,
                            selection = TextRange(value.length),
                        )
                        isMacAddressError = !isValidMacAddress(value)
                    }
                )

                // Show the encryption dropdown, only for protected network.
                if (authMode.lowercase() != "open") {
                    DropdownView(
                        items = EncryptionMode.entries.map { it.toDisplayString() },
                        label = stringResource(id = R.string.encryption),
                        placeholder = stringResource(id = R.string.encryption_placeholder),
                        defaultSelectedItem = encryptionMode,
                    ) { encryptionMode = it }
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onCancelClick()
                }
            ) { Text(text = stringResource(id = R.string.cancel)) }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Validate the fields.
                    when {
                        ssid.trim().isEmpty() -> isSsidEmpty = true

                        authMode.lowercase() != "open" && password?.isEmpty() ?: true ->
                            isPasswordEmpty = true

                        macAddress.text.isNotEmpty() && !isValidMacAddress(macAddress.text) ->
                            isMacAddressError = true

                        else -> onConfirmClick(
                            WifiData(
                                ssid = ssid,
                                macAddress = macAddress.text.ifBlank { null },
                                password = password,
                                authType = authMode.toAuthenticationMode(),
                                encryptionMode = if (authMode.lowercase() == "open")
                                    EncryptionMode.NONE else encryptionMode.toEncryptionMode(),
                            )
                        )
                    }
                }
            ) { Text(text = stringResource(id = R.string.confirm)) }
        }
    )
}

@Preview
@Composable
private fun OpenAddWifiManuallyDialogPreview() {
    AddWifiManuallyDialog(
        onCancelClick = {},
        onConfirmClick = {}
    )
}

/** Adds colon to the MAC address. */
private fun addColonToMacAddress(s: String, insertText: String = ":"): String {
    val mac = s.replace(insertText, "")
    val sb = StringBuilder()
    for (i in mac.indices) {
        sb.append(mac[i])
        if (i % 2 == 1 && i != mac.length - 1) {
            sb.append(insertText)
        }
    }
    return sb.toString()
}

/** Checks if the MAC address is valid. */
private fun isValidMacAddress(address: String): Boolean {
    return try {
        // Regex pattern to match a valid MAC address
        val macAddressPattern = Regex("^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$")
        return macAddressPattern.matches(address)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        false
    }
}