package no.nordicsemi.android.wifi.provisioner.feature.nfc.view

import android.net.wifi.ScanResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.wifi.provisioner.feature.nfc.R
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
    var password by remember { mutableStateOf("") }
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
                var showPassword by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = scanResult.SSID,
                    readOnly = true,
                    label = { Text(text = stringResource(id = R.string.ssid)) },
                    onValueChange = { }
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    visualTransformation = if (showPassword)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (!showPassword)
                                    Icons.Outlined.VisibilityOff
                                else Icons.Outlined.Visibility,
                                contentDescription = null
                            )
                        }
                    }
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
                    onConfirmClick(
                        WifiData(
                            ssid = scanResult.SSID,
                            password = password,
                            authType = "WPA2-PSK", // FIXME: use it from the scanResult.
                            encryptionMode = "NONE" // FIXME: use it from the scanResult.
                        )
                    )
                }
            ) { Text(text = stringResource(id = R.string.confirm)) }
        }
    )
}
