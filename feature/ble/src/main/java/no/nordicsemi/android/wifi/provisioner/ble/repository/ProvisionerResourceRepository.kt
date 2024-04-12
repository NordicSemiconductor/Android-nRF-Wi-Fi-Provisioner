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

package no.nordicsemi.android.wifi.provisioner.ble.repository

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import no.nordicsemi.android.wifi.provisioner.ble.ProvisionerRepository
import no.nordicsemi.android.wifi.provisioner.ble.Resource
import no.nordicsemi.android.wifi.provisioner.ble.domain.DeviceStatusDomain
import no.nordicsemi.android.wifi.provisioner.ble.domain.ScanRecordDomain
import no.nordicsemi.android.wifi.provisioner.ble.domain.VersionDomain
import no.nordicsemi.android.wifi.provisioner.ble.domain.WifiConfigDomain
import no.nordicsemi.android.wifi.provisioner.ble.domain.WifiConnectionStateDomain
import no.nordicsemi.android.wifi.provisioner.ble.internal.ConnectionStatus

class ProvisionerResourceRepository(
    private val repository: ProvisionerRepository
) {

    suspend fun start(device: BluetoothDevice): Flow<ConnectionStatus> {
        return repository.start(device)
    }

    fun readVersion(): Flow<Resource<VersionDomain>> {
        return runTask { repository.readVersion() }
    }

    fun getStatus(): Flow<Resource<DeviceStatusDomain>> {
        return runTask { repository.getStatus() }
    }

    fun startScan(): Flow<Resource<ScanRecordDomain>> {
        return repository.startScan()
            .map { Resource.createSuccess(it) }
            .onStart { emit(Resource.createLoading()) }
            .catch {
                it.printStackTrace()
                emit(Resource.createError(it))
            }
    }

    suspend fun stopScanBlocking() {
        repository.stopScan()
    }

    fun setConfig(config: WifiConfigDomain): Flow<Resource<WifiConnectionStateDomain>> {
        return repository.setConfig(config)
            .map { Resource.createSuccess(it) }
            .onStart { emit(Resource.createLoading()) }
            .catch {
                it.printStackTrace()
                emit(Resource.createError(it))
            }
    }

    fun forgetConfig(): Flow<Resource<Unit>> {
        return runTask { repository.forgetConfig() }
    }

    suspend fun release() {
        repository.release()
    }

    private fun <T> runTask(block: suspend () -> T): Flow<Resource<T>> {
        return flow { emit(Resource.createSuccess(block())) }
            .onStart { emit(Resource.createLoading()) }
            .catch {
                it.printStackTrace()
                emit(Resource.createError(it))
            }
    }
}
