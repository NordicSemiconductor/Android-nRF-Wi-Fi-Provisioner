package no.nordicsemi.android.wifi.provisioner.feature.nfc.uicomponent

import android.net.wifi.ScanResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.wifi.provisioner.feature.nfc.R
import no.nordicsemi.android.wifi.provisioner.nfc.domain.AuthenticationMode
import no.nordicsemi.android.wifi.provisioner.nfc.domain.EncryptionMode
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiData

/**
 * Composable function to show the dialog to enter the password for the selected Wi-Fi network.
 *
 * @param scanResult The selected Wi-Fi network.
 * @param onCancelClick The lambda to be called when the cancel button is clicked.
 * @param onConfirmClick The lambda to be called when the confirm button is clicked.
 */
@Composable
internal fun PasswordDialog(
    scanResult: ScanResult,
    onCancelClick: () -> Unit,
    onConfirmClick: (WifiData) -> Unit,
) {
    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordEmpty by rememberSaveable { mutableStateOf(false) }
    val authMode = AuthenticationMode.get(scanResult)
    val encryptionMode = EncryptionMode.getEncryption(scanResult)

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
            ) {
                var showPassword by rememberSaveable { mutableStateOf(false) }

                // Show the SSID of the selected network. The SSID is read-only.
                OutlinedTextField(
                    value = scanResult.SSID,
                    readOnly = true,
                    label = { Text(text = stringResource(id = R.string.ssid_label)) },
                    onValueChange = { }
                )
                // Show the password field.
                PasswordInputField(
                    input = password,
                    label = stringResource(id = R.string.password),
                    placeholder = stringResource(id = R.string.password_placeholder),
                    showPassword = showPassword,
                    isError = isPasswordEmpty && password.trim().isEmpty(),
                    errorMessage = stringResource(id = R.string.password_error),
                    onShowPassChange = { showPassword = !showPassword },
                    onUpdate = {
                        password = it
                        isPasswordEmpty = password.trim().isEmpty()
                    },
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
                    if (password.trim().isEmpty()) {
                        isPasswordEmpty = true
                    } else {
                        onConfirmClick(
                            WifiData(
                                ssid = scanResult.SSID,
                                macAddress = scanResult.BSSID,
                                password = password,
                                authType = authMode.first(),
                                encryptionMode = encryptionMode
                            )
                        )
                    }
                }
            ) { Text(text = stringResource(id = R.string.confirm)) }
        }
    )
}
