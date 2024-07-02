plugins {
    alias(libs.plugins.nordic.library)
    alias(libs.plugins.nordic.kotlin.android)
    alias(libs.plugins.nordic.nexus.android)
    alias(libs.plugins.kotlin.parcelize)
}

group = "no.nordicsemi.android.wifi"

nordicNexusPublishing {
    POM_ARTIFACT_ID = "provisioning-nfc"
    POM_NAME = "Wi-Fi provisioning over NFC Library"
    POM_DESCRIPTION = "A library for provisioning nRF700x devices over NFC."
    POM_URL = "https://github.com/NordicSemiconductor/Android-nRF-Wi-Fi-Provisioner"
    POM_SCM_URL = "https://github.com/NordicSemiconductor/Android-nRF-Wi-Fi-Provisioner"
    POM_SCM_CONNECTION = "scm:git@github.com:NordicSemiconductorAndroid-nRF-Wi-Fi-Provisioner.git"
    POM_SCM_DEV_CONNECTION = "scm:git@github.com:NordicSemiconductor/Android-nRF-Wi-Fi-Provisioner.git"
}

android {
    namespace = "no.nordicsemi.android.wifi.provisioner.nfc"
}

dependencies {
    implementation(libs.nordic.ble.ktx)
}