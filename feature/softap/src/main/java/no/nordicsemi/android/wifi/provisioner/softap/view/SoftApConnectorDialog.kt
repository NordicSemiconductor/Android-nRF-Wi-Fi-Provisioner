package no.nordicsemi.android.wifi.provisioner.softap.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.common.theme.NordicTheme
import no.nordicsemi.android.wifi.provisioner.feature.softap.R
import no.nordicsemi.android.wifi.provisioner.ui.R as RUI

private const val DEFAULT_SOFTAP_SSID = "nrf-wifiprov"

@Composable
internal fun EditSsidDialog(
    ssidName: String = DEFAULT_SOFTAP_SSID,
    onSsidChange: (String) -> Unit,
    dismiss: () -> Unit,
) {
    var ssid by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                text = ssidName,
                selection = TextRange(index = ssidName.length)
            )
        )
    }

    AlertDialog(
        onDismissRequest = dismiss,
        icon = { Icon(imageVector = Icons.Outlined.Wifi, contentDescription = null) },
        title = {
            Text(
                text = stringResource(id = R.string.softap_ssid_title),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            SoftApConnectorContent(
                ssid = ssid,
                onSsidChange = {
                    ssid = TextFieldValue(
                        text = it,
                        selection = TextRange(it.length)
                    )
                },
            )
        },
        dismissButton = {
            TextButton(onClick = dismiss) {
                Text(text = stringResource(id = RUI.string.dismiss))
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSsidChange(ssid.text) },
                enabled = ssid.text.isNotEmpty(),
            ) {
                Text(text = stringResource(id = RUI.string.accept))
            }
        }
    )
}

@Composable
private fun SoftApConnectorContent(
    ssid: TextFieldValue,
    onSsidChange: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    Column {
        Text(
            text = stringResource(id = R.string.softap_ssid_rationale),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.size(size = 16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
        ) {
            OutlinedTextField(
                value = ssid,
                onValueChange = { newValue ->
                    onSsidChange(newValue.text)
                },
                singleLine = true,
                modifier = Modifier
                    .weight(1.0f)
                    .focusRequester(focusRequester),
                supportingText = {
                    if (ssid.text.isEmpty()) {
                        Text(text = stringResource(id = R.string.softap_ssid_empty))
                    }
                },
                isError = ssid.text.isEmpty(),
                trailingIcon = {
                    IconButton(onClick = { onSsidChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(id = R.string.action_clear)
                        )
                    }
                }
            )

            IconButton(
                onClick = {
                    onSsidChange(DEFAULT_SOFTAP_SSID)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = stringResource(id = R.string.action_restore)
                )
            }
        }
        LaunchedEffect(key1 = Unit) {
            focusRequester.requestFocus()
        }
    }
}

@Preview(widthDp = 250, heightDp = 140)
@Composable
private fun SoftApConnectorContentPreview() {
    NordicTheme {
        var ssid by rememberSaveable { mutableStateOf(TextFieldValue("value")) }
        SoftApConnectorContent(
            ssid = ssid,
            onSsidChange = { ssid = TextFieldValue(it) },
        )
    }
}