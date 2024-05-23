package no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel

import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiData

/**
 * A sealed class to represent the events that can be triggered from the UI.
 */
internal sealed interface NfcProvisioningViewEvent

/**
 * Event triggered when the wifi ssid scan button is clicked.
 */
internal data object OnScanClickEvent : NfcProvisioningViewEvent

/**
 * Event triggered when the wifi network is added manually.
 *
 * @param wifiData The Wi-Fi data.
 */
internal data class OnAddWifiNetworkClickEvent(
    val wifiData: WifiData,
) : NfcProvisioningViewEvent

/**
 * Event triggered when the back button is clicked.
 */
internal data object OnBackClickEvent : NfcProvisioningViewEvent
