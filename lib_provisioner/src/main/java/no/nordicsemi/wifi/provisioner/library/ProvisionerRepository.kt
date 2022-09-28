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
import no.nordicsemi.wifi.provisioner.library.domain.DeviceStatusDomain
import no.nordicsemi.wifi.provisioner.library.domain.ScanRecordDomain
import no.nordicsemi.wifi.provisioner.library.domain.VersionDomain
import no.nordicsemi.wifi.provisioner.library.domain.WifiConfigDomain
import no.nordicsemi.wifi.provisioner.library.domain.WifiConnectionStateDomain
import no.nordicsemi.wifi.provisioner.library.internal.ConnectionStatus
import kotlinx.coroutines.flow.Flow

/**
 * Class responsible for establishing connection and maintaining communication with a nRF 7 device.
 *
 * It has several methods which allows for sending Wi-Fi credentials to an IoT device.
 * The typical flow contains:
 * 1. [start] - Connecting to the device.
 * 2. [readVersion] & [getStatus] - Obtaining the device's version and status.
 * 3. [startScan] - Send START_SCAN command to the device and obtain Wi-Fi list.
 * 4. [stopScan] - After getting desired result the scanning should be stopped.
 * 4. [setConfig] - After selecting Wi-Fi and providing password, a provisioning data should be send to the DK.
 * 5. Observe connection status and eventually repeat step 4 if the password was wrong.
 *
 * The device can be unprovisioned if Status returns Wi-Fi info by calling [forgetConfig].
 *
 * The connection should be closed after the job is done by calling [release].
 *
 * @constructor [ProvisionerRepository.newInstance]
 */
interface ProvisionerRepository {

    /**
     * Connects to selected device.
     *
     * @param device[BluetoothDevice] to which the app should connect
     * @return [Flow] which emits connectivity status changes
     */
    suspend fun start(device: BluetoothDevice): Flow<ConnectionStatus>

    /**
     * Read the current version.
     *
     * @return [Flow] of type [Resource] which emit 2 states: [Loading] and [Success]/[Error]
     */
    fun readVersion(): Flow<Resource<VersionDomain>>

    /**
     * Read device status info from the DK.
     * It can contain information about provisioning data, connection status and Wi-Fi scanning status and params.
     *
     * @return [Flow] of type [Resource] which emit 2 states: [Loading] and [Success]/[Error]
     */
    fun getStatus(): Flow<Resource<DeviceStatusDomain>>

    /**
     * Start scanning and obtains available Wi-Fi list.
     *
     * @return [Flow] of type [Resource]. Starts with [Loading] and can emit multiple [Success]/[Error]
     */
    fun startScan(): Flow<Resource<ScanRecordDomain>>

    /**
     * Stop scanning for available Wi-Fi's. Should be called after [startScan].
     *
     * @return [Flow] of type [Resource] which emit 2 states: [Loading] and [Success]/[Error]
     */
    fun stopScan(): Flow<Resource<Unit>>

    /**
     * Stop scanning for available Wi-Fi's. Should be called after [startScan].
     *
     * Like [stopScan], but it suspends and wait for the result.
     */
    suspend fun stopScanBlocking()

    /**
     * Provision the connected DK with data obtained from [startScan] + password.
     *
     * @return [Flow] of type [Resource]. Starts with [Loading] and emits multiple [Success] with Connection status updates.
     */
    fun setConfig(config: WifiConfigDomain): Flow<Resource<WifiConnectionStateDomain>>

    /**
     * Unprovision the DK - forget slected SSID, password, etc.
     *
     * @return [Flow] of type [Resource] which emit 2 states: [Loading] and [Success]/[Error]
     */
    fun forgetConfig(): Flow<Resource<Unit>>

    /**
     * Closes connection with the DK.
     */
    suspend fun release()

    /**
     * Opens nRF Logger app.
     */
    fun openLogger()

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: ProvisionerRepository? = null

        /**
         * Creates new instance of [ProvisionerRepository]
         *
         * @param context - the application context
         */
        fun newInstance(context: Context): ProvisionerRepository {
            val app = context.applicationContext
            val newInstance = instance ?: ProvisionerFactory.createRepository(app)
//            val newInstance = instance ?: ProvisionerFactory.createTestRepository()
            instance = newInstance
            return newInstance
        }

        fun instance(): ProvisionerRepository = instance!! //TODO
    }
}
