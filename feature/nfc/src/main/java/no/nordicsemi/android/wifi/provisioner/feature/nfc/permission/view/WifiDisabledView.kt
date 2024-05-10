package no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.view

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import no.nordicsemi.android.common.theme.NordicTheme
import no.nordicsemi.android.common.theme.view.WarningView
import no.nordicsemi.android.wifi.provisioner.feature.nfc.R

@Composable
internal fun WifiDisabledView() {
    WarningView(
        imageVector = Icons.Default.WifiOff,
        title = stringResource(id = R.string.wifi_disabled),
        hint = stringResource(id = R.string.wifi_disabled_des),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val context = LocalContext.current
        Button(onClick = { enableWifi(context) }) {
            Text(text = stringResource(id = R.string.enable_wifi))
        }
    }
}

private fun enableWifi(context: Context) {
    context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
}

@Preview
@Composable
private fun WifiDisabledViewPreview() {
    NordicTheme {
        WifiDisabledView()
    }
}