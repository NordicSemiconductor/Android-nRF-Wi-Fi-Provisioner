package no.nordicsemi.android.wifi.provisioner.nfc.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * WifiData is a data class that holds the wifi data.
 * @param ssid The ssid of the wifi network.
 * @param password The password of the wifi network.
 * @param authType The authentication type of the wifi network.
 */
@Parcelize
data class WifiData(
    val ssid: String,
    val password: String,
    val authType: String,
    val encryptionMode: String = "NONE",
    // TODO: Add more fields as required.
): Parcelable