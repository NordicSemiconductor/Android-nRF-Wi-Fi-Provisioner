package com.nordicsemi.android.wifi.provisioning.home.view.sections

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nordicsemi.android.wifi.provisioning.R
import com.nordicsemi.android.wifi.provisioning.home.view.components.DataItem
import com.nordicsemi.android.wifi.provisioning.home.view.components.ErrorDataItem
import com.nordicsemi.android.wifi.provisioning.home.view.components.LoadingItem
import com.nordicsemi.android.wifi.provisioning.home.view.toDisplayString
import com.nordicsemi.wifi.provisioner.library.Error
import com.nordicsemi.wifi.provisioner.library.Loading
import com.nordicsemi.wifi.provisioner.library.Resource
import com.nordicsemi.wifi.provisioner.library.Success
import com.nordicsemi.wifi.provisioner.library.domain.WifiConnectionStateDomain
import no.nordicsemi.ui.scanner.ui.exhaustive

@Composable
internal fun UnprovisioningSection(status: Resource<Unit>) {
    when (status) {
        is Error -> ErrorItem(status.error)
        is Loading -> LoadingItem()
        is Success -> ProvisioningSection()
    }.exhaustive
}


@Composable
private fun ErrorItem(error: Throwable) {
    ErrorDataItem(
        iconRes = R.drawable.ic_upload_wifi,
        title = stringResource(id = R.string.unprovision_status),
        error = error
    )
}

@Composable
private fun ProvisioningSection() {
    DataItem(
        iconRes = R.drawable.ic_upload_wifi,
        title = stringResource(id = R.string.unprovision_status),
        description = stringResource(id = R.string.success)
    )
}
