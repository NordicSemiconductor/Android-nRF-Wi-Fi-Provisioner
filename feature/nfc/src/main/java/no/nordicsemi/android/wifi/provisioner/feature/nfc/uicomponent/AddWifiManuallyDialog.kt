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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.wifi.provisioner.feature.nfc.R
import no.nordicsemi.android.wifi.provisioner.nfc.domain.EncryptionMode
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiData
import no.nordicsemi.kotlin.wifi.provisioner.domain.AuthModeDomain

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
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var authMode by rememberSaveable { mutableStateOf("") }
    var encryptionMode by rememberSaveable { mutableStateOf("") }

    var isSsidEmpty by rememberSaveable { mutableStateOf(false) }

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
                val items = AuthModeDomain.entries.map { it.name }
                // Show the authentication dropdown.
                DropdownView(
                    items = items,
                    label = stringResource(id = R.string.authentication),
                    placeholder = stringResource(id = R.string.authentication_placeholder),
                    defaultSelectedItem = authMode
                ) { authMode = it }

                // Show the encryption dropdown.
                DropdownView(
                    items = EncryptionMode.entries.map { it.name },
                    label = stringResource(id = R.string.encryption),
                    placeholder = stringResource(id = R.string.encryption_placeholder),
                    defaultSelectedItem = encryptionMode
                ) { encryptionMode = it }

                // Show the SSID field.
                TextInputField(
                    input = ssid,
                    label = stringResource(id = R.string.ssid_label),
                    placeholder = stringResource(id = R.string.ssid_placeholder),
                    errorState = isSsidEmpty && ssid.isEmpty(),
                    onUpdate = {
                        ssid = it
                        isSsidEmpty = ssid.isEmpty()
                    }
                )

                // Show the password field.
                PasswordInputField(
                    input = password,
                    label = stringResource(id = R.string.password),
                    placeholder = stringResource(id = R.string.password_placeholder),
                    showPassword = showPassword,
                    onShowPassChange = { showPassword = !showPassword },
                    onUpdate = { password = it },
                )
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
                    if (ssid.isEmpty()) {
                        isSsidEmpty = true
                        return@TextButton
                    } else {
                        onConfirmClick(
                            WifiData(
                                ssid = ssid,
                                password = password,
                                authType = authMode,
                                encryptionMode = encryptionMode,
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
