package no.nordicsemi.android.wifi.provisioner.nfc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Error
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Loading
import no.nordicsemi.android.wifi.provisioner.nfc.domain.NetworkState
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Success
import javax.inject.Singleton

@RequiresApi(Build.VERSION_CODES.M)
@SuppressWarnings("MissingPermission")
@Singleton
internal class WifiManagerRepositoryImp(
    context: Context,
) : WifiManagerRepository {
    private var wifiManager: WifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val _networkState = MutableStateFlow<NetworkState<List<ScanResult>>>(Loading())
    override val networkState = _networkState.asStateFlow()

    /**
     * Broadcast receiver to receive the scan results.
     */
    private val wifiScanReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceive(context: Context, intent: Intent) {
            try {
                val success =
                    intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                if (!success) {
                    wifiManager.startScan()
                }
                val results = wifiManager.scanResults
                _networkState.value = Success(results)
            } catch (e: Exception) {
                e.printStackTrace()
                _networkState.value = Error(e)
            }
        }
    }

    init {
        // Register the broadcast receiver
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(wifiScanReceiver, intentFilter)
    }

    /**
     * This method is used to start the wifi scan.
     */
    override fun onScan() {
        wifiManager.startScan()
    }
}
