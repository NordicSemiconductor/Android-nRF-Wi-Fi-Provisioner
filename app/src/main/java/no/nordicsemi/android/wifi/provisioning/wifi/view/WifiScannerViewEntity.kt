package no.nordicsemi.android.wifi.provisioning.wifi.view

import no.nordicsemi.android.wifi.provisioning.wifi.viewmodel.WifiSortOption
import no.nordicsemi.wifi.provisioner.library.domain.AuthModeDomain
import no.nordicsemi.wifi.provisioner.library.domain.ScanRecordDomain

data class WifiScannerViewEntity(
    val isLoading: Boolean = true,
    val error: Throwable? = null,
    val sortOption: WifiSortOption = WifiSortOption.RSSI,
    private val items: List<ScanRecordsForSsid> = emptyList()
) {
    val sortedItems: List<ScanRecordsForSsid>

    init {
        sortedItems = when (sortOption) {
            WifiSortOption.NAME -> items.sortedBy { it.wifiData.ssid }
            WifiSortOption.RSSI -> items.sortedByDescending { it.biggestRssi }
        }
    }
}

data class ScanRecordsForSsid(
    val wifiData: WifiData,
    val items: List<ScanRecordDomain> = emptyList(),
) {

    val biggestRssi: Int

    init {
        biggestRssi = items.maxOf { it.rssi ?: 0 }
    }
}

data class WifiData(
    val ssid: String,
    val authMode: AuthModeDomain,
    val channelFallback: ScanRecordDomain, //Needed for proto v1
    val selectedChannel: ScanRecordDomain? = null
) {

    fun isPasswordRequired(): Boolean {
        return authMode != AuthModeDomain.OPEN
    }
}
