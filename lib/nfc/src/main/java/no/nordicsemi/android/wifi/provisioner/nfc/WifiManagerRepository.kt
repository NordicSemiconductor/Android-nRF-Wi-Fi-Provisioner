package no.nordicsemi.android.wifi.provisioner.nfc

import android.net.wifi.ScanResult
import kotlinx.coroutines.flow.StateFlow
import no.nordicsemi.android.wifi.provisioner.nfc.domain.NetworkState

/**
 * A repository interface to manage the wifi manager.
 */
sealed interface WifiManagerRepository {

    /**
     * A state flow to represent the network state of the wifi scan.
     */
    val networkState: StateFlow<NetworkState<List<ScanResult>>>

    /**
     * This method is used to start the wifi scan.
     */
    fun onScan()
}