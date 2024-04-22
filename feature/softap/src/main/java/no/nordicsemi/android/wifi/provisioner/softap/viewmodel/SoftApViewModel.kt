@file:Suppress("unused")

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
import no.nordicsemi.android.wifi.provisioner.softap.domain.WifiConfigDomain
import no.nordicsemi.android.wifi.provisioner.softap.view.WiFiAccessPointsDestinationId
import okhttp3.OkHttpClient
import javax.inject.Inject

/**
 * Created by Roshan Rajaratnam on 15/02/2024.
 */
@HiltViewModel
class SoftApViewModel @Inject constructor(
    navigator: Navigator,
    savedStateHandle: SavedStateHandle,
    private val softApManager: SoftApManager,
    private val okHttpClient: OkHttpClient
) : SimpleNavigationViewModel(navigator = navigator, savedStateHandle = savedStateHandle) {
    init {
        softApManager.provisioningState.onEach {
            when (it) {
                ProvisioningState.Disconnected -> {}
                ProvisioningState.Connecting -> {}
                ProvisioningState.Connected -> {
                    discoverServices()
                }

                ProvisioningState.Provisioning -> {}
                ProvisioningState.ProvisioningComplete -> {}
            }
        }.launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        softApManager.disconnect()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    internal fun connect(ssid: String = "0018F0-nrf-wifiprov", password: String) {
        softApManager.connect(ssid = ssid, password = password)
    }

    private fun discoverServices() {
        viewModelScope.launch {
            softApManager.discoverServices()
            navigateTo(WiFiAccessPointsDestinationId)
        }
    }

    private fun listSsids() {
        val handler = CoroutineExceptionHandler { _, t ->
            Log.e("AAAA", "$t")
        }
        viewModelScope.launch(handler) {
            val result = softApManager.listSsids()
            Log.d("AAAA", "Results: ${result.results}")
            result.results.forEach {
                it.wifiInfo?.takeIf { wifiInfo ->
                    wifiInfo.ssid == "OnHub"
                }?.let { wifiInfo ->
                    Log.d("AAAA", "Found the device!")
                    val wifiConfig = WifiConfigDomain(
                        info = wifiInfo,
                        passphrase = "newbird379",
                    )
                    Log.d("AAAA", "WifiConfig: $wifiConfig")
                    provision(wifiConfig)
                    return@forEach
                }
            }
        }
    }

    private fun provision(config: WifiConfigDomain) {

        val handler = CoroutineExceptionHandler { _, t ->
            Log.e("AAAA", "$t")
        }
        viewModelScope.launch(handler) {
            try {
                softApManager.provision(config = config).also { response ->
                    if (response.isSuccessful) {
                        softApManager.disconnect()
                        softApManager.discoverServices()
                    }
                }
            } catch (exception: Exception) {
                // TODO This workaround was added to start the service discovery again once the
                //  provisioning is completed. This has to be cleaned up once the fw disconnects
                //  gracefully.
                Log.e("AAAA", "Error: $exception")
                softApManager.disconnect()
                softApManager.discoverServices()
            }
        }
    }
}

