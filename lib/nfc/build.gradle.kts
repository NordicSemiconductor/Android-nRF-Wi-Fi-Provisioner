plugins {
    alias(libs.plugins.nordic.library)
    alias(libs.plugins.nordic.kotlin)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "no.nordicsemi.android.wifi.provisioner.nfc"
}

dependencies {
    implementation(libs.nordic.ble.ktx)
}