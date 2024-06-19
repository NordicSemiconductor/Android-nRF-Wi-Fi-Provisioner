package no.nordicsemi.android.wifi.provisioner.ble.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BluetoothConnected
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import no.nordicsemi.android.common.theme.NordicTheme
import no.nordicsemi.android.common.ui.view.ProgressItem
import no.nordicsemi.android.common.ui.view.ProgressItemStatus
import no.nordicsemi.android.common.ui.view.WizardStepAction
import no.nordicsemi.android.common.ui.view.WizardStepComponent
import no.nordicsemi.android.common.ui.view.WizardStepState
import no.nordicsemi.android.kotlin.ble.core.MockServerDevice
import no.nordicsemi.android.wifi.provisioner.ble.domain.VersionDomain
import no.nordicsemi.android.wifi.provisioner.ble.view.BleViewEntity
import no.nordicsemi.android.wifi.provisioner.feature.ble.R
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Error
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Loading
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Success
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnReconnectClickEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.ProvisioningViewEvent
import no.nordicsemi.android.wifi.provisioner.ui.R as RUI

@Composable
fun ConnectionSection(
    state: BleViewEntity,
    onEvent: (ProvisioningViewEvent) -> Unit,
) {
    Column {
        WizardStepComponent(
            icon = Icons.Outlined.BluetoothConnected,
            title = stringResource(id = R.string.section_connect),
            state = when {
                state.device == null -> WizardStepState.INACTIVE
                !state.isConnected || state.network == null -> WizardStepState.CURRENT
                else -> WizardStepState.COMPLETED
            },
            decor = when {
                !state.isConnected && state.version != null && state.version !is Loading ->
                    WizardStepAction.Action(
                        text = stringResource(id = R.string.action_retry),
                        onClick = { onEvent(OnReconnectClickEvent) },
                    )
                state.version is Loading -> WizardStepAction.ProgressIndicator
                else -> null
            },
        ) {
            val pairingText = when {
                state.device == null -> stringResource(id = R.string.connect)
                state.version is Loading -> stringResource(id = R.string.connecting)
                !state.isConnected -> stringResource(id = R.string.disconnected)
                else -> stringResource(id = R.string.connected)
            }
            ProgressItem(
                text = pairingText,
                status = when {
                    state.device == null -> ProgressItemStatus.DISABLED
                    state.version is Loading -> ProgressItemStatus.WORKING
                    !state.isConnected -> ProgressItemStatus.ERROR
                    else -> ProgressItemStatus.SUCCESS
                },
            )
            val readingText = when {
                !state.isConnected || state.version == null -> stringResource(id = R.string.read_version)
                state.version is Error -> stringResource(id = RUI.string.error, state.version.error.message ?: stringResource(id = RUI.string.unknown_error))
                state.version is Success -> stringResource(id = R.string.version, state.version.data.value)
                else -> stringResource(id = R.string.reading_version)
            }
            ProgressItem(
                text = readingText,
                status = when {
                    !state.isConnected -> ProgressItemStatus.DISABLED
                    state.status is Error || state.version is Error -> ProgressItemStatus.ERROR
                    state.status == null -> ProgressItemStatus.DISABLED
                    state.status is Loading -> ProgressItemStatus.WORKING
                    else -> ProgressItemStatus.SUCCESS
                },
            )
        }
    }
}

@Preview(heightDp = 200)
@Composable
private fun DeviceStatusNotConnectedPreview() {
    NordicTheme {
        ConnectionSection(
            state = BleViewEntity(
                device = MockServerDevice("Device", "00:11:22:33:44:55"),
                version = Success(VersionDomain(1)),
                isConnected = false,
            ),
            onEvent = {},
        )
    }
}

@Preview(heightDp = 200)
@Composable
private fun DeviceStatusPreview() {
    NordicTheme {
        ConnectionSection(
            state = BleViewEntity(
                device = MockServerDevice("Device", "00:11:22:33:44:55"),
                version = Success(VersionDomain(1)),
                status = Loading(),
                isConnected = true,
            ),
            onEvent = {},
        )
    }
}
@Preview(heightDp = 200)
@Composable
private fun DeviceStatusVersionErrorPreview() {
    NordicTheme {
        ConnectionSection(
            state = BleViewEntity(
                device = MockServerDevice("Device", "00:11:22:33:44:55"),
                version = Error(Exception("Some error")),
                isConnected = true,
            ),
            onEvent = {},
        )
    }
}
@Preview(heightDp = 200)
@Composable
private fun DeviceStatusStatusErrorPreview() {
    NordicTheme {
        ConnectionSection(
            state = BleViewEntity(
                device = MockServerDevice("Device", "00:11:22:33:44:55"),
                version = Success(VersionDomain(1)),
                status = Error(Exception("Some error")),
                isConnected = true,
            ),
            onEvent = {},
        )
    }
}