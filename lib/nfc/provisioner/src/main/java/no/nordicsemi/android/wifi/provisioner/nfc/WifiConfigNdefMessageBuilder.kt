package no.nordicsemi.android.wifi.provisioner.nfc

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.provider.ContactsContract
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiData
import javax.inject.Inject
import kotlin.math.floor

internal const val NFC_TOKEN_MIME_TYPE: String = "application/vnd.wfa.wsc"
private val CREDENTIAL_FIELD_ID = byteArrayOf(0x10, 0x0e)
private val NETWORK_IDX: ByteArray = byteArrayOf(0x10, 0x26)
private val WPS_AUTH_WPA2_PERSONAL: ByteArray = byteArrayOf(0x00, 0x20)

private val WPS_CRYPT_AES: ByteArray = byteArrayOf(0x00, 0x08)
private val AUTH_TYPE: ByteArray = byteArrayOf(0x10, 0x03)
private val AUTH_WPA_PERSONAL: ByteArray = byteArrayOf(0x00, 0x02)
private val NETWORK_KEY: ByteArray = byteArrayOf(0x10, 0x27)
private val NETWORK_NAME: ByteArray = byteArrayOf(0x10, 0x45)

private val CRYPT_TYPE: ByteArray = byteArrayOf(0x10, 0x0F)
private val CRYPT_WEP: ByteArray = byteArrayOf(0x00, 0x02)

/**
 * This class is responsible for creating the NDEF message for the WiFi data.
 */
class WifiConfigNdefMessageBuilder @Inject constructor() {

    /**
     * Creates the NDEF record for the WiFi data.
     *
     * @param wifiData the WiFi data to be written to the NDEF record.
     * @return the NDEF record for the WiFi data.
     */
    private fun createWifiRecord(wifiData: WifiData): NdefRecord {
        val ssid = wifiData.ssid
        val password = wifiData.password
        val auth = wifiData.authType // TODO: For now, we are only supporting WPA2_PSK.
        val crypt = "AES" // TODO: For now, we are only supporting AES.
        val authByte = WPS_AUTH_WPA2_PERSONAL
        val cryptByte = WPS_CRYPT_AES
        val ssidByte = ssid.toByteArray()
        val passwordByte = password.toByteArray()
        val ssidLength = byteArrayOf(
            floor((ssid.length / 256).toDouble())
                .toInt().toByte(), (ssid.length % 256).toByte()
        )
        val passwordLength = byteArrayOf(
            floor((password.length / 256).toDouble())
                .toInt().toByte(), (password.length % 256).toByte()
        )
        val cred = byteArrayOf(0x00, 0x36)
        val idx = byteArrayOf(0x00, 0x01, 0x01)
        val mac = byteArrayOf(0x00, 0x06) // TODO: Get the mac address of the device.
        val keypad = byteArrayOf(0x00, 0x0B) // TODO: Get the keypad of the device.

        val payload: ByteArray = concat(
            CREDENTIAL_FIELD_ID, cred,
            NETWORK_IDX, idx,
            NETWORK_NAME, ssidLength, ssidByte,
            AUTH_TYPE, AUTH_WPA_PERSONAL, authByte,
            CRYPT_TYPE, CRYPT_WEP, cryptByte,
            NETWORK_KEY, passwordLength, passwordByte
        )
        return NdefRecord.createMime(NFC_TOKEN_MIME_TYPE, payload)
    }

    /**
     * Creates the NDEF message for the WiFi data.
     *
     * @param wifiNetwork the WiFi data to be written to the NDEF message.
     * @return the NDEF message for the WiFi data.
     */
    fun createNdefMessage(wifiNetwork: WifiData): NdefMessage {
        val version = byteArrayOf(((0x1 shl 4) or (0x2)).toByte())
        // TODO: Not sure is this is needed.
        val handOverRecord = NdefRecord(
            NdefRecord.TNF_WELL_KNOWN,
            NdefRecord.RTD_HANDOVER_REQUEST,
            ByteArray(0),
            version
        )
        // TODO: Not sure is this is needed.
        val aar = NdefRecord.createApplicationRecord(ContactsContract.Directory.PACKAGE_NAME)
        val wifiRecord = createWifiRecord(wifiNetwork)
        return NdefMessage(arrayOf(wifiRecord)) //, handOverRecord, aar))
    }

    /**
     * Concatenates the given byte arrays into a single byte array.
     *
     * @param arrays the byte arrays to be concatenated.
     * @return the concatenated byte array.
     */
    private fun concat(vararg arrays: ByteArray): ByteArray {
        // Calculate the total length of the resulting ByteArray
        val totalLength = arrays.sumOf { it.size }

        // Create a new ByteArray with the calculated length
        val result = ByteArray(totalLength)

        // Keep track of the current position in the result array
        var currentPos = 0

        // Copy each ByteArray into the result array
        for (array in arrays) {
            System.arraycopy(array, 0, result, currentPos, array.size)
            currentPos += array.size
        }

        return result
    }

}