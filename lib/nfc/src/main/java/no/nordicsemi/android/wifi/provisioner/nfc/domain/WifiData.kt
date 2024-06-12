package no.nordicsemi.android.wifi.provisioner.nfc.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * WifiData is a data class that holds the Wi-Fi data.
 *
 * @param ssid The human-readable of the Wi-Fi network.
 * @param password The password of the Wi-Fi network, or null if the network is open.
 * @param macAddress Optional BSSID of the Wi-Fi network, in case a network is accessible from
 *                   multiple access points.
 * @param authType The authentication type of the Wi-Fi network.
 * @param encryptionMode The encryption mode of the Wi-Fi network.
 */
@Parcelize
data class WifiData(
    val ssid: String,
    val password: String?,
    val macAddress: String?,
    val authType: AuthenticationMode,
    val encryptionMode: EncryptionMode,
) : Parcelable