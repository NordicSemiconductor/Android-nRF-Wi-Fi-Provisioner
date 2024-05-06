package no.nordicsemi.android.wifi.provisioner.softap

import android.net.nsd.NsdServiceInfo
import no.nordicsemi.android.wifi.provisioner.softap.SoftApManager.Companion.KEY_LINK_ADDR
import no.nordicsemi.android.wifi.provisioner.softap.domain.WifiConfigDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.ConnectionInfoDomain
import okio.ByteString.Companion.toByteString

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
    val name: String
        get() = ssid

    internal var serviceInfo: NsdServiceInfo? = null
    var macAddress: String? = null
        get() = serviceInfo?.attributes?.get(KEY_LINK_ADDR)?.toByteString()?.utf8()
        private set
    var connectionInfoDomain: ConnectionInfoDomain? = null
        get() = ConnectionInfoDomain(ipv4Address = serviceInfo?.host.toString())
        private set
    var wifiConfigDomain: WifiConfigDomain? = null
        internal set
}