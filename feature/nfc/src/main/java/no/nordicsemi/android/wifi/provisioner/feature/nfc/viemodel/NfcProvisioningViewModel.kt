package no.nordicsemi.android.wifi.provisioner.feature.nfc.viemodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import no.nordicsemi.android.common.navigation.Navigator
import no.nordicsemi.android.wifi.provisioner.nfc.NfcManager
import no.nordicsemi.android.wifi.provisioner.nfc.WifiManagerRepository
import javax.inject.Inject

@HiltViewModel
internal class NfcProvisioningViewModel @Inject constructor(
    private val navigator: Navigator,
    private val nfcManager: NfcManager,
    private val wifiManager: WifiManagerRepository,
) : ViewModel() {

    fun onEvent(event: NfcProvisioningViewEvent) {
        when (event) {
            is OnScanClickEvent -> {
                // TODO: Implement provision click event
                // Navigate to the Scanning screen.
                listSsids()
            }

            OnBackClickEvent -> navigator.navigateUp()
        }
    }

    private fun listSsids() {
        val handler = CoroutineExceptionHandler { _, exception ->
            exception.printStackTrace()
        }
        viewModelScope.launch(handler) {
            try {
                val result = wifiManager.startWifiScan()
                wifiManager.wifiNetworks.onEach { scanResults ->
                    Log.d("AAA", "WifiNetworks: $scanResults")
                    scanResults.onEach {
                        Log.d("AAA", "ScanResult: $it")
                    }
                }.launchIn(viewModelScope)
                Log.d("AAA", "listSsids: $result")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("AAA", "Error: ${e.message}")
            }
            /*val result = nfcManager.listSsids()
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
                    return@forEach
                }
            }*/
        }
    }
}
