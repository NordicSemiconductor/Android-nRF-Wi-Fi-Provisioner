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

package com.nordicsemi.wifi.provisioner.library

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import com.nordicsemi.wifi.provisioner.library.domain.*
import com.nordicsemi.wifi.provisioner.library.internal.ProvisionerBleManager
import kotlinx.coroutines.flow.*
import no.nordicsemi.android.logger.LoggerAppRunner
import no.nordicsemi.android.logger.NordicLogger
import okio.ByteString.Companion.toByteString

class ProvisionerRepositoryImpl internal constructor(
    private val context: Context,
    private val appRunner: LoggerAppRunner
) : ProvisionerRepository {

    private var manager: ProvisionerBleManager? = null

    @SuppressLint("MissingPermission")
    override suspend fun start(device: BluetoothDevice) {
        manager = ProvisionerFactory.createBleManager(context, device)
        manager?.start(device)
        device.createBond()
    }

    override fun readVersion(): Flow<Resource<VersionDomain>> {
        return runTask { VersionDomain(manager?.getVersion()!!.toByteString().hex()) }
    }

    override fun getStatus(): Flow<Resource<DeviceStatusDomain>> {
        return runTask {
            val status = manager?.getStatus()
            status?.toDomain()!!
        }
    }

    suspend fun scan() {
        manager?.scan()
    }

    override fun startScan(): Flow<Resource<ScanRecordDomain>> {
        return manager?.startScan()!!
            .map { Resource.createSuccess(it.toDomain()) }
            .onStart { emit(Resource.createLoading()) }
            .catch {
                it.printStackTrace()
                emit(Resource.createError(it))
            }
    }

    override fun stopScan(): Flow<Resource<Unit>> {
        return runTask { manager?.stopScan() }
    }

    override suspend fun stopScanBlocking() {
        manager?.stopScan()
    }

    override fun setConfig(config: WifiConfigDomain): Flow<Resource<WifiConnectionStateDomain>> {
        return manager?.provision(config.toApi())!!
            .map { Resource.createSuccess(it.toDomain()) }
            .onStart { emit(Resource.createLoading()) }
            .catch {
                it.printStackTrace()
                emit(Resource.createError(it))
            }
    }

    override fun forgetConfig(): Flow<Resource<Unit>> {
        return runTask { manager?.forgetWifi() }
    }

    override suspend fun release() {
        manager?.release()
        manager = null
    }

    override fun openLogger() {
        manager?.openLogger() ?: appRunner.runLogger()
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

internal object ProvisionerFactory {

    fun createTestRepository() = TestProvisionerRepository()

    fun createRepository(context: Context): ProvisionerRepositoryImpl {
        return ProvisionerRepositoryImpl(context, LoggerAppRunner(context))
    }

    fun createBleManager(context: Context, device: BluetoothDevice): ProvisionerBleManager {
        return ProvisionerBleManager(context, createNordicLogger(context, device.address))
    }

    private fun createNordicLogger(context: Context, address: String): NordicLogger {
        return NordicLogger(context, LoggerAppRunner(context), "Wi-Fi", "Provisioner", address)
    }
}
