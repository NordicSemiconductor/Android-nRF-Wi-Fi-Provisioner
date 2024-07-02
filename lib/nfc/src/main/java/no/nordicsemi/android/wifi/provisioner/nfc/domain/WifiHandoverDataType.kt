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

package no.nordicsemi.android.wifi.provisioner.nfc.domain

/**
 * WifiHandoverDataType is an object that holds the data types for the Wi-Fi handover data.
 */
internal object WifiHandoverDataType {

    /**
     * The MIME type for the Wi-Fi Simple Configuration Token.
     */
    const val NFC_TOKEN_MIME_TYPE: String = "application/vnd.wfa.wsc"

    /**
     * The Credential field ID.
     */
    const val CREDENTIAL_FIELD_ID: Short = 0x100e

    /**
     * The Network Index field ID.
     */
    const val NETWORK_INDEX_FIELD_ID: Short = 0x1026
    const val NETWORK_INDEX_DEFAULT_VALUE: Byte = 0x01.toByte()

    /**
     * The SSID field ID.
     */
    const val SSID_FIELD_ID: Short = 0x1045
    const val MAX_SSID_SIZE_BYTES: Int = 32

    /**
     * The Encryption Type field ID and encryption types.
     */
    const val ENC_TYPE_FIELD_ID: Short = 0x100f
    const val ENC_TYPE_NONE: Short = 0x0001
    const val ENC_TYPE_WEP: Short = 0x0002 // deprecated
    const val ENC_TYPE_TKIP: Short = 0x0004 // deprecated -> only with mixed mode (0x000c)
    const val ENC_TYPE_AES: Short = 0x0008 // includes CCMP and GCMP
    const val ENC_TYPE_AES_TKIP: Short = 0x000c // mixed mode

    /**
     * The Authentication Type field ID and authentication types.
     */
    const val AUTH_TYPE_FIELD_ID: Short = 0x1003
    const val AUTH_TYPE_EXPECTED_SIZE: Short = 2
    const val AUTH_TYPE_OPEN: Short = 0x0001
    const val AUTH_TYPE_WPA_PSK: Short = 0x0002
    const val AUTH_TYPE_WPA_EAP: Short = 0x0008
    const val AUTH_TYPE_WPA2_EAP: Short = 0x0010
    const val AUTH_TYPE_WPA2_PSK: Short = 0x0020
    const val AUTH_TYPE_WPA_WPA2_PSK: Short = 0x0022
    const val AUTH_TYPE_SHARED: Short = 0x0004 // deprecated "WEP" type

    /**
     * The Network key (wifi password) field ID.
     */
    const val NETWORK_KEY_FIELD_ID: Short = 0x1027
    const val MAX_NETWORK_KEY_SIZE_BYTES: Int = 64

    /**
     * The MAC Address field ID.
     */
    const val MAC_ADDRESS_FIELD_ID: Short = 0x1020
    const val MAX_MAC_ADDRESS_SIZE_BYTES: Short = 6
}
