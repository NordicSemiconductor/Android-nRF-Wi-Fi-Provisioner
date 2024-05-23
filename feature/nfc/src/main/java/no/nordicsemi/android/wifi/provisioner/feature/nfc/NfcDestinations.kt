package no.nordicsemi.android.wifi.provisioner.feature.nfc

import android.os.Build
import androidx.annotation.RequiresApi
import no.nordicsemi.android.common.navigation.createDestination
import no.nordicsemi.android.common.navigation.createSimpleDestination
import no.nordicsemi.android.common.navigation.defineDestination
import no.nordicsemi.android.wifi.provisioner.feature.nfc.view.NfcProvisioningScreen
import no.nordicsemi.android.wifi.provisioner.feature.nfc.view.NfcScreen
import no.nordicsemi.android.wifi.provisioner.feature.nfc.view.WifiScannerView
import no.nordicsemi.android.wifi.provisioner.nfc.domain.WifiData

val NfcProvisionerDestinationId = createSimpleDestination("nfc-provider-destination")
val WifiScannerDestinationId =
    createSimpleDestination(
        name = "wifi-scanner-destination",
    )

val NfcDestinationId =
    createDestination<WifiData, Unit>("provision-over-nfc-destination")


@RequiresApi(Build.VERSION_CODES.M)
val NfcProvisionerDestinations = listOf(
    defineDestination(NfcProvisionerDestinationId) {
        NfcProvisioningScreen()
    },
    defineDestination(WifiScannerDestinationId) {
        WifiScannerView()
    },
    defineDestination(NfcDestinationId) {
        NfcScreen()
    }
)