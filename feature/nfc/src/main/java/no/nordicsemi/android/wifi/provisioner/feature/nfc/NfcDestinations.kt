package no.nordicsemi.android.wifi.provisioner.feature.nfc

import android.os.Build
import androidx.annotation.RequiresApi
import no.nordicsemi.android.common.navigation.createDestination
import no.nordicsemi.android.common.navigation.createSimpleDestination
import no.nordicsemi.android.common.navigation.defineDestination
import no.nordicsemi.android.wifi.provisioner.feature.nfc.view.NfcPublishScreen
import no.nordicsemi.android.wifi.provisioner.feature.nfc.view.HomeScreen
import no.nordicsemi.android.wifi.provisioner.feature.nfc.view.WifiScannerScreen
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiData

val NfcDestination = createSimpleDestination("nfc")
val WifiScannerDestination =
    createSimpleDestination(
        name = "wifi-scanner-destination",
    )

val NfcPublishDestination =
    createDestination<WifiData, Unit>("publish-destination")


@RequiresApi(Build.VERSION_CODES.M)
val NfcProvisionerDestinations = listOf(
    defineDestination(NfcDestination) {
        HomeScreen()
    },
    defineDestination(WifiScannerDestination) {
        WifiScannerScreen()
    },
    defineDestination(NfcPublishDestination) {
        NfcPublishScreen()
    }
)