package com.nordicsemi.android.wifi.provisioning.wifi.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nordicsemi.android.wifi.provisioning.R
import com.nordicsemi.android.wifi.provisioning.home.CloseIconAppBar
import com.nordicsemi.android.wifi.provisioning.wifi.viewmodel.WifiScannerViewModel
import com.nordicsemi.wifi.provisioner.library.domain.WifiInfoDomain

@Composable
internal fun WifiScannerScreen() {
    val viewModel = hiltViewModel<WifiScannerViewModel>()
    val viewEntity = viewModel.state.collectAsState().value

    Column {
        CloseIconAppBar(stringResource(id = R.string.app_name)) {
            viewModel.onEvent(NavigateUpEvent)
        }

        WifiList(viewEntity)
    }
}

@Composable
private fun WifiList(viewEntity: WifiScannerViewEntity) {
    LazyColumn {
        viewEntity.items.forEach {
            item { WifiItem(wifi = it.wifiInfo) }
        }
    }
}

@Composable
private fun WifiItem(wifi: WifiInfoDomain) {
    Row {
        Icon(
            Icons.Default.Wifi, contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = CircleShape
                )
        )

        Spacer(modifier = Modifier.size(16.dp))
        
        Text(text = wifi.ssid)
    }
}
