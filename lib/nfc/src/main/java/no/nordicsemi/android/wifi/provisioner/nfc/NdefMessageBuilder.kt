package no.nordicsemi.android.wifi.provisioner.nfc

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import no.nordicsemi.android.wifi.provisioner.nfc.domain.AuthenticationMode
import no.nordicsemi.android.wifi.provisioner.nfc.domain.EncryptionMode
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiAuthTypeBelowTiramisu
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiAuthTypeTiramisuOrAbove
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiData
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
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.MAX_MAC_ADDRESS_SIZE_BYTES
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.NETWORK_KEY_FIELD_ID
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.NFC_TOKEN_MIME_TYPE
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiHandoverDataType.SSID_FIELD_ID
import java.nio.ByteBuffer
import java.nio.charset.Charset

/**
 * This class is responsible for creating the NDEF message for the WiFi data.
 */
class NdefMessageBuilder {

    /**
     * Creates the NDEF message for the WiFi data.
     *
     * @param wifiNetwork the WiFi data to be written to the NDEF message.
     * @return the NDEF message for the WiFi data.
     */
    fun createNdefMessage(wifiNetwork: WifiData): NdefMessage {
        return generateNdefMessage(wifiNetwork)
    }

    /**
     * Generates the NDEF message for the given WiFi network.
     *
     * @param wifiNetwork the WiFi network to be written to the NDEF message.
     */
    private fun generateNdefMessage(wifiNetwork: WifiData): NdefMessage {
        val payload: ByteArray = generateNdefPayload(wifiNetwork)
        val empty = byteArrayOf()

        val mimeRecord = NdefRecord(
            NdefRecord.TNF_MIME_MEDIA,
            NFC_TOKEN_MIME_TYPE.toByteArray(Charset.forName("US-ASCII")),
            empty,
            payload
        )

        return NdefMessage(arrayOf(mimeRecord))
    }

    /**
     * Generates the NDEF payload for the given WiFi network.
     *
     * @param wifiNetwork the WiFi network to be written to the NDEF message.
     */
    private fun generateNdefPayload(wifiNetwork: WifiData): ByteArray {
        val ssid: String = wifiNetwork.ssid
        val ssidSize = ssid.toByteArray().size.toShort()
        val authType: Short = getAuthBytes(wifiNetwork.authType)
        val networkKey: String = wifiNetwork.password
        val networkKeySize = networkKey.toByteArray().size.toShort()

        val encType = getEncByte(wifiNetwork.encryptionMode)

        val macAddress = ByteArray(MAX_MAC_ADDRESS_SIZE_BYTES)
        for (i in 0 until MAX_MAC_ADDRESS_SIZE_BYTES) {
            macAddress[i] = 0xff.toByte()
        }

        /* Fill buffer */
        val bufferSize = 24 + ssidSize + networkKeySize // size of required credential attributes

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
        buffer.putShort(2.toShort())
        buffer.putShort(authType)

        // Add encryption type
        buffer.putShort(ENC_TYPE_FIELD_ID)
        buffer.putShort(2.toShort())
        buffer.putShort(encType)

        // Add network key / password
        buffer.putShort(NETWORK_KEY_FIELD_ID)
        buffer.putShort(networkKeySize)
        buffer.put(networkKey.toByteArray())

        return buffer.array()
    }

    /**
     * Returns the encryption type in bytes.
     *
     * @param enc the encryption type.
     */
    private fun getEncByte(enc: String): Short {

        // If Empty, default to AES
        if (enc.isEmpty()) {
            return ENC_TYPE_AES
        }
        return when (enc) {
            EncryptionMode.WEP.toString() -> ENC_TYPE_WEP
            EncryptionMode.TKIP.toString() -> ENC_TYPE_TKIP
            EncryptionMode.AES.toString() -> ENC_TYPE_AES
            EncryptionMode.AES_TKIP.toString() -> ENC_TYPE_AES_TKIP
            else -> ENC_TYPE_NONE
        }
    }

    /**
     * Returns the authentication type in bytes.
     *
     * @param auth the authentication type.
     */
    private fun getAuthBytes(auth: AuthenticationMode): Short {
        return when (auth) {
            WifiAuthTypeBelowTiramisu.WEP, WifiAuthTypeTiramisuOrAbove.WEP -> AUTH_TYPE_SHARED
            WifiAuthTypeBelowTiramisu.WPA_PSK, WifiAuthTypeTiramisuOrAbove.WPA_PSK -> AUTH_TYPE_WPA_PSK
            WifiAuthTypeBelowTiramisu.WPA_EAP -> AUTH_TYPE_WPA_EAP
            WifiAuthTypeBelowTiramisu.WPA2_PSK -> AUTH_TYPE_WPA2_PSK
            WifiAuthTypeBelowTiramisu.WPA_WPA2_PSK -> AUTH_TYPE_WPA_WPA2_PSK
            WifiAuthTypeBelowTiramisu.WPA2_EAP, WifiAuthTypeTiramisuOrAbove.WPA2_EAP -> AUTH_TYPE_WPA2_EAP
            else -> AUTH_TYPE_OPEN
        }
    }
}