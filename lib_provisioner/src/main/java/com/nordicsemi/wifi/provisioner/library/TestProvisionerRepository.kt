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
import com.nordicsemi.wifi.provisioner.library.domain.*
import com.nordicsemi.wifi.provisioner.library.internal.ConnectionStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val DELAY_TIME = 1000L

class TestProvisionerRepository : ProvisionerRepository {

    override suspend fun start(device: BluetoothDevice): Flow<ConnectionStatus> {
        return flow {
            emit(ConnectionStatus.CONNECTED)
            delay(5000)
            emit(ConnectionStatus.DISCONNECTED)
        }
    }

    override fun readVersion(): Flow<Resource<VersionDomain>> {
        return flow {
            emit(Resource.createLoading())
            delay(DELAY_TIME)
//            emit(Resource.createError(Exception("Error message.")))
            emit(Resource.createSuccess(VersionDomain(1234)))
        }
    }

    override fun getStatus(): Flow<Resource<DeviceStatusDomain>> {
        return flow {
            emit(Resource.createLoading())
            delay(DELAY_TIME)
//            emit(Resource.createError(Exception("Error message.")))
            emit(Resource.createSuccess(createDeviceStatus()))
        }
    }

    override fun startScan(): Flow<Resource<ScanRecordDomain>> {
        return flow {
            emit(Resource.createLoading())
            delay(DELAY_TIME)
//            emit(Resource.createError(Exception("Error message.")))

            emit(Resource.createSuccess(createScanRecord(-100)))
            delay(DELAY_TIME)
            emit(Resource.createSuccess(createScanRecord(-50)))
            delay(DELAY_TIME)
            emit(Resource.createSuccess(createScanRecord(-80)))
            delay(DELAY_TIME)
            emit(Resource.createSuccess(createScanRecord(-100)))
            delay(DELAY_TIME)
            emit(Resource.createSuccess(createScanRecord(-50)))
            delay(DELAY_TIME)
            emit(Resource.createSuccess(createScanRecord(-80)))
        }
    }

    override fun stopScan(): Flow<Resource<Unit>> {
        return flow {
            emit(Resource.createLoading())
            delay(DELAY_TIME)
            emit(Resource.createSuccess(Unit))
        }
    }

    override suspend fun stopScanBlocking() {

    }

    override fun setConfig(config: WifiConfigDomain): Flow<Resource<WifiConnectionStateDomain>> {
        return flow {
            emit(Resource.createLoading())
            delay(DELAY_TIME)
//            emit(Resource.createError(Exception("Error message.")))

            emit(Resource.createSuccess(WifiConnectionStateDomain.AUTHENTICATION))
            delay(DELAY_TIME)


            emit(Resource.createSuccess(WifiConnectionStateDomain.ASSOCIATION))
            delay(DELAY_TIME)

            emit(Resource.createSuccess(WifiConnectionStateDomain.OBTAINING_IP))
            delay(DELAY_TIME)

            emit(Resource.createError(Exception("Error message.")))
//            emit(Resource.createSuccess(WifiConnectionStateDomain.CONNECTED))
        }
    }

    override fun forgetConfig(): Flow<Resource<Unit>> {
        return flow {
            emit(Resource.createLoading())
            delay(DELAY_TIME)
            emit(Resource.createSuccess(Unit))
        }
    }

    override suspend fun release() {

    }

    override fun openLogger() {

    }

    private fun createDeviceStatus(): DeviceStatusDomain {
        return DeviceStatusDomain(
            wifiState = WifiConnectionStateDomain.DISCONNECTED,
            wifiInfo = createWifiInfo(-66),
            connectionInfo = ConnectionInfoDomain(ipv4Address = "11:22:33:44:55"),
            scanParams = ScanParamsDomain(
                band = BandDomain.BAND_2_4_GH,
                passive = true,
                periodMs = 23,
                groupChannels = 4
            )
        )
    }

    private fun createScanRecord(rssi: Int): ScanRecordDomain {
        return ScanRecordDomain(
            rssi = rssi,
            wifiInfo = createWifiInfo(rssi)
        )
    }

    private fun createWifiInfo(rssi: Int) = WifiInfoDomain("Good Wifi", "bssid", BandDomain.BAND_2_4_GH, 1, AuthModeDomain.WEP)
}
