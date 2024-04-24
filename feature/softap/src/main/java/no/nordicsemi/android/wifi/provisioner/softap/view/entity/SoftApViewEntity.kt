package no.nordicsemi.android.wifi.provisioner.softap.view.entity

import no.nordicsemi.android.wifi.provisioner.softap.SoftAp
import no.nordicsemi.kotlin.wifi.provisioner.domain.WifiConnectionStateDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Resource
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Success
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiData
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.view.ViewEntity

data class SoftApViewEntity(
    val device: SoftAp? = null,
    override val network: WifiData? = null,
    override val password: String? = null,
    override val showPasswordDialog: Boolean? = null,
    override val provisioningStatus: Resource<WifiConnectionStateDomain>? = null,
    override val isConnected: Boolean = true,
) : ViewEntity {

    override fun hasFinishedWithSuccess(): Boolean {
        val status = (provisioningStatus as? Success)?.data
        return (isConnected
                && status == WifiConnectionStateDomain.CONNECTED)
                || status == WifiConnectionStateDomain.CONNECTION_FAILED
    }
}