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

package no.nordicsemi.android.wifi.provisioner.feature.nfc.view

import android.net.wifi.ScanResult
import android.net.wifi.WifiSsid
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.Segment
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.nordicsemi.android.common.permissions.wifi.RequireLocationForWiFi
import no.nordicsemi.android.common.permissions.wifi.RequireWiFi
import no.nordicsemi.android.common.ui.view.NordicAppBar
import no.nordicsemi.android.common.ui.view.WarningView
import no.nordicsemi.android.wifi.provisioner.feature.nfc.R
import no.nordicsemi.android.wifi.provisioner.feature.nfc.mapping.Frequency
import no.nordicsemi.android.wifi.provisioner.feature.nfc.mapping.toDisplayString
import no.nordicsemi.android.wifi.provisioner.feature.nfc.uicomponent.RssiIconView
import no.nordicsemi.android.wifi.provisioner.feature.nfc.uicomponent.VerticalBlueBar
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.OnNavigateUpClickEvent
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.OnNetworkSelectEvent
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.OnPasswordCancelEvent
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.OnPasswordSetEvent
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.OnSortOptionSelected
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.RefreshWiFiNetworksEvent
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.WifiScannerViewEvent
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.WifiScannerViewModel
import no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel.WifiScannerViewState
import no.nordicsemi.android.wifi.provisioner.nfc.domain.AuthenticationMode
import no.nordicsemi.android.wifi.provisioner.nfc.domain.EncryptionMode
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Error
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Loading
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Success
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiData
import no.nordicsemi.android.wifi.provisioner.ui.PasswordDialog
import no.nordicsemi.android.wifi.provisioner.ui.view.WifiSortView

/**
 * A composable function to display the list of available networks.
 */
