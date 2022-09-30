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

package no.nordicsemi.wifi.provisioner.library

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import no.nordicsemi.android.common.logger.NordicLogger
import no.nordicsemi.wifi.provisioner.library.domain.*
import no.nordicsemi.wifi.provisioner.library.internal.ProvisionerBleManager

internal class ProvisionerRepositoryImpl internal constructor(
    private val context: Context
) : ProvisionerRepository {

    private var manager: ProvisionerBleManager? = null

    @SuppressLint("MissingPermission")
    override suspend fun start(device: BluetoothDevice): Flow<ConnectionStatus> {
        return ProvisionerFactory.createBleManager(context, device)
            .apply { manager = this }
            .start(device)
    }

    override suspend fun readVersion(): VersionDomain {
        return VersionDomain(manager!!.getVersion().version)
    }

    override suspend fun getStatus(): DeviceStatusDomain {
        return manager!!.getStatus().toDomain()
    }

    override fun startScan(): Flow<ScanRecordDomain> {
        return manager!!.startScan().map { it.toDomain() }
    }

    override suspend fun stopScan() {
        manager!!.stopScan()
    }

    override fun setConfig(config: WifiConfigDomain): Flow<WifiConnectionStateDomain> {
        return manager!!.provision(config.toApi()).map { it.toDomain() }
    }

    override suspend fun forgetConfig() {
        manager!!.forgetWifi()
    }

    override suspend fun release() {
        manager?.release()
        manager = null
    }

    override fun openLogger() {
        NordicLogger.launch(context, manager?.logger)
    }

//    private fun <T> runTask(block: suspend () -> T): Flow<Resource<T>> {
//        return flow { emit(Resource.createSuccess(block())) }
//            .onStart { emit(Resource.createLoading()) }
//            .catch {
//                it.printStackTrace()
//                emit(Resource.createError(it))
//            }
//    }
}

internal object ProvisionerFactory {

    fun createRepository(context: Context): ProvisionerRepositoryImpl {
        return ProvisionerRepositoryImpl(context)
    }

    fun createBleManager(context: Context, device: BluetoothDevice): ProvisionerBleManager {
        return ProvisionerBleManager(context, createNordicLogger(context, device.address))
    }

    private fun createNordicLogger(context: Context, address: String): NordicLogger {
        return NordicLogger(context, "Wi-Fi", address, null)
    }
}
