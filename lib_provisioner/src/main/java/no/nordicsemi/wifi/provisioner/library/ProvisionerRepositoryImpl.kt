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

private const val MANAGER_NOT_INITIALIZED = "Manager not initialized. Call start(BluetoothDevice) first."

internal class ProvisionerRepositoryImpl internal constructor(
    private val context: Context
) : ProvisionerRepository {

    private var manager: ProvisionerBleManager? = null

    @SuppressLint("MissingPermission")
    override suspend fun start(device: BluetoothDevice): Flow<ConnectionStatus> =
        ProvisionerFactory.createBleManager(context, device)
            .apply { manager = this }
            .start(device)

    override suspend fun readVersion(): VersionDomain = manager?.getVersion()?.toDomain()
        ?: throw IllegalStateException(MANAGER_NOT_INITIALIZED)

    override suspend fun getStatus(): DeviceStatusDomain = manager?.getStatus()?.toDomain()
        ?: throw IllegalStateException(MANAGER_NOT_INITIALIZED)

    override fun startScan(): Flow<ScanRecordDomain> = manager?.startScan()?.map { it.toDomain() }
        ?: throw IllegalStateException(MANAGER_NOT_INITIALIZED)

    override suspend fun stopScan(): Unit = manager?.stopScan()
        ?: throw IllegalStateException(MANAGER_NOT_INITIALIZED)

    override fun setConfig(config: WifiConfigDomain): Flow<WifiConnectionStateDomain> =
        manager?.provision(config.toApi())
            ?.map { it.toDomain() }
            ?: throw IllegalStateException(MANAGER_NOT_INITIALIZED)

    override suspend fun forgetConfig(): Unit = manager?.forgetWifi()
        ?: throw IllegalStateException(MANAGER_NOT_INITIALIZED)

    override suspend fun release() {
        manager?.release()
        manager = null
    }

    override fun openLogger() {
        NordicLogger.launch(context, manager?.logger)
    }
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
