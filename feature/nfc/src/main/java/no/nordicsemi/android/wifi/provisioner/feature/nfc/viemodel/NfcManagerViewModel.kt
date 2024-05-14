package no.nordicsemi.android.wifi.provisioner.feature.nfc.viemodel

import android.app.Activity
import android.nfc.NdefMessage
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import no.nordicsemi.android.wifi.provisioner.nfc.NfcManagerForWifi
import javax.inject.Inject

/**
 * ViewModel for the NFC manager.
 */
@HiltViewModel
internal class NfcManagerViewModel @Inject constructor(
    private val nfcManagerForWifi: NfcManagerForWifi,
) : ViewModel() {

    fun onScan(activity: Activity, ndefMessage: NdefMessage) {
        nfcManagerForWifi.onNfcTap(
            activity = activity,
            message = ndefMessage
        )
    }

    fun onPause(activity: Activity) {
        nfcManagerForWifi.onPause(activity)
    }
}