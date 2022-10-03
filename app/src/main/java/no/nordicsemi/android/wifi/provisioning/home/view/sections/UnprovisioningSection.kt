package no.nordicsemi.android.wifi.provisioning.home.view.sections

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.wifi.provisioning.R
import no.nordicsemi.android.wifi.provisioning.home.view.components.DataItem
import no.nordicsemi.android.wifi.provisioning.home.view.components.ErrorDataItem
import no.nordicsemi.android.wifi.provisioning.home.view.components.LoadingItem
import no.nordicsemi.android.wifi.provisioning.util.Error
import no.nordicsemi.android.wifi.provisioning.util.Loading
import no.nordicsemi.android.wifi.provisioning.util.Resource
import no.nordicsemi.android.wifi.provisioning.util.Success

@Composable
internal fun UnprovisioningSection(status: Resource<Unit>) {
    when (status) {
        is Error -> ErrorItem(status.error)
        is Loading -> LoadingItem(modifier = Modifier.padding(vertical = 8.dp))
        is Success -> ProvisioningSection()
    }
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
