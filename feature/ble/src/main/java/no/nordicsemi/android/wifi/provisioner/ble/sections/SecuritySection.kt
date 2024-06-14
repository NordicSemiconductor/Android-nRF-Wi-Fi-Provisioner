package no.nordicsemi.android.wifi.provisioner.ble.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiPassword
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import no.nordicsemi.android.common.theme.view.WizardStepAction
import no.nordicsemi.android.common.theme.view.WizardStepComponent
import no.nordicsemi.android.common.theme.view.WizardStepState
import no.nordicsemi.android.wifi.provisioner.ble.view.BleViewEntity
import no.nordicsemi.android.wifi.provisioner.feature.ble.R
import no.nordicsemi.android.wifi.provisioner.ui.mapping.toDisplayString
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnShowPasswordDialog
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.ProvisioningViewEvent

@Composable
fun SecuritySection(
    state: BleViewEntity,
    onEvent: (ProvisioningViewEvent) -> Unit,
) {
    WizardStepComponent(
        icon = Icons.Default.WifiPassword,
        title = stringResource(id = R.string.section_security),
        state = when {
            state.isConnected && state.network != null && state.password == null && state.network.isPasswordRequired() -> WizardStepState.CURRENT
            state.isConnected && state.network != null && (state.password != null || !state.network.isPasswordRequired()) -> WizardStepState.COMPLETED
            else -> WizardStepState.INACTIVE
        },
        decor = when {
            state.isConnected && state.network != null  ->
                WizardStepAction.Action(
                    text = stringResource(id = R.string.action_set_password),
                    onClick = { onEvent(OnShowPasswordDialog) },
                    enabled = state.network.isPasswordRequired() && !state.isRunning() && !state.isProvisioningComplete(),
                )
            else -> null
        },
    ) {
        ProvideTextStyle(value = MaterialTheme.typography.bodyMedium) {
            when {
                !state.isConnected || state.network == null ->
                    Text(text = stringResource(id = R.string.set_password))

                else -> {
                    Text(
                        text = stringResource(
                            id = R.string.security,
                            state.network.authMode.toDisplayString()
                        )
                    )
                    if (state.network.isPasswordRequired() && state.password != null) {
                        Text(text = stringResource(id = R.string.password_encoded))
                    }
                }
            }
        }
    }
}