/*
 * Copyright (c) 2022, Nordic Semiconductor
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

package com.nordicsemi.android.wifi.provisioning.home.view

import com.nordicsemi.wifi.provisioner.library.Loading
import com.nordicsemi.wifi.provisioner.library.Resource
import com.nordicsemi.wifi.provisioner.library.Success
import com.nordicsemi.wifi.provisioner.library.domain.DeviceStatusDomain
import com.nordicsemi.wifi.provisioner.library.domain.ScanRecordDomain
import com.nordicsemi.wifi.provisioner.library.domain.VersionDomain
import com.nordicsemi.wifi.provisioner.library.domain.WifiConnectionStateDomain
import no.nordicsemi.android.common.ui.scanner.model.DiscoveredBluetoothDevice

data class HomeViewEntity(
    val device: DiscoveredBluetoothDevice? = null,
    val version: Resource<VersionDomain>? = null,
    val status: Resource<DeviceStatusDomain>? = null,
    val network: ScanRecordDomain? = null,
    val password: String? = null,
    val persistentMemory: Boolean = true,
    val showPasswordDialog: Boolean? = null,
    val provisioningStatus: Resource<WifiConnectionStateDomain>? = null,
    val unprovisioningStatus: Resource<Unit>? = null,
    val isConnected: Boolean = true
) {
    fun isStatusSuccess(): Boolean {
        return device != null && version is Success && status is Success
    }

    fun isUnprovisioning(): Boolean {
        return device != null
                && version is Success
                && status is Success
                && status.data.wifiState == WifiConnectionStateDomain.CONNECTED
                && unprovisioningStatus !is Success
    }

    fun isRunning(): Boolean {
        return version == null || version is Loading || status is Loading || provisioningStatus is Loading || unprovisioningStatus is Loading
    }

    fun hasFinished(): Boolean {
        val status = (provisioningStatus as? Success)?.data
        return !isConnected
                || status == WifiConnectionStateDomain.CONNECTED
                || status == WifiConnectionStateDomain.CONNECTION_FAILED
                || unprovisioningStatus is Success
    }

    fun hasFinishedWithSuccess(): Boolean {
        val status = (provisioningStatus as? Success)?.data
        return isConnected
                && (status == WifiConnectionStateDomain.CONNECTED
                || status == WifiConnectionStateDomain.CONNECTION_FAILED
                || unprovisioningStatus is Success)
    }
}
