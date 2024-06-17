package no.nordicsemi.android.wifi.provisioner.ble.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NetworkCheck
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.common.theme.view.ProgressItem
import no.nordicsemi.android.common.theme.view.ProgressItemStatus
import no.nordicsemi.android.common.theme.view.WizardStepAction
import no.nordicsemi.android.common.theme.view.WizardStepComponent
import no.nordicsemi.android.common.theme.view.WizardStepState
import no.nordicsemi.android.wifi.provisioner.ble.view.BleViewEntity
import no.nordicsemi.android.wifi.provisioner.ble.view.toDisplayString
import no.nordicsemi.android.wifi.provisioner.feature.ble.R
import no.nordicsemi.android.wifi.provisioner.ui.R as RUI
import no.nordicsemi.kotlin.wifi.provisioner.domain.WifiConnectionStateDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Error
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Loading
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Resource
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Success
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.OnProvisionClickEvent
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.event.ProvisioningViewEvent

@Composable
fun ProvisioningSection(
    state: BleViewEntity,
    onEvent: (ProvisioningViewEvent) -> Unit,
) {
    WizardStepComponent(
        icon = Icons.Outlined.NetworkCheck,
        title = stringResource(id = R.string.section_provision),
        state = when {
            state.isValidationInProgress() || state.isProvisioningComplete() -> WizardStepState.COMPLETED
            state.isProvisioningAvailable() || state.isProvisioningInProgress() -> WizardStepState.CURRENT
            else -> WizardStepState.INACTIVE
        },
        decor = when {
            state.isProvisioningInProgress() -> WizardStepAction.ProgressIndicator
            state.isProvisioningAvailable() || state.isValidationInProgress() || state.isProvisioningComplete() ->
                WizardStepAction.Action(
                    text = stringResource(id = R.string.action_provision),
                    onClick = { onEvent(OnProvisionClickEvent) },
                    enabled = !state.isRunning() && !state.isProvisioningComplete(),
                )
            state.hasProvisioningFailed() ->
                WizardStepAction.Action(
                    text = stringResource(id = R.string.action_retry),
                    onClick = { onEvent(OnProvisionClickEvent) },
                    enabled = !state.isRunning(),
                )
            else -> null
        },
        showVerticalDivider = false,
    ) {
        val progress = when (state.provisioningStatus) {
            is Loading -> ProgressItemStatus.WORKING
            is Success -> ProgressItemStatus.SUCCESS
            is Error -> ProgressItemStatus.ERROR
            else -> ProgressItemStatus.DISABLED
        }
        ProgressItem(
            text = WifiConnectionStateDomain.Disconnected.toDisplayString(progress),
            status = progress,
            iconRightPadding = 24.dp,
        )
    }
}