package no.nordicsemi.android.wifi.provisioning.wifi.view

import no.nordicsemi.android.wifi.provisioning.wifi.viewmodel.WifiSortOption
import no.nordicsemi.wifi.provisioner.library.domain.AuthModeDomain
import no.nordicsemi.wifi.provisioner.library.domain.ScanRecordDomain

data class WifiScannerViewEntity(
    val isLoading: Boolean = true,
    val error: Throwable? = null,
    val sortOption: WifiSortOption = WifiSortOption.RSSI,
    private val items: List<ScanRecordsSameSsid> = emptyList()
) {
    val sortedItems: List<ScanRecordsSameSsid>

    init {
        sortedItems = when (sortOption) {
            WifiSortOption.NAME -> items.sortedBy { it.ssid }
            WifiSortOption.RSSI -> items.sortedByDescending { it.biggestRssi }
        }
    }
}

data class ScanRecordsSameSsid(
    val ssid: String,
    val authMode: AuthModeDomain,
    val items: List<ScanRecordDomain> = emptyList()
) {

    val biggestRssi: Int

    init {
        biggestRssi = items.maxOf { it.rssi ?: 0 }
    }
}
