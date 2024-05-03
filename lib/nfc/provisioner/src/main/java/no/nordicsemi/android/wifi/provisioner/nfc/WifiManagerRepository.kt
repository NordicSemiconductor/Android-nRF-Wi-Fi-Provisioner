package no.nordicsemi.android.wifi.provisioner.nfc

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.nordicsemi.android.wifi.provisioner.softap.SoftApManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NfcManager @Inject constructor(
    private val softApManager: SoftApManager,
) {

    /**
     * Lists the SSIDs scanned by the nRF7002 device
     */
    suspend fun listSsids() = softApManager.listSsids()
}

@Singleton
@SuppressLint("MissingPermission")
class WifiManagerRepository @Inject constructor(
    private val wifiManager: WifiManager,
    @ApplicationContext private val context: Context,
) {
    private val _wifiNetworks = MutableStateFlow<List<ScanResult>>(emptyList())
    val wifiNetworks = _wifiNetworks.asStateFlow()

    init {
        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
        }
        val wifiInfo = wifiManager.connectionInfo?.let {
            Log.d("AAA", "Connected to: ${it.ssid}")
        }
        // Register BroadcastReceiver to receive scan results
        val wifiScanReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                    _wifiNetworks.value = wifiManager.scanResults
                    Log.d("AAA", "onReceive: ${wifiManager.scanResults}")
                }
            }
        }
        context.registerReceiver(
            wifiScanReceiver,
            IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        )
    }

    fun startWifiScan() {
        wifiManager.startScan()
    }
}