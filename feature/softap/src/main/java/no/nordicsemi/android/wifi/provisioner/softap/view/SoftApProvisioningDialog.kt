package no.nordicsemi.android.wifi.provisioner.softap.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import no.nordicsemi.android.wifi.provisioner.feature.softap.R
import no.nordicsemi.android.wifi.provisioner.softap.view.entity.SoftApViewEntity
import no.nordicsemi.android.wifi.provisioner.ui.CircularProgressIndicatorContent


@Composable
internal fun SoftApProvisioningDialog(state: SoftApViewEntity, dismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = dismiss,
        icon = { Icon(imageVector = Icons.Outlined.Settings, contentDescription = null) },
        title = {
            Text(
                text = stringResource(id = R.string.provisioning),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            if (state.hasFinished() || state.hasFinishedWithSuccess()) {
                Text(text = stringResource(id = R.string.provisioning_device_success))
            } else {
                CircularProgressIndicatorContent(
                    text = stringResource(id = R.string.provisioning_device_rationale)
                )
            }
        },
        confirmButton = {
            if (state.hasFinished()) {
                TextButton(onClick = dismiss) {
                    Text(text = stringResource(id = R.string.ok))
                }
            }
        }
    )
}