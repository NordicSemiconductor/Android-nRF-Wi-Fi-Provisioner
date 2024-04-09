package no.nordicsemi.android.wifi.provisioner.softap.domain

data class WifiScanResultDomain(
    val ssid: String,
    val bssid: String,
    val bandDomain: BandDomain,
    val channel: Int,
    val authModeDomain: AuthModeDomain,
    val rssi: Int
)