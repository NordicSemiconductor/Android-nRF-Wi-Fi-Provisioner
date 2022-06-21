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

import android.bluetooth.BluetoothDevice
import android.content.Context
import com.nordicsemi.wifi.provisioner.library.internal.ProvisionerBleManager
import kotlinx.coroutines.CoroutineScope
import no.nordicsemi.android.ble.ktx.state
import no.nordicsemi.android.logger.LoggerAppRunner
import no.nordicsemi.android.logger.NordicLogger

class ProvisionerRepository internal constructor(
    private val context: Context
) {

    private var manager: ProvisionerBleManager? = null

    suspend fun start(device: BluetoothDevice, scope: CoroutineScope) {
        manager = ProvisionerFactory.createBleManager(context, device)
        manager?.start(device)
    }

    suspend fun getStatus() {
        manager?.getStatus()
    }

    suspend fun startScan() {
        manager?.startScan()
    }

    suspend fun stopScan() {
        manager?.stopScan()
    }

    suspend fun setConfig() {
        manager?.provision()
    }

    suspend fun forgetConfig() {
        manager?.forgetWifi()
    }

    suspend fun release() {
        manager?.release()
        manager = null
    }

    companion object {
        fun newInstance(context: Context): ProvisionerRepository {
            return ProvisionerFactory.createRepository(context)
        }
    }
}

internal object ProvisionerFactory {

    fun createRepository(context: Context): ProvisionerRepository {
        return ProvisionerRepository(context)
    }

    fun createBleManager(context: Context, device: BluetoothDevice): ProvisionerBleManager {
        return ProvisionerBleManager(context, createNordicLogger(context, device.address))
    }

    private fun createNordicLogger(context: Context, address: String): NordicLogger {
        return NordicLogger(context, LoggerAppRunner(context), "Wi-Fi", "Provisioner", address)
    }
}
