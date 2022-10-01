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
package no.nordicsemi.wifi.provisioner.library.internal

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.util.Log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.ktx.*
import no.nordicsemi.android.ble.ktx.state.ConnectionState
import no.nordicsemi.android.common.logger.NordicLogger
import no.nordicsemi.android.wifi.provisioning.*
import no.nordicsemi.wifi.provisioner.library.ConnectionStatus
import no.nordicsemi.wifi.provisioner.library.exception.NotificationTimeoutException
import no.nordicsemi.wifi.provisioner.library.exception.ResponseError
import no.nordicsemi.wifi.provisioner.library.exception.ResponseErrorException
import no.nordicsemi.wifi.provisioner.library.internal.response.InfoPacket
import no.nordicsemi.wifi.provisioner.library.internal.response.ResponsePacket
import no.nordicsemi.wifi.provisioner.library.internal.response.ResultPacket
import java.util.*

val PROVISIONING_SERVICE_UUID: UUID = UUID.fromString("14387800-130c-49e7-b877-2881c89cb258")
private val VERSION_CHARACTERISTIC_UUID = UUID.fromString("14387801-130c-49e7-b877-2881c89cb258")
private val CONTROL_POINT_CHARACTERISTIC_UUID =
    UUID.fromString("14387802-130c-49e7-b877-2881c89cb258")
private val DATA_OUT_CHARACTERISTIC_UUID = UUID.fromString("14387803-130c-49e7-b877-2881c89cb258")

private const val TIMEOUT_MILLIS = 60_000L

