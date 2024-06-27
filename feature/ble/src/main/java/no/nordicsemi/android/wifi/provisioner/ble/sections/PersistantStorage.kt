package no.nordicsemi.android.wifi.provisioner.ble.sections

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.WifiPassword
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import no.nordicsemi.android.common.ui.view.StatusItem
import no.nordicsemi.android.common.ui.view.WizardStepComponent
import no.nordicsemi.android.common.ui.view.WizardStepState
import no.nordicsemi.android.wifi.provisioner.ble.view.BleViewEntity
import no.nordicsemi.android.wifi.provisioner.ble.view.OnVolatileMemoryChangedEvent
import no.nordicsemi.android.wifi.provisioner.feature.ble.R
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.ProvisioningViewEvent


@Composable
fun PersistentStorage(
    state: BleViewEntity,
    onEvent: (ProvisioningViewEvent) -> Unit,
) {
    Row {
        WizardStepComponent(
            modifier = Modifier.weight(weight = 1f),
            icon = Icons.Default.SdStorage,
            title = stringResource(id = R.string.section_persistent_storage),
            state = when {
                state.isConnected && state.network != null && state.password == null && state.network.isPasswordRequired() -> WizardStepState.CURRENT
                state.isConnected && state.network != null && (state.password != null || !state.network.isPasswordRequired()) -> WizardStepState.COMPLETED
                else -> WizardStepState.INACTIVE
            },
            decor = null,
        ) {
            StatusItem {
                Text(text = stringResource(id = R.string.persist_credentials_rationale))
            }
        }
        if (
            state.isProvisioningAvailable() ||
            state.isProvisioningInProgress() ||
            state.isProvisioningComplete() ||
            state.hasProvisioningFailed() ||
            state.isValidationInProgress()
        )
            Switch(
                checked = state.persistentMemory,
                onCheckedChange = { onEvent(OnVolatileMemoryChangedEvent) },
                enabled = state.isProvisioningAvailable()
            )
    }
}