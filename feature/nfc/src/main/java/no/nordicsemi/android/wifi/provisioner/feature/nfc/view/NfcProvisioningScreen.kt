package no.nordicsemi.android.wifi.provisioner.feature.nfc.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiFind
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import no.nordicsemi.android.common.theme.view.NordicAppBar
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viemodel.NfcProvisioningViewEvent
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viemodel.NfcProvisioningViewModel
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viemodel.OnBackClickEvent
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viemodel.OnScanClickEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NfcProvisioningScreen() {
    val viewModel: NfcProvisioningViewModel = hiltViewModel()
    val onEvent: (NfcProvisioningViewEvent) -> Unit = { viewModel.onEvent(it) }
    var openAddWifiDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp)
    ) {
        NordicAppBar(
            text = "Wifi Provisioning over NFC",
            showBackButton = true,
            onNavigationButtonClick = { onEvent(OnBackClickEvent) }
        )
        // Content
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            // Show an option to enter the WiFi credentials manually.
            OutlinedCard(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        openAddWifiDialog = true
                    }

            ) {
                // TODO: Once this is merged with NFC app, replace this with the one from NFC app.
                NfcRecordOutlinedCardItem(
                    headline = "Enter WiFi credentials manually",
                    description = {
                        Text(
                            text = "Add WiFi credentials manually.",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    icon = Icons.Default.Wifi,
                ) {
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                openAddWifiDialog = true
                            }
                            .padding(8.dp)
                    )
                }
            }

            // Show an option to search for a WiFi network.
            OutlinedCard(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        // TODO: Show a list of WiFi networks.

                        onEvent(OnScanClickEvent)
                    }

            ) {
                NfcRecordOutlinedCardItem(
                    headline = "Search for WiFi networks",
                    description = {
                        Text(
                            text = "Search for available WiFi networks.",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    icon = Icons.Default.WifiFind,
                ) {
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                // TODO: Show a list of WiFi networks.
                                onEvent(OnScanClickEvent)
                            }
                            .padding(8.dp)
                    )
                }
            }
            if (openAddWifiDialog) {
                // Open a dialog to enter the WiFi credentials manually.
                AlertDialog(
                    onDismissRequest = { },
                    icon = {
                        Icon(imageVector = Icons.Outlined.Wifi, contentDescription = null)
                    },
                    title = {
                        Text(
                            text = "Setup WiFi",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    text = {
                        Column {
                            var ssid by remember { mutableStateOf("") }
                            var password by remember { mutableStateOf("") }
                            var showPassword by remember { mutableStateOf(false) }
                            Text(
                                text = "Setup WiFi",
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.size(size = 16.dp))
                            OutlinedTextField(value = ssid, onValueChange = { ssid = it })
                            Spacer(modifier = Modifier.size(size = 8.dp))
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
                                                Icons.Outlined.Visibility
                                            else Icons.Outlined.VisibilityOff,
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
                                openAddWifiDialog = false
                            }
                        ) { Text(text = "Cancel") }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                openAddWifiDialog = false
                                // TODO: Connect to the WiFi network.
                            }
                        ) { Text(text = "Confirm") }
                    }
                )
            }
        }
    }
}
