package no.nordicsemi.android.wifi.provisioner.softap

import android.net.nsd.NsdServiceInfo

/**
 * Created by Roshan Rajaratnam on 23/02/2024.
 *
 * Defines the WifiNetworkState
 */
sealed class ProvisioningState {

    /**
     * State when connected to a Unprovisioned Wifi Node.
     */
    data object Disconnected : ProvisioningState()

    /**
     * State when connected to a Unprovisioned Wifi Node.
     */
    data object Connecting : ProvisioningState()

    /**
     * State when connected to a Unprovisioned Wifi Node.
     */
    data object Connected : ProvisioningState()

    /**
     * State when the discovering the network services available on the network.
     */
    data object InitiatingNetworkServiceDiscovery : ProvisioningState()

    /**
     * State when the network service discovery is complete.
     */
    data class NetworkServiceDiscoveryComplete(val serviceInfo : NsdServiceInfo) : ProvisioningState()

    /**
     * State when connected to a Unprovisioned Wifi Node
     */
    data object Provisioning : ProvisioningState()

    /**
     * State when Disconnected from an Unprovisioned Wifi Node
     */
    data object ProvisioningComplete : ProvisioningState()
}