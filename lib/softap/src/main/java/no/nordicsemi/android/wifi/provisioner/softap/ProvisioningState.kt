package no.nordicsemi.android.wifi.provisioner.softap

/**
 * Created by Roshan Rajaratnam on 23/02/2024.
 *
 * Defines the WifiNetworkState
 */
sealed class ProvisioningState {

    /**
     * State when connected to a Unprovisioned Wifi Node
     */
    data object Disconnected : ProvisioningState()

    /**
     * State when connected to a Unprovisioned Wifi Node
     */
    data object Connecting : ProvisioningState()

    /**
     * State when connected to a Unprovisioned Wifi Node
     */
    data object Connected : ProvisioningState()

    /**
     * State when connected to a Unprovisioned Wifi Node
     */
    data object Provisioning : ProvisioningState()

    /**
     * State when Disconnected from an Unprovisioned Wifi Node
     */
    data object ProvisioningComplete : ProvisioningState()
}