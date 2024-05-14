package no.nordicsemi.android.wifi.provisioner.nfc

import android.app.Activity
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import javax.inject.Inject
import javax.inject.Singleton

enum class NfcFlags(val value: Int) {
    NFC_A(NfcAdapter.FLAG_READER_NFC_A),
    NFC_B(NfcAdapter.FLAG_READER_NFC_B),
    NFC_F(NfcAdapter.FLAG_READER_NFC_F),
    NFC_V(NfcAdapter.FLAG_READER_NFC_V),
    NFC_BARCODE(NfcAdapter.FLAG_READER_NFC_BARCODE),
}

/** The Nfc reader flags for the NfcAdapter. */
private val flags = setOf(
    NfcFlags.NFC_A,
    NfcFlags.NFC_B,
    NfcFlags.NFC_F,
    NfcFlags.NFC_V,
    NfcFlags.NFC_BARCODE
).fold(0) { acc, flag -> acc or flag.value }

/**
 * A class that manages the NFC adapter for the wifi provisioning.
 */
@Singleton
class NfcManagerForWifi @Inject constructor(
    private val nfcAdapter: NfcAdapter?,
) {
    private var ndefMessage: NdefMessage? = null

    /**
     * Handles the NFC tap event.
     * @param activity the activity.
     * @param message the Ndef message.
     */
    fun onNfcTap(activity: Activity, message: NdefMessage) {
        ndefMessage = message
        nfcAdapter?.takeIf { it.isEnabled }?.let {
            val readerFlag = getReaderFlag()
            it.enableReaderMode(activity, ::onTagDiscovered, readerFlag, null)
        }
    }

    /**
     * Callback when tag is discovered.
     * @param tag the discovered tag.
     */
    private fun onTagDiscovered(tag: Tag?) {
        try {
            tag?.let {
                ndefMessage?.let {
                    if (tag.techList.contains(Ndef::class.java.name)) {
                        // Write the Ndef message to the tag.
                        val ndef = Ndef.get(tag)
                        try {
                            ndef.connect()
                            ndef.writeNdefMessage(it)
                            ndef.close()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if (tag.techList.contains(NdefFormatable::class.java.name)) {
                        // Format the tag and write the Ndef message.
                        val ndefFormatable = NdefFormatable.get(tag)
                        try {
                            ndefFormatable.connect()
                            ndefFormatable.format(it)
                            ndefFormatable.close()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Returns each NfcFlags and plays sounds while scanning if the isSoundOn parameter is set to ON.
     */
    private fun getReaderFlag(): Int {
        val soundFlag = 0
        return flags or soundFlag
    }


    /**
     * Pauses the NFC reader.
     * @param activity the activity.
     */
    fun onPause(activity: Activity) {
        nfcAdapter?.disableReaderMode(activity)
    }
}