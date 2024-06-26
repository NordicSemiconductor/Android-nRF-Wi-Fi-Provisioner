/*
 * Copyright (c) 2024, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.wifi.provisioner.ble.view

import no.nordicsemi.android.kotlin.ble.core.ServerDevice
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Loading
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Resource
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Success
import no.nordicsemi.kotlin.wifi.provisioner.domain.DeviceStatusDomain
import no.nordicsemi.android.wifi.provisioner.ble.domain.VersionDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.WifiConnectionStateDomain
import no.nordicsemi.kotlin.wifi.provisioner.domain.resource.Error
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.WifiData
import no.nordicsemi.kotlin.wifi.provisioner.feature.common.view.ViewEntity

data class BleViewEntity(
    val device: ServerDevice? = null,
    val version: Resource<VersionDomain>? = null,
    val status: Resource<DeviceStatusDomain>? = null,
    override val network: WifiData? = null,
    override val password: String? = null,
    val persistentMemory: Boolean = true,
    override val showPasswordDialog: Boolean? = null,
    override val provisioningStatus: Resource<WifiConnectionStateDomain>? = null,
    val unprovisioningStatus: Resource<Unit>? = null,
    override val isConnected: Boolean = false
) : ViewEntity {

    fun isRunning(): Boolean {
        return device != null && version == null
                || version is Loading
                || status is Loading
                || isProvisioningInProgress()
                || isValidationInProgress()
                || unprovisioningStatus is Loading
    }

    override fun hasFinished(): Boolean {
        val status = (provisioningStatus as? Success)?.data
        return !isConnected
                || status is WifiConnectionStateDomain.Connected
                || status is WifiConnectionStateDomain.ConnectionFailed
                || unprovisioningStatus is Success
    }

    override fun isProvisioningAvailable(): Boolean {
        return isConnected
                && network != null
                && provisioningStatus == null
                && (password != null || !network.isPasswordRequired())
    }

    override fun isProvisioningInProgress(): Boolean {
        return isConnected && provisioningStatus is Loading
    }

    override fun isValidationInProgress(): Boolean {
        return isConnected
                && provisioningStatus != null
                && provisioningStatus !is Loading
                && !isProvisioningComplete()
                && !hasProvisioningFailed()
    }

    override fun isProvisioningComplete(): Boolean {
        val status = (provisioningStatus as? Success)?.data
        return isConnected && (status is WifiConnectionStateDomain.Connected
                || status is WifiConnectionStateDomain.ConnectionFailed)
    }

    override fun hasProvisioningFailed(): Boolean {
        return provisioningStatus is Error
    }
}
