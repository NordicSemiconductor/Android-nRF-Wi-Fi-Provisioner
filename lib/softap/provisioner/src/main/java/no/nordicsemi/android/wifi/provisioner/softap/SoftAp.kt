package no.nordicsemi.android.wifi.provisioner.softap

import no.nordicsemi.kotlin.wifi.provisioner.domain.ConnectionInfoDomain

/**
 * WifiDevice class represents a Wi-Fi network that the device should connect to.
 */
data class SoftAp(
    val ssid: String,
    val password: String?
) {
    var connectionInfoDomain: ConnectionInfoDomain? = null
}