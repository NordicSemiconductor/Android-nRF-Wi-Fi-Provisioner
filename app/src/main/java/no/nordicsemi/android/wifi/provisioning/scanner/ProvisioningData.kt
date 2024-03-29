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

package no.nordicsemi.android.wifi.provisioning.scanner

import android.os.ParcelUuid
import no.nordicsemi.android.common.ui.scanner.model.DiscoveredBluetoothDevice
import no.nordicsemi.android.support.v18.scanner.ScanResult
import java.util.*

internal data class ProvisioningData(
    val version: Int,
    val isProvisioned: Boolean,
    val isConnected: Boolean,
    val rssi: Int
) {
    constructor(data: ByteArray) : this(
        version = data[0].toInt(),
        // 2 bytes reserved for flags
        isProvisioned = data[1].toInt() and 0x01 != 0,
        isConnected = data[1].toInt() and 0x02 != 0,
        rssi = data[3].toInt()
    )

    companion object {
        private val parcelUuid = ParcelUuid(UUID.fromString("14387800-130c-49e7-b877-2881c89cb258"))

        fun create(scanResult: ScanResult) = scanResult.scanRecord?.serviceData
            ?.get(parcelUuid)
            ?.let { ProvisioningData(it) }
    }
}

internal fun DiscoveredBluetoothDevice.provisioningData(): ProvisioningData? {
    return scanResult?.let { ProvisioningData.create(it) }
}
