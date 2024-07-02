/*
 * Copyright (c) 2024, Nordic Semiconductor
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

package no.nordicsemi.android.wifi.provisioner.nfc

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import no.nordicsemi.android.wifi.provisioner.nfc.domain.AuthenticationMode
import no.nordicsemi.android.wifi.provisioner.nfc.domain.EncryptionMode
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiData
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.AUTH_TYPE_EXPECTED_SIZE
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.AUTH_TYPE_FIELD_ID
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.AUTH_TYPE_OPEN
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.AUTH_TYPE_SHARED
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.AUTH_TYPE_WPA2_EAP
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.AUTH_TYPE_WPA2_PSK
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.AUTH_TYPE_WPA_EAP
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.AUTH_TYPE_WPA_PSK
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.AUTH_TYPE_WPA_WPA2_PSK
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.CREDENTIAL_FIELD_ID
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.ENC_TYPE_AES
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.ENC_TYPE_AES_TKIP
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.ENC_TYPE_FIELD_ID
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.ENC_TYPE_NONE
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.ENC_TYPE_TKIP
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.ENC_TYPE_WEP
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.MAC_ADDRESS_FIELD_ID
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.MAX_MAC_ADDRESS_SIZE_BYTES
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.NETWORK_KEY_FIELD_ID
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.NFC_TOKEN_MIME_TYPE
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.SSID_FIELD_ID
import java.nio.ByteBuffer
import java.nio.charset.Charset

/**
 * This class is responsible for creating the NDEF message for a Wi-Fi data.
 */
class NdefMessageBuilder {

    /**
     * Creates the NDEF message for the given Wi-Fi data.
     *
     * @param wifiNetwork the Wi-Fi data to be written to the NDEF message.
     * @return the NDEF message for the Wi-Fi data.
     */
    fun createNdefMessage(wifiNetwork: WifiData): NdefMessage {
        val record = createWifiRecord(wifiNetwork)
        return NdefMessage(arrayOf(record))
    }

    /**
     * Generates the NDEF record for the given Wi-Fi network.
     *
     * @param wifiNetwork the Wi-Fi network to be written to the NDEF message.
     */
    fun createWifiRecord(wifiNetwork: WifiData): NdefRecord {
        val payload: ByteArray = generateNdefPayload(wifiNetwork)
        val empty = byteArrayOf()

        return NdefRecord(
            NdefRecord.TNF_MIME_MEDIA,
            NFC_TOKEN_MIME_TYPE.toByteArray(Charset.forName("US-ASCII")),
            empty,
            payload
        )
    }

    /**
     * Generates the NDEF payload for the given Wi-Fi network.
     *
     * @param wifiNetwork the Wi-Fi network to be written to the NDEF message.
     */
    private fun generateNdefPayload(wifiNetwork: WifiData): ByteArray {
        val ssid: String = wifiNetwork.ssid
        val ssidSize = ssid.toByteArray().size.toShort()
        val authType: Short = getAuthBytes(wifiNetwork.authType)
        val networkKey: String = wifiNetwork.password ?: ""
        val networkKeySize = networkKey.toByteArray().size.toShort()
        val encType = getEncByte(wifiNetwork.encryptionMode)

        val macAddressBufferSize = if (wifiNetwork.macAddress != null) 10 else 0
        /* Fill buffer */
        // size of required credential attributes
        val bufferSize = 24 + ssidSize + networkKeySize + macAddressBufferSize

        // Create a buffer with the required size
        val buffer = ByteBuffer.allocate(bufferSize)
        buffer.putShort(CREDENTIAL_FIELD_ID)
        buffer.putShort((bufferSize - 4).toShort())

        // Add the SSID
        buffer.putShort(SSID_FIELD_ID)
        buffer.putShort(ssidSize)
        buffer.put(ssid.toByteArray())

        // Add authentication type
        buffer.putShort(AUTH_TYPE_FIELD_ID)
        buffer.putShort(AUTH_TYPE_EXPECTED_SIZE)
        buffer.putShort(authType)

        // Add encryption type
        buffer.putShort(ENC_TYPE_FIELD_ID)
        buffer.putShort(2.toShort())
        buffer.putShort(encType)

        // Add network key / password
        buffer.putShort(NETWORK_KEY_FIELD_ID)
        buffer.putShort(networkKeySize)
        buffer.put(networkKey.toByteArray())

        // Add MAC address if available
        wifiNetwork.macAddress?.let { address ->
            // Convert the MAC address string to a ByteArray
            val macAddress = address.split(":")
                .map { it.toInt(16).toByte() }
                .toByteArray()

            // Add the MAC address
            buffer.putShort(MAC_ADDRESS_FIELD_ID)
            buffer.putShort(MAX_MAC_ADDRESS_SIZE_BYTES)
            buffer.put(macAddress)
        }

        return buffer.array()
    }

    /**
     * Returns the encryption type in bytes.
     *
     * @param enc the encryption type.
     */
    private fun getEncByte(enc: EncryptionMode): Short {
        return when (enc) {
            EncryptionMode.NONE -> ENC_TYPE_NONE
            EncryptionMode.WEP -> ENC_TYPE_WEP
            EncryptionMode.TKIP -> ENC_TYPE_TKIP
            EncryptionMode.AES -> ENC_TYPE_AES
            EncryptionMode.AES_TKIP -> ENC_TYPE_AES_TKIP
        }
    }

    /**
     * Returns the authentication type in bytes.
     *
     * @param auth the authentication type.
     */
    private fun getAuthBytes(auth: AuthenticationMode): Short {
        return when (auth) {
            AuthenticationMode.WEP -> AUTH_TYPE_SHARED
            AuthenticationMode.WPA_PSK -> AUTH_TYPE_WPA_PSK
            AuthenticationMode.WPA_EAP -> AUTH_TYPE_WPA_EAP
            AuthenticationMode.WPA2_PSK -> AUTH_TYPE_WPA2_PSK
            AuthenticationMode.WPA_WPA2_PSK -> AUTH_TYPE_WPA_WPA2_PSK
            AuthenticationMode.WPA2_EAP -> AUTH_TYPE_WPA2_EAP
            else -> AUTH_TYPE_OPEN
        }
    }
}