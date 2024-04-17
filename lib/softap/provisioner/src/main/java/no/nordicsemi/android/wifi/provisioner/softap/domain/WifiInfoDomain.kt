package no.nordicsemi.android.wifi.provisioner.softap.domain

data class WifiInfoDomain(
    val ssid: String,
    val bandDomain: BandDomain,
    val channel: Int,
    val authModeDomain: AuthModeDomain
)
