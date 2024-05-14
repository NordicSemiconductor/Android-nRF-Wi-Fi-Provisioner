package no.nordicsemi.android.wifi.provisioner.feature.nfc.viemodel

import android.net.wifi.ScanResult

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
internal data object OnBackClickEvent: NfcProvisioningViewEvent

/**
 * Event triggered when the wifi network is selected.
 *
 * @param network The selected network.
 */
internal data class OnNetworkSelectedEvent(val network: ScanResult) : NfcProvisioningViewEvent

/**
 * Event triggered when password is confirmed.
 *
 * @param password The password entered by the user.
 * @param network The selected network.
 */
internal data class OnPasswordConfirmedEvent(
    val password: String,
    val network: ScanResult,
    ) : NfcProvisioningViewEvent
