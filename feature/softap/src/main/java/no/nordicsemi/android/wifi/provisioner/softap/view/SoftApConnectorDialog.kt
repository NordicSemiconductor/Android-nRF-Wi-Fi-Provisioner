package no.nordicsemi.android.wifi.provisioner.softap.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.wifi.provisioner.feature.softap.R
import no.nordicsemi.android.wifi.provisioner.softap.Open
import no.nordicsemi.android.wifi.provisioner.softap.PassphraseConfiguration


@Composable
internal fun SoftApConnectorDialog(
    isNetworkServiceDiscoveryCompleted: Boolean?,
    connect: (String, PassphraseConfiguration) -> Unit,
    dismiss: () -> Unit
) {
    var ssid by rememberSaveable { mutableStateOf("nrf-wifiprov") }
    var password by rememberSaveable { mutableStateOf("") }
    var showButtons by rememberSaveable {
        mutableStateOf(true)
    }

    AlertDialog(
        onDismissRequest = dismiss,
        icon = { Icon(imageVector = Icons.Outlined.Wifi, contentDescription = null) },
        title = {
            Text(
                text = stringResource(id = R.string.soft_ap),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            if (isNetworkServiceDiscoveryCompleted == null) {
                showButtons = true
                SoftApConnectorContent(
                    ssid = ssid,
                    password = password,
                    onSsidChange = { ssid = it },
                    onPasswordChange = { password = it },
                    onShowPassword = {}
                )
            } else if (!isNetworkServiceDiscoveryCompleted) {
                showButtons = false
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = stringResource(id = R.string.softap_rationale),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                showButtons = true
            }
        },
        dismissButton = {
            if(showButtons) {
                TextButton(onClick = dismiss) {
                    Text(text = "Cancel")
                }
            }
        },
        confirmButton = {
            if(showButtons) {
                TextButton(onClick = { connect(ssid, Open) }) {
                    Text(text = "Confirm")
                }
            }
        }
    )
}

@Composable
private fun SoftApConnectorContent(
    ssid: String,
    password: String,
    onSsidChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onShowPassword: () -> Unit
) {
    var showPassword by rememberSaveable { mutableStateOf(true) }
    Column {
        Text(
            text = stringResource(id = R.string.softap_rationale),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.size(size = 16.dp))
        OutlinedTextField(value = ssid, onValueChange = onSsidChange)
        Spacer(modifier = Modifier.size(size = 8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            visualTransformation = if (showPassword)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (!showPassword)
                            Icons.Outlined.Visibility
                        else Icons.Outlined.VisibilityOff,
                        contentDescription = null
                    )
                }
            }
        )
    }
}