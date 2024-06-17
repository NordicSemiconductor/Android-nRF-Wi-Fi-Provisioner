package no.nordicsemi.kotlin.wifi.provisioner.feature.common.view

import no.nordicsemi.kotlin.wifi.provisioner.domain.WifiConnectionStateDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Resource
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiData

/**
 * ViewEntity is an interface that represents the view state of the application
 *
 * @property network               Network data
 * @property password              Password of the network
 * @property showPasswordDialog    Show password dialog
 * @property isConnected           Is connected to the network
 **/
interface ViewEntity {
    val network: WifiData?
    val password: String?
    val showPasswordDialog: Boolean?
    val provisioningStatus: Resource<WifiConnectionStateDomain>?
    val isConnected: Boolean

    fun hasFinished(): Boolean

    fun isProvisioningAvailable(): Boolean

    fun isProvisioningInProgress(): Boolean

    /**
     * Check if the provisioning has finished successfully.
     */
    fun isProvisioningComplete(): Boolean

    fun hasProvisioningFailed(): Boolean
}