@RequiresApi(Build.VERSION_CODES.M)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WifiScannerScreen() {
    val wifiScannerViewModel = hiltViewModel<WifiScannerViewModel>()
    val onEvent: (WifiScannerViewEvent) -> Unit = { wifiScannerViewModel.onEvent(it) }
    val viewState by wifiScannerViewModel.viewState.collectAsStateWithLifecycle()
    var isGroupedBySsid by rememberSaveable { mutableStateOf(false) }
    val groupIcon = if (isGroupedBySsid) {
        Icons.AutoMirrored.Default.FormatListBulleted
    } else Icons.AutoMirrored.Default.Segment
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle the back press.
    BackHandler {
        onEvent(OnNavigateUpClickEvent)
    }
    // Show the scanning screen.
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            NordicAppBar(
                title = { Text(text = stringResource(id = R.string.wifi_scanner_appbar)) },
                showBackButton = true,
                onNavigationButtonClick = { onEvent(OnNavigateUpClickEvent) },
                actions = {
                    // Show the group icon to group by SSID.
                    Icon(
                        imageVector = groupIcon,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                isGroupedBySsid = !isGroupedBySsid
                            }
                            .padding(8.dp)

                    )
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            RequireWiFi(isNearbyWifiDevicesPermissionRequired = true) {
                RequireLocationForWiFi {
                    LaunchedEffect(key1 = false) {
                        wifiScannerViewModel.scanAvailableWifiNetworks()
                    }

                    WiFiScannerContent(
                        state = viewState,
                        isGroupedBySsid = isGroupedBySsid,
                        onEvent = onEvent
                    )
                    viewState.selectedNetwork?.let { scanResult ->
                        // Show the password dialog
                        PasswordDialog(
                            onDismiss = { onEvent(OnPasswordCancelEvent) },
                            onConfirmPressed = { password ->
                                val wifiData = WifiData(
                                    ssid = scanResult.SSID,
                                    macAddress = scanResult.BSSID,
                                    password = password,
                                    authType = AuthenticationMode.get(scanResult),
                                    encryptionMode = EncryptionMode.get(scanResult)
                                )
                                onEvent(OnPasswordSetEvent(wifiData))
                            },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WiFiScannerContent(
    state: WifiScannerViewState,
    isGroupedBySsid: Boolean,
    onEvent: (WifiScannerViewEvent) -> Unit,
) {
    Column {
        val insets = WindowInsets.displayCutout
            .union(WindowInsets.navigationBars)
            .only(WindowInsetsSides.Horizontal)
        WifiSortView(
            sortOption = state.sortOption,
            enabled = state.networks is Success,
            insets = insets,
            onChanged = { option -> onEvent(OnSortOptionSelected(option)) }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(insets)
        ) {
            when (val scanningState = state.networks) {
                is Error -> {
                    WarningView(
                        imageVector = Icons.Rounded.Warning,
                        title = stringResource(id = R.string.error_while_scanning),
                        hint = scanningState.t.message ?: "Unknown error occurred."
                    )
                }

                is Loading -> {
                    // Show the loading indicator.
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                is Success -> {
                    // Show the list of available networks.
                    PullToRefreshBox(
                        isRefreshing = state.networks == Loading,
                        onRefresh = { // Refreshing
                            onEvent(RefreshWiFiNetworksEvent)
                        },
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            if (isGroupedBySsid) {
                                // Group by SSID
                                GroupBySsid(state.sortedItems, onEvent)
                            } else {
                                // Show the list of available networks grouped by SSIDs.
                                WifiList(state.sortedItems, onEvent)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * A composable function to display the list of available networks.
 *
 * @param networks The list of available networks.
 * @param onEvent The event callback.
 */
@Composable
internal fun WifiList(
    networks: List<ScanResult>,
    onEvent: (WifiScannerViewEvent) -> Unit
) {
    networks.forEach { network ->
        // Skip hidden networks.
        if (network.SSID.isNotEmpty()) {
            NetworkItem(
                network = network,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                onEvent = onEvent
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun NetworkItem(
    network: ScanResult,
    modifier: Modifier = Modifier,
    onEvent: (WifiScannerViewEvent) -> Unit,
) {
    val securityType = AuthenticationMode.get(network)
    val isOpen = securityType == AuthenticationMode.OPEN

    // Show the network.
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (isOpen) {
                    // Password dialog is not required for open networks.
                    val wifiData = WifiData(
                        ssid = network.SSID,
                        macAddress = network.BSSID,
                        password = null,
                        authType = AuthenticationMode.OPEN,
                        encryptionMode = EncryptionMode.NONE
                    )
                    onEvent(OnPasswordSetEvent(wifiData))
                } else {
                    // Show the password dialog
                    onEvent(OnNetworkSelectEvent(network))
                }
            }
            .then(modifier),
    ) {
        RssiIconView(network.level)
        Column(
            modifier = Modifier
                .padding(8.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Display the SSID of the access point
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Text(
                    text = getSSid(network.wifiSsid),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Text(
                    text = network.SSID,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            // Display the security type of the access point.
            Text(
                text = securityType.toDisplayString(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alpha(0.7f)
            )
            // Display the address of the access point.
            Text(
                text = stringResource(
                    id = R.string.bssid,
                    network.BSSID.uppercase(),
                ),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alpha(0.7f)
            )
            Text(
                text = stringResource(
                    id = R.string.channel_number,
                    Frequency.toChannelNumber(network.frequency),
                    Frequency.get(network.frequency)
                ),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alpha(0.7f),
            )
        }
        if (!isOpen) {
            // Show the lock icon for protected networks.
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
            )
        }
    }
}

/**
 * Returns the SSID of the network from given [WifiSsid].
 */
fun getSSid(wifiSsid: WifiSsid?): String {
    return wifiSsid.toString().replace("\"", "")
}

@Composable
private fun GroupBySsid(
    networks: List<ScanResult>,
    onEvent: (WifiScannerViewEvent) -> Unit,
) {
    networks.groupBy { it.SSID }.forEach { (ssid, network) ->
        var isExpanded by rememberSaveable { mutableStateOf(false) }
        val expandIcon =
            if (isExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore

        // Skip hidden networks.
        if (ssid == null || ssid.isEmpty()) {
            return@forEach
        }
        // Show the network.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(start = 16.dp, 8.dp)
        ) {
            Text(text = ssid)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = expandIcon,
                contentDescription = null,
                modifier = Modifier.padding(8.dp)
            )
        }
        // Show networks under the same SSID.
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandIn(expandFrom = Alignment.TopCenter) + fadeIn(),
            exit = shrinkOut(shrinkTowards = Alignment.TopCenter) + fadeOut()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider()
                VerticalBlueBar {
                    network.forEach { scanResult ->
                        NetworkItem(
                            network = scanResult,
                            modifier = Modifier.padding(8.dp),
                            onEvent = onEvent
                        )
                        if (scanResult != network.last()) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 16.dp),
                                thickness = 0.5.dp
                            )
                        }
                    }
                }
            }
        }
        HorizontalDivider()
    }
}

@Preview(showBackground = true)
@Composable
fun WiFiScannerContentLoading() {
    WiFiScannerContent(
        state = WifiScannerViewState(),
        isGroupedBySsid = false,
        onEvent = {}
    )
}

@RequiresApi(Build.VERSION_CODES.R)
@Preview(showBackground = true)
@Composable
fun WiFiScannerContentNetworks() {
    WiFiScannerContent(
        state = WifiScannerViewState(
            networks = Success(
                listOf(
                    ScanResult().apply {
                        SSID = "Network 1"
                        BSSID = "00:11:22:33:44:55"
                        level = -50
                        frequency = 2437
                        capabilities = "[WPA2-PSK-CCMP][ESS]"
                    },
                    ScanResult().apply {
                        SSID = "Network 2"
                        BSSID = "00:11:22:33:44:56"
                        level = -60
                        frequency = 2437
                        capabilities = "[WPA2-PSK-CCMP][ESS]"
                    }
                )
            )
        ),
        isGroupedBySsid = false,
        onEvent = {}
    )
}