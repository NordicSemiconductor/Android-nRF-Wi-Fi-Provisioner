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
package com.nordicsemi.wifi.provisioner.library.internal

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.ktx.asValidResponseFlow
import no.nordicsemi.android.ble.ktx.suspend
import no.nordicsemi.android.ble.ktx.suspendForValidResponse
import no.nordicsemi.android.logger.NordicLogger
import no.nordicsemi.android.wifi.provisioning.*
import java.util.*

val PROVISIONING_SERVICE_UUID: UUID = UUID.fromString("14387800-130c-49e7-b877-2881c89cb258")
private val VERSION_CHARACTERISTIC_UUID = UUID.fromString("14387801-130c-49e7-b877-2881c89cb258")
private val CONTROL_POINT_CHARACTERISTIC_UUID = UUID.fromString("14387802-130c-49e7-b877-2881c89cb258")
private val DATA_OUT_CHARACTERISTIC_UUID = UUID.fromString("14387803-130c-49e7-b877-2881c89cb258")

internal class ProvisionerBleManager(
    context: Context,
    private val logger: NordicLogger
) : BleManager(context) {

    private var versionCharacteristic: BluetoothGattCharacteristic? = null
    private var controlPointCharacteristic: BluetoothGattCharacteristic? = null
    private var dataOutCharacteristic: BluetoothGattCharacteristic? = null

    private var useLongWrite = true

    val dataHolder = ConnectionObserverAdapter()

    init {
        connectionObserver = dataHolder
    }

    override fun log(priority: Int, message: String) {
        logger.log(priority, message)
    }

    override fun getMinLogPriority(): Int {
        return Log.VERBOSE
    }

    suspend fun start(device: BluetoothDevice) {
        try {
            connect(device)
                .useAutoConnect(false)
                .retry(3, 100)
                .suspend()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun release() {
        disconnect().suspend()
    }

    private inner class ProvisioningManagerGattCallback : BleManagerGattCallback() {

        @SuppressLint("WrongConstant")
        override fun initialize() {
            requestMtu(517).enqueue()
        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            val service: BluetoothGattService? = gatt.getService(PROVISIONING_SERVICE_UUID)
            if (service != null) {
                versionCharacteristic = service.getCharacteristic(VERSION_CHARACTERISTIC_UUID)
                controlPointCharacteristic = service.getCharacteristic(CONTROL_POINT_CHARACTERISTIC_UUID)
                dataOutCharacteristic = service.getCharacteristic(DATA_OUT_CHARACTERISTIC_UUID)
            }
            var writeRequest = false
            var writeCommand = false

            controlPointCharacteristic?.let {
                val rxProperties: Int = it.properties
                writeRequest = rxProperties and BluetoothGattCharacteristic.PROPERTY_WRITE > 0
                writeCommand =
                    rxProperties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE > 0

                // Set the WRITE REQUEST type when the characteristic supports it.
                // This will allow to send long write (also if the characteristic support it).
                // In case there is no WRITE REQUEST property, this manager will divide texts
                // longer then MTU-3 bytes into up to MTU-3 bytes chunks.
                if (!writeRequest) {
                    useLongWrite = false
                }
            }
            return versionCharacteristic != null && controlPointCharacteristic != null && dataOutCharacteristic != null && (writeRequest || writeCommand)
        }

        override fun onServicesInvalidated() {
            versionCharacteristic = null
            controlPointCharacteristic = null
            dataOutCharacteristic = null
            useLongWrite = true
        }
    }

    suspend fun getVersion(): String {
        val response = readCharacteristic(versionCharacteristic).suspendForValidResponse<ByteArrayReadResponse>().value
        return response.toString()
    }

    suspend fun getStatus(): DeviceStatus {
        val request = Request(op_code = OpCode.GET_STATUS)
        val response = waitForIndication(controlPointCharacteristic)
            .trigger(writeCharacteristic(controlPointCharacteristic, request.encode(), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT))
            .suspendForValidResponse<ByteArrayReadResponse>()

        verifyResponseSuccess(response.value)

        return Response.ADAPTER.decode(response.value).device_status!!
    }

    suspend fun startScan() = callbackFlow<ScanRecord> {
        val request = Request(op_code = OpCode.START_SCAN)
        val response = waitForIndication(controlPointCharacteristic)
            .trigger(writeCharacteristic(controlPointCharacteristic, request.encode(), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT))
            .suspendForValidResponse<ByteArrayReadResponse>()

        if (Response.ADAPTER.decode(response.value).status != Status.SUCCESS) {
            throw createResponseError()
        }

        setNotificationCallback(dataOutCharacteristic)
            .asValidResponseFlow<ByteArrayReadResponse>()
            .onEach {
                val result = Result.ADAPTER.decode(it.value)
                trySend(result.scan_record!!)
            }
            .launchIn(this)

        awaitClose {
            disableNotifications(dataOutCharacteristic)
        }
    }

    suspend fun stopScan() {
        val request = Request(op_code = OpCode.STOP_SCAN)
        val response = waitForIndication(controlPointCharacteristic)
            .trigger(writeCharacteristic(controlPointCharacteristic, request.encode(), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT))
            .suspendForValidResponse<ByteArrayReadResponse>()

        verifyResponseSuccess(response.value)
    }

    suspend fun provision() = callbackFlow<ConnectionState> {
        val request = Request(op_code = OpCode.SET_CONFIG)
        val response = waitForIndication(controlPointCharacteristic)
            .trigger(writeCharacteristic(controlPointCharacteristic, request.encode(), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT))
            .suspendForValidResponse<ByteArrayReadResponse>()

        if (Response.ADAPTER.decode(response.value).status != Status.SUCCESS) {
            throw createResponseError()
        }

        setNotificationCallback(dataOutCharacteristic)
            .asValidResponseFlow<ByteArrayReadResponse>()
            .onEach {
                val result = Result.ADAPTER.decode(it.value)
                trySend(result.state!!)
            }
            .launchIn(this)

        awaitClose {
            disableNotifications(dataOutCharacteristic)
        }
    }

    suspend fun forgetWifi() {
        val request = Request(op_code = OpCode.FORGET_CONFIG)
        val response = waitForIndication(controlPointCharacteristic)
            .trigger(writeCharacteristic(controlPointCharacteristic, request.encode(), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT))
            .suspendForValidResponse<ByteArrayReadResponse>()

        verifyResponseSuccess(response.value)
    }

    private fun verifyResponseSuccess(response: ByteArray) {
        if (Response.ADAPTER.decode(response).status != Status.SUCCESS) {
            throw createResponseError()
        }
    }

    private fun createResponseError() = IllegalArgumentException("Response should be success")

    override fun getGattCallback(): BleManagerGattCallback {
        return ProvisioningManagerGattCallback()
    }
}
