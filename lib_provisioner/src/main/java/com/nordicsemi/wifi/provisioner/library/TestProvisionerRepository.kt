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
import com.nordicsemi.wifi.provisioner.library.domain.DeviceStatusDomain
import com.nordicsemi.wifi.provisioner.library.domain.ScanRecordDomain
import com.nordicsemi.wifi.provisioner.library.domain.WifiConnectionStateDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TestProvisionerRepository : ProvisionerRepository {
    override suspend fun start(device: BluetoothDevice) {

    }

    override fun readVersion(): Flow<Resource<String>> {
        return flow {

        }
    }

    override fun getStatus(): Flow<Resource<DeviceStatusDomain>> {
        TODO("Not yet implemented")
    }

    override fun startScan(): Flow<Resource<ScanRecordDomain>> {
        TODO("Not yet implemented")
    }

    override fun stopScan(): Flow<Resource<Unit>> {
        TODO("Not yet implemented")
    }

    override fun setConfig(): Flow<Resource<WifiConnectionStateDomain>> {
        TODO("Not yet implemented")
    }

    override fun forgetConfig(): Flow<Resource<Unit>> {
        TODO("Not yet implemented")
    }
}
