package no.nordicsemi.android.wifi.provisioner.ble.sections

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiFind
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.common.ui.view.ProgressItem
import no.nordicsemi.android.common.ui.view.ProgressItemStatus
import no.nordicsemi.android.common.ui.view.StatusItem
import no.nordicsemi.android.common.ui.view.WizardStepAction
import no.nordicsemi.android.common.ui.view.WizardStepComponent
import no.nordicsemi.android.common.ui.view.WizardStepState
import no.nordicsemi.android.wifi.provisioner.ble.view.BleViewEntity
import no.nordicsemi.android.wifi.provisioner.ble.view.OnUnprovisionEvent
import no.nordicsemi.android.wifi.provisioner.ble.view.toDisplayString
import no.nordicsemi.android.wifi.provisioner.feature.ble.R
import no.nordicsemi.android.wifi.provisioner.ui.mapping.toDisplayString
import no.nordicsemi.kotlin.wifi.provisioner.domain.AuthModeDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.BandDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.DeviceStatusDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.ScanRecordDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.WifiInfoDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Error
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Loading
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Success
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiData
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnSelectWifiEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.ProvisioningViewEvent
import okio.ByteString.Companion.encodeUtf8
import no.nordicsemi.android.wifi.provisioner.ui.R as RUI

@Composable
fun NetworkStatusSection(
    state: BleViewEntity,
    onEvent: (ProvisioningViewEvent) -> Unit,
) {
    WizardStepComponent(
        icon = Icons.Default.WifiFind,
        title = stringResource(id = R.string.section_network),
        state = when {
            state.isConnected && state.network != null -> WizardStepState.COMPLETED
            state.isConnected && state.status != null -> WizardStepState.CURRENT
            else -> WizardStepState.INACTIVE
        },
        decor = when {
            !state.isConnected -> null
            state.unprovisioningStatus is Loading -> WizardStepAction.ProgressIndicator
            state.status is Success ->
                if (state.status.data.isContentEmpty() || state.unprovisioningStatus is Success) {
                    WizardStepAction.Action(
                        text = stringResource(id = R.string.action_select),
                        onClick = { onEvent(OnSelectWifiEvent) },
                        enabled = !state.isRunning() && !state.isProvisioningComplete(),
                    )
                } else {
                    WizardStepAction.Action(
                        text = stringResource(id = R.string.action_unprovision),
                        onClick = { onEvent(OnUnprovisionEvent) },
                        enabled = !state.isRunning(),
                        dangerous = true,
                    )
                }
            else -> null
        },
    ) {
        when {
            !state.isConnected || state.status == null ->
                ProgressItem(
                    text = stringResource(id = R.string.select_wifi),
                    status = ProgressItemStatus.DISABLED,
                )

            state.unprovisioningStatus is Loading ->
                ProgressItem(
                    text = "Sending request...",
                    status = ProgressItemStatus.WORKING,
                )

            state.unprovisioningStatus is Success ->
                ProgressItem(
                    text = stringResource(
                        id = R.string.status_info,
                        stringResource(id = R.string.wifi_status_unprovisioned)
                    ),
                    status = ProgressItemStatus.SUCCESS,
                )

            state.unprovisioningStatus is Error ->
                ProgressItem(
                    text = state.unprovisioningStatus.error.message
                        ?: stringResource(id = RUI.string.unknown_error),
                    status = ProgressItemStatus.ERROR,
                )

            state.status is Loading ->
                ProgressItem(
                    text = "Loading...",
                    status = ProgressItemStatus.WORKING,
                )

            state.status is Success ->
                ProgressItem(
                    text = stringResource(
                        id = R.string.status_info,
                        state.status.data.wifiState.toDisplayString()
                    ),
                    status = ProgressItemStatus.SUCCESS,
                )

            state.status is Error ->
                ProgressItem(
                    text = state.status.error.message
                        ?: stringResource(id = RUI.string.unknown_error),
                    status = ProgressItemStatus.ERROR,
                )
        }
        if (state.isConnected) {
            when {
                // Show selected network,
                state.network != null ->
                    NetworkStatus(network = state.network)

                // If the device was un-provisioned, don't display old configuration,
                state.unprovisioningStatus != null -> {}

                // Show current network,
                state.status is Success && !state.status.data.isContentEmpty() ->
                    DeviceNetworkStatus(status = state.status.data)
            }
        }
    }
}

@Composable
private fun DeviceNetworkStatus(status: DeviceStatusDomain) {
    StatusItem {
        status.wifiInfo?.let {
            Text(text = stringResource(id = RUI.string.ssid, it.ssid))
            Text(text = stringResource(id = RUI.string.bssid, it.macAddress))
            it.band?.toDisplayString()?.let {
                Text(text = stringResource(id = RUI.string.band, it))
            }
            Text(text = stringResource(id = RUI.string.channel, it.channel.toString()))
        }

        status.connectionInfo?.let {
            if (status.wifiInfo != null) {
                Spacer(modifier = Modifier.size(16.dp))
            }

            Text(
                text = stringResource(id = R.string.connection_info),
                style = MaterialTheme.typography.labelLarge
            )
            Text(text = stringResource(id = RUI.string.ip_4, it.ipv4Address))
        }

        status.scanParams?.let {
            if (status.wifiInfo != null || status.connectionInfo != null) {
                Spacer(modifier = Modifier.size(16.dp))
            }

            Text(
                text = stringResource(id = R.string.scan_param_title),
                style = MaterialTheme.typography.labelLarge
            )
            Text(text = stringResource(id = RUI.string.band, it.band.toDisplayString()))
            Text(text = stringResource(id = R.string.scan_param_passive, it.passive.toString()))
            Text(text = stringResource(id = R.string.scan_param_period_ms, it.periodMs.toString()))
            Text(text = stringResource(id = R.string.scan_param_group_channels, it.groupChannels.toString()))
        }
    }
}

@Composable
private fun NetworkStatus(network: WifiData) {
    StatusItem {
        Text(text = stringResource(id = RUI.string.ssid, network.ssid))
        network.selectedChannel?.wifiInfo?.let { selectedAccessPoint ->
            Text(text = stringResource(id = RUI.string.bssid, selectedAccessPoint.macAddress))
            selectedAccessPoint.band?.toDisplayString()?.let {
                Text(text = stringResource(id = RUI.string.band, it))
            }
            Text(text = stringResource(id = RUI.string.channel, selectedAccessPoint.channel.toString()))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NetworkStatusPreview() {
    MaterialTheme {
        NetworkStatus(
            network = WifiData(
                ssid = "SSID",
                authMode = AuthModeDomain.WPA3_PSK,
                channelFallback = ScanRecordDomain(rssi = null, wifiInfo = null),
                selectedChannel = ScanRecordDomain(
                    rssi = -30,
                    wifiInfo = WifiInfoDomain(
                        ssid = "SSID",
                        bssid = "004455".encodeUtf8(),
                        band = BandDomain.BAND_2_4_GH,
                        channel = 13,
                        authModeDomain = AuthModeDomain.WPA3_PSK,
                    )
                )
            )
        )
    }
}