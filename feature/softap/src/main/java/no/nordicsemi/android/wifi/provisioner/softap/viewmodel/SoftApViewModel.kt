package no.nordicsemi.android.wifi.provisioner.softap.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import no.nordicsemi.android.common.navigation.Navigator
import no.nordicsemi.android.common.navigation.viewmodel.SimpleNavigationViewModel
import no.nordicsemi.android.wifi.provisioner.softap.ProvisioningState
import no.nordicsemi.android.wifi.provisioner.softap.SoftApManager
import no.nordicsemi.android.wifi.provisioner.softap.credentials.Credentials
import javax.inject.Inject

/**
 * Created by Roshan Rajaratnam on 15/02/2024.
 */
@HiltViewModel
class SoftApViewModel @Inject constructor(
    navigator: Navigator,
    savedStateHandle: SavedStateHandle,
    private val softApManager: SoftApManager
) : SimpleNavigationViewModel(navigator = navigator, savedStateHandle = savedStateHandle) {

    init {
        softApManager.provisioningState.onEach {
            when (it) {
                ProvisioningState.Disconnected -> {}
                ProvisioningState.Connecting -> {}
                ProvisioningState.Connected -> {
                    provision()
                }
                ProvisioningState.Provisioning -> {}
                ProvisioningState.ProvisioningComplete -> {}
            }
        }.launchIn(viewModelScope)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    internal fun connect(ssid: String = "mobileappsrules", password: String) {
        softApManager.connect(ssid = ssid, password = password)
    }

    internal fun listSsids() {
        val handler = CoroutineExceptionHandler { _, t ->
            Log.e("AAAA", "$t")
        }
        viewModelScope.launch(handler) {
            softApManager.listSsids()
        }
    }

    internal fun provision(
        credentials: Credentials = Credentials(
            ssid = "OnHub",
            password = "newbird379"
        )
    ) {
        val handler = CoroutineExceptionHandler { _, t ->
            Log.e("AAAA", "$t")
        }
        viewModelScope.launch(handler) {
            softApManager.provision(credentials = credentials).also { response ->
                if (response.isSuccessful) {
                    softApManager.disconnect()
                }
            }
        }
    }
}

