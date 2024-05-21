package no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel

import android.net.wifi.ScanResult
import no.nordicsemi.android.wifi.provisioner.nfc.domain.Loading
import no.nordicsemi.android.wifi.provisioner.nfc.domain.NetworkState

/**
 * NfcProvisioningViewState is a data class that holds the state of the NFC provisioning screen.
 * @param view The current view state of the NFC provisioning screen.
 */
internal data class NfcProvisioningViewState(
    val view: NfcProvisioningView = Home,
)

/**
 * NfcProvisioningView is a sealed interface that holds the different view states of the NFC provisioning screen.
 */
internal sealed interface NfcProvisioningView

/**
 * Home is a data object that represents the home screen of the NFC provisioning screen.
 */
internal data object Home : NfcProvisioningView

/**
 * Scan is a data class that represents the scanning screen of the NFC provisioning screen.
 * @param networkState The network state of the scanning screen.
 */
internal data class Scan(
    val networkState: NetworkState<List<ScanResult>> = Loading(),
) : NfcProvisioningView

internal data class AskForPassword(
    val network: ScanResult,
) : NfcProvisioningView

internal data object Provisioning : NfcProvisioningView
