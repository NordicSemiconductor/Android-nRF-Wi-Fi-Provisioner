package no.nordicsemi.android.wifi.provisioner.feature.nfc.viewmodel

import android.app.Activity
import android.nfc.NdefMessage
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import no.nordicsemi.android.common.navigation.Navigator
import no.nordicsemi.android.common.navigation.viewmodel.SimpleNavigationViewModel
import no.nordicsemi.android.wifi.provisioner.feature.nfc.NfcDestinationId
import no.nordicsemi.android.wifi.provisioner.nfc.NdefMessageBuilder
import no.nordicsemi.android.wifi.provisioner.nfc.NfcManagerForWifi
import javax.inject.Inject

/**
 * ViewModel for the NFC manager.
 */
@HiltViewModel
internal class NfcManagerViewModel @Inject constructor(
    private val nfcManagerForWifi: NfcManagerForWifi,
    ndefMessageBuilder: NdefMessageBuilder,
    private val navigator: Navigator,
    savedStateHandle: SavedStateHandle,
) : SimpleNavigationViewModel(navigator, savedStateHandle) {
    private val params = parameterOf(NfcDestinationId)
    private val ndefMessage: NdefMessage = ndefMessageBuilder.createNdefMessage(params)

    fun onScan(activity: Activity) {
        nfcManagerForWifi.onNfcTap(
            activity = activity,
            message = ndefMessage
        )
    }

    fun onPause(activity: Activity) {
        nfcManagerForWifi.onPause(activity)
    }

    fun onBackNavigation() {
        navigator.navigateUp()
    }
}