internal class ProvisionerBleManager(
    context: Context,
    val logger: NordicLogger
) : BleManager(context) {

    private var versionCharacteristic: BluetoothGattCharacteristic? = null
    private var controlPointCharacteristic: BluetoothGattCharacteristic? = null
    private var dataOutCharacteristic: BluetoothGattCharacteristic? = null

    fun openLogger() {
        NordicLogger.launch(context, logger)
    }

    override fun log(priority: Int, message: String) {
        logger.log(priority, message)
    }

    override fun getMinLogPriority(): Int {
        return Log.VERBOSE
    }

    @SuppressLint("MissingPermission")
    suspend fun start(device: BluetoothDevice): Flow<ConnectionStatus> {
        return try {
            connect(device)
                .useAutoConnect(false)
                .retry(3, 100)
                .suspend()
            createBondInsecure()
                .suspend()
            stateAsFlow().map {
                when (it) {
                    ConnectionState.Connecting -> ConnectionStatus.CONNECTING
                    ConnectionState.Initializing -> ConnectionStatus.CONNECTED
                    ConnectionState.Ready -> ConnectionStatus.SUCCESS
                    ConnectionState.Disconnecting -> ConnectionStatus.DISCONNECTING
                    is ConnectionState.Disconnected -> {
                        when (it.reason) {
                            ConnectionState.Disconnected.Reason.SUCCESS -> ConnectionStatus.DISCONNECTED
                            ConnectionState.Disconnected.Reason.LINK_LOSS -> ConnectionStatus.LINK_LOSS
                            ConnectionState.Disconnected.Reason.NOT_SUPPORTED -> ConnectionStatus.MISSING_SERVICE
                            else -> ConnectionStatus.UNKNOWN_ERROR
                        }
                        ConnectionStatus.DISCONNECTED
                    }

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            flow { emit(ConnectionStatus.DISCONNECTED) }
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun release() {
        removeBond().suspend()
        disconnect().suspend()
    }

    private inner class ProvisioningManagerGattCallback : BleManagerGattCallback() {

        @SuppressLint("WrongConstant")
        override fun initialize() {
            requestMtu(512).enqueue()
            enableIndications(controlPointCharacteristic).enqueue()
            enableNotifications(dataOutCharacteristic).enqueue()
        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            gatt.getService(PROVISIONING_SERVICE_UUID)?.apply {
                versionCharacteristic = getCharacteristic(VERSION_CHARACTERISTIC_UUID,
                    BluetoothGattCharacteristic.PROPERTY_READ)
                controlPointCharacteristic = getCharacteristic(CONTROL_POINT_CHARACTERISTIC_UUID,
                    BluetoothGattCharacteristic.PROPERTY_WRITE or BluetoothGattCharacteristic.PROPERTY_INDICATE)
                dataOutCharacteristic = getCharacteristic(DATA_OUT_CHARACTERISTIC_UUID,
                    BluetoothGattCharacteristic.PROPERTY_NOTIFY)
            }

            return versionCharacteristic != null &&
                   controlPointCharacteristic != null &&
                   dataOutCharacteristic != null
        }

        override fun onServicesInvalidated() {
            versionCharacteristic = null
            controlPointCharacteristic = null
            dataOutCharacteristic = null
        }
    }

    suspend fun getVersion(): Info = withTimeout(TIMEOUT_MILLIS) {
        readCharacteristic(versionCharacteristic)
            .suspendForValidResponse<InfoPacket>()
            .value
    }

    suspend fun getStatus(): DeviceStatus = withTimeout(TIMEOUT_MILLIS) {
        val request = Request(op_code = OpCode.GET_STATUS)

        val response = waitForIndication(controlPointCharacteristic)
            .trigger(
                writeCharacteristic(
                    controlPointCharacteristic,
                    request.encode(),
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                )
            )
            .suspendForValidResponse<ResponsePacket>()

        verifyResponseSuccess(response)

        response.value.device_status ?: throw IllegalStateException("Device status is null")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun startScan() = callbackFlow {
        val request = Request(op_code = OpCode.START_SCAN)

        val timeoutJob = launch {
            delay(TIMEOUT_MILLIS)
            throw NotificationTimeoutException()
        }

        setNotificationCallback(dataOutCharacteristic)
            .asValidResponseFlow<ResultPacket>()
            .onEach {
                timeoutJob.cancel()
                trySend(it.value.scan_record!!)
            }
            .launchIn(this)

        val response = waitForIndication(controlPointCharacteristic)
            .trigger(
                writeCharacteristic(
                    controlPointCharacteristic,
                    request.encode(),
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                )
            )
            .suspendForValidResponse<ResponsePacket>()

        verifyResponseSuccess(response)

        awaitClose {
            removeNotificationCallback(dataOutCharacteristic)
        }
    }

    suspend fun stopScan() {
        val request = Request(op_code = OpCode.STOP_SCAN)
        val response = waitForIndication(controlPointCharacteristic)
            .trigger(
                writeCharacteristic(
                    controlPointCharacteristic,
                    request.encode(),
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                )
            )
            .suspendForValidResponse<ResponsePacket>()

        verifyResponseSuccess(response)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun provision(config: WifiConfig) = callbackFlow {
        val request = Request(op_code = OpCode.SET_CONFIG, config = config)

        val timeoutJob = launch {
            delay(TIMEOUT_MILLIS)
            throw NotificationTimeoutException()
        }

        setNotificationCallback(dataOutCharacteristic)
            .asValidResponseFlow<ResultPacket>()
            .onEach {
                timeoutJob.cancel()
                trySend(it.value.state!!)
            }.launchIn(this)

        val response = waitForIndication(controlPointCharacteristic)
            .trigger(
                writeCharacteristic(
                    controlPointCharacteristic,
                    request.encode(),
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                )
            )
            .suspendForValidResponse<ResponsePacket>()

        verifyResponseSuccess(response)

        awaitClose {
            removeNotificationCallback(dataOutCharacteristic)
        }
    }

    suspend fun forgetWifi() {
        val request = Request(op_code = OpCode.FORGET_CONFIG)
        val response = waitForIndication(controlPointCharacteristic)
            .trigger(
                writeCharacteristic(
                    controlPointCharacteristic,
                    request.encode(),
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                )
            )
            .suspendForValidResponse<ResponsePacket>()

        verifyResponseSuccess(response)
    }

    private fun verifyResponseSuccess(response: ResponsePacket) {
        response.value.status
            .takeUnless { it == Status.SUCCESS }
            ?.let { throw createResponseError(it) }
    }

    private fun createResponseError(status: Status): Exception {
        val errorCode = when (status) {
            Status.INVALID_ARGUMENT -> ResponseError.INVALID_ARGUMENT
            Status.INVALID_PROTO -> ResponseError.INVALID_PROTO
            Status.INTERNAL_ERROR -> ResponseError.INTERNAL_ERROR
            Status.SUCCESS -> throw IllegalArgumentException("Error code should not be success.")
        }
        return ResponseErrorException(errorCode)
    }

    override fun getGattCallback(): BleManagerGattCallback {
        return ProvisioningManagerGattCallback()
    }
}
