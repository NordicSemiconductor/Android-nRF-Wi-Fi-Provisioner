package no.nordicsemi.android.wifi.provisioner.softap.view.entity

import no.nordicsemi.android.wifi.provisioner.softap.SoftAp
import no.nordicsemi.kotlin.wifi.provisioner.domain.WifiConnectionStateDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Resource
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Success
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiData
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.view.ViewEntity

/**
 * SoftApViewEntity is a data class that represents the view state of the SoftAp provisioning screen
 */
data class SoftApViewEntity(
    val device: SoftAp? = null,
    override val network: WifiData? = null,
    override val password: String? = null,
    override val showPasswordDialog: Boolean? = null,
    override val provisioningStatus: Resource<WifiConnectionStateDomain>? = null,
    override val isConnected: Boolean = true,
    val isAuthorized: Boolean = false,
    val showSoftApDialog: Boolean = false,
    val isNetworkServiceDiscoveryCompleted: Boolean? = null
) : ViewEntity {

    override fun hasFinished(): Boolean {
        val status = (provisioningStatus as? Success)?.data
        return status == WifiConnectionStateDomain.DISCONNECTED ||
                status == WifiConnectionStateDomain.CONNECTED
    }

    override fun hasFinishedWithSuccess(): Boolean {
        val status = (provisioningStatus as? Success)?.data
        return status == WifiConnectionStateDomain.CONNECTED
    }
}