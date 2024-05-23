package no.nordicsemi.android.wifi.provisioner.nfc

import android.app.Activity
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

sealed interface NfcScanEvent
data object Loading : NfcScanEvent
data object Success : NfcScanEvent
data class Error(val message: String) : NfcScanEvent

/**
 * A class that manages the NFC adapter for the wifi provisioning.
 */
@Singleton
class NfcManagerForWifi @Inject constructor(
    private val nfcAdapter: NfcAdapter?,
) {
    private val _nfcScanEvent = MutableStateFlow<NfcScanEvent?>(null)
    val nfcScanEvent = _nfcScanEvent.asStateFlow()
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
        _nfcScanEvent.value = Loading
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
                            _nfcScanEvent.value = Success
                        } catch (e: Exception) {
                            _nfcScanEvent.value = Error(e.message ?: "Error writing Ndef message")
                            e.printStackTrace()
                        }
                    } else if (tag.techList.contains(NdefFormatable::class.java.name)) {
                        // Format the tag and write the Ndef message.
                        val ndefFormatable = NdefFormatable.get(tag)
                        try {
                            ndefFormatable.connect()
                            ndefFormatable.format(it)
                            ndefFormatable.close()
                            _nfcScanEvent.value = Success
                        } catch (e: Exception) {
                            _nfcScanEvent.value = Error(e.message ?: "Error formatting Ndef message")
                            e.printStackTrace()
                        }
                    } else {
                        // The tag does not support Ndef or NdefFormatable.
                        // Show an error message.
                        _nfcScanEvent.value = Error("Tag does not support Ndef or NdefFormatable")
                    }
                }
            }
        } catch (e: Exception) {
            _nfcScanEvent.value = Error(e.message ?: "Unknown error occurred")
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