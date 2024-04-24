@file:Suppress("unused")

package no.nordicsemi.android.wifi.provisioner.softap.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import no.nordicsemi.android.common.navigation.Navigator
import no.nordicsemi.android.common.navigation.viewmodel.SimpleNavigationViewModel
import no.nordicsemi.android.wifi.provisioner.softap.SoftApManager
import no.nordicsemi.android.wifi.provisioner.softap.view.SoftApConnectorDestination
import javax.inject.Inject

/**
 * Created by Roshan Rajaratnam on 15/02/2024.
 */
@HiltViewModel
class SoftApConnectorViewModel @Inject constructor(
    navigationManager: Navigator,
    savedStateHandle: SavedStateHandle,
    private val softApManager: SoftApManager
) : SimpleNavigationViewModel(navigator = navigationManager, savedStateHandle = savedStateHandle) {

    @RequiresApi(Build.VERSION_CODES.Q)
    internal fun connect(ssid: String = "0018F0-nrf-wifiprov", password: String) {
        viewModelScope.launch {
            softApManager.connect(ssid = ssid, password = password)?.let {
                navigateUpWithResult(SoftApConnectorDestination, it)
            }
        }
    }
}

