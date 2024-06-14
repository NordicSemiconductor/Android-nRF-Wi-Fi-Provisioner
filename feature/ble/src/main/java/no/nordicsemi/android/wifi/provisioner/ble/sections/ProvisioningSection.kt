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
            state.isProvisioningComplete() -> WizardStepState.COMPLETED
            state.isProvisioningAvailable() || state.isProvisioningInProgress() -> WizardStepState.CURRENT
            else -> WizardStepState.INACTIVE
        },
        decor = when {
            state.isProvisioningInProgress() -> WizardStepAction.ProgressIndicator
            state.isProvisioningAvailable() || state.isProvisioningComplete() ->
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
        ProgressItem(
            text = state.provisioningStatus.getText(WifiConnectionStateDomain.Authentication),
            status = state.provisioningStatus.getStatus(WifiConnectionStateDomain.Authentication),
            iconRightPadding = 24.dp,
        )
        ProgressItem(
            text = state.provisioningStatus.getText(WifiConnectionStateDomain.Association),
            status = state.provisioningStatus.getStatus(WifiConnectionStateDomain.Association),
            iconRightPadding = 24.dp,
        )
        ProgressItem(
            text = state.provisioningStatus.getText(WifiConnectionStateDomain.ObtainingIp),
            status = state.provisioningStatus.getStatus(WifiConnectionStateDomain.ObtainingIp),
            iconRightPadding = 24.dp,
        )
        ProgressItem(
            text = state.provisioningStatus.getText(WifiConnectionStateDomain.Connected),
            status = state.provisioningStatus.getStatus(WifiConnectionStateDomain.Connected),
            iconRightPadding = 24.dp,
        )
    }
}

private fun Resource<WifiConnectionStateDomain>?.getStatus(state: WifiConnectionStateDomain): ProgressItemStatus {
    return when (this) {
        is Loading -> when (state) {
            WifiConnectionStateDomain.Authentication -> ProgressItemStatus.WORKING
            else -> ProgressItemStatus.DISABLED
        }

        is Success -> when {
            data is WifiConnectionStateDomain.ConnectionFailed && state == WifiConnectionStateDomain.Connected -> ProgressItemStatus.ERROR
            state.id == data.id + 1 -> ProgressItemStatus.WORKING
            state.id <= data.id -> ProgressItemStatus.SUCCESS
            else -> ProgressItemStatus.DISABLED
        }

        is Error -> ProgressItemStatus.ERROR

        else -> ProgressItemStatus.DISABLED
    }
}

@Composable
private fun Resource<WifiConnectionStateDomain>?.getText(state: WifiConnectionStateDomain): String {
    val status = getStatus(state)
    return when (this) {
        is Loading, null -> state.toDisplayString(status)
        is Success -> {
            val data = data
            when {
                data is WifiConnectionStateDomain.ConnectionFailed && state == WifiConnectionStateDomain.Connected -> data.reason?.toDisplayString() ?: "Connection failed"
                else -> state.toDisplayString(status)
            }
        }
        is Error -> when (state) {
            WifiConnectionStateDomain.Connected -> error.message ?: stringResource(id = R.string.unknown_error)
            else -> state.toDisplayString(status)
        }
    }
}