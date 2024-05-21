package no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel

import android.net.wifi.ScanResult
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
 * Event triggered when the back button is clicked.
 */
internal data object OnBackClickEvent : NfcProvisioningViewEvent

/**
 * Event triggered when the wifi network is selected.
 *
 * @param network The selected network.
 */
internal data class OnNetworkSelectedEvent(val network: ScanResult) : NfcProvisioningViewEvent

/**
 * Event triggered when password is confirmed.
 *
 * @param wifiData The selected wifi network.
 */
internal data class OnPasswordConfirmedEvent(
    val wifiData: WifiData,
) : NfcProvisioningViewEvent
