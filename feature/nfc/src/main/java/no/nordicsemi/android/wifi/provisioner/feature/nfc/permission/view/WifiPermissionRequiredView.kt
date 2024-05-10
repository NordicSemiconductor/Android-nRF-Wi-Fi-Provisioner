package no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import no.nordicsemi.android.common.theme.NordicTheme
import no.nordicsemi.android.common.theme.view.WarningView
import no.nordicsemi.android.wifi.provisioner.feature.nfc.R
import no.nordicsemi.android.wifi.provisioner.feature.nfc.permission.viewmodel.PermissionViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
internal fun WifiPermissionRequiredView() {
    val viewModel: PermissionViewModel = hiltViewModel()
    val context = LocalContext.current
    var permissionDenied by remember { mutableStateOf(viewModel.isWifiPermissionDeniedForever(context)) }

    WarningView(
        imageVector = Icons.Default.WifiOff,
        title = stringResource(id = R.string.wifi_permission_required),
        hint = stringResource(id = R.string.wifi_permission_required_des),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val requiredPermissions = arrayOf(
            Manifest.permission.NEARBY_WIFI_DEVICES,
        )

        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            viewModel.markWifiPermissionRequested()
            permissionDenied = viewModel.isWifiPermissionDeniedForever(context)
            viewModel.refreshWifiPermission()
        }

        if (!permissionDenied) {
            Button(onClick = { launcher.launch(requiredPermissions) }) {
                Text(text = stringResource(id = R.string.grant_permission))
            }
        } else {
            Button(onClick = { openPermissionSettings(context) }) {
                Text(text = stringResource(id = R.string.settings))
            }
        }
    }
}

private fun openPermissionSettings(context: Context) {
    ContextCompat.startActivity(
        context,
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        ),
        null
    )
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
private fun WifiPermissionRequiredViewPreview() {
    NordicTheme {
        WifiPermissionRequiredView()
    }
}