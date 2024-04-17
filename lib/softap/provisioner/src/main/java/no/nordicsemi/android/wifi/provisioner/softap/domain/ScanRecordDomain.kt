package no.nordicsemi.android.wifi.provisioner.softap.domain

data class ScanRecordDomain(
    val infoDomain: WifiInfoDomain?,
    val rssi: Int
)