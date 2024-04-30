package no.nordicsemi.android.wifi.provisioner.softap

import no.nordicsemi.android.wifi.provisioner.softap.domain.WifiConfigDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.ConnectionInfoDomain

/**
 * WifiDevice class represents a Wi-Fi network that the device should connect to.
 *
 * @property ssid                      SSID of the network.
 * @property passphraseConfiguration   Passphrase configuration of the network.
 * @property macAddress                MAC address of the device.
 * @property connectionInfoDomain      Connection info of the device.
 */
data class SoftAp(
    val ssid: String,
    val passphraseConfiguration: PassphraseConfiguration
) {
    var macAddress: String? = null
        internal set
    var connectionInfoDomain: ConnectionInfoDomain? = null
        internal set
    var wifiConfigDomain: WifiConfigDomain? = null
        internal set
}