package no.nordicsemi.android.wifi.provisioner.ble.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiFind
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.common.theme.view.ProgressItem
import no.nordicsemi.android.common.theme.view.ProgressItemStatus
import no.nordicsemi.android.common.theme.view.VerticalDivider
import no.nordicsemi.android.common.theme.view.WizardStepAction
import no.nordicsemi.android.common.theme.view.WizardStepComponent
import no.nordicsemi.android.common.theme.view.WizardStepState
import no.nordicsemi.android.wifi.provisioner.ble.view.BleViewEntity
import no.nordicsemi.android.wifi.provisioner.ble.view.OnUnprovisionEvent
import no.nordicsemi.android.wifi.provisioner.ble.view.toDisplayString
import no.nordicsemi.android.wifi.provisioner.feature.ble.R
import no.nordicsemi.android.wifi.provisioner.ui.R as RUI
import no.nordicsemi.android.wifi.provisioner.ui.mapping.toDisplayString
import no.nordicsemi.kotlin.wifi.provisioner.domain.DeviceStatusDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Error
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Loading
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Success
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiData
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnSelectWifiEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.ProvisioningViewEvent

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
        showVerticalDivider = false
    ) {
        Column {
            when {
                !state.isConnected || state.status == null ->
                    ProgressItem(
                        text = stringResource(id = R.string.select_wifi),
                        status = ProgressItemStatus.DISABLED,
                        iconRightPadding = 24.dp,
                    )

                state.unprovisioningStatus is Loading ->
                    ProgressItem(
                        text = "Sending request...",
                        status = ProgressItemStatus.WORKING,
                        iconRightPadding = 24.dp,
                    )

                state.unprovisioningStatus is Success ->
                    ProgressItem(
                        text = stringResource(
                            id = R.string.status_info,
                            stringResource(id = R.string.wifi_status_unprovisioned)
                        ),
                        status = ProgressItemStatus.SUCCESS,
                        iconRightPadding = 24.dp,
                    )

                state.unprovisioningStatus is Error ->
                    ProgressItem(
                        text = state.unprovisioningStatus.error.message
                            ?: stringResource(id = RUI.string.unknown_error),
                        status = ProgressItemStatus.ERROR,
                        iconRightPadding = 24.dp,
                    )

                state.status is Loading ->
                    ProgressItem(
                        text = "Loading...",
                        status = ProgressItemStatus.WORKING,
                        iconRightPadding = 24.dp,
                    )

                state.status is Success ->
                    ProgressItem(
                        text = stringResource(
                            id = R.string.status_info,
                            state.status.data.wifiState.toDisplayString()
                        ),
                        status = ProgressItemStatus.SUCCESS,
                        iconRightPadding = 24.dp,
                    )

                state.status is Error ->
                    ProgressItem(
                        text = state.status.error.message
                            ?: stringResource(id = RUI.string.unknown_error),
                        status = ProgressItemStatus.ERROR,
                        iconRightPadding = 24.dp,
                    )
            }
            if (state.isConnected) {
                ProvideTextStyle(value = MaterialTheme.typography.bodyMedium) {
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
    }
}

@Composable
private fun DeviceNetworkStatus(status: DeviceStatusDomain) {
    Row(
        modifier = Modifier
            .padding(top = 16.dp)
            .height(IntrinsicSize.Min)
    ) {
        VerticalDivider(
            modifier = Modifier.padding(start = 9.dp, end = 26.dp)
        )
        Column(
            modifier = Modifier.padding(start = 8.dp, end = 16.dp)
        ) {
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
}

@Composable
private fun NetworkStatus(network: WifiData) {
    Row(
        modifier = Modifier
            .padding(top = 16.dp)
            .height(IntrinsicSize.Min)
    ) {
        VerticalDivider(
            modifier = Modifier.padding(start = 9.dp, end = 26.dp)
        )
        Column(
            modifier = Modifier.padding(start = 8.dp, end = 16.dp)
        ) {
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
}