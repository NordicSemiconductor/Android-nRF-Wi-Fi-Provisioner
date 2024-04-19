plugins {
    alias(libs.plugins.nordic.library)
    alias(libs.plugins.nordic.kotlin)
    alias(libs.plugins.wire)
}

android {
    namespace = "no.nordicsemi.wifi.provisioner.domain"
}

dependencies{
    implementation(libs.androidx.core.ktx)
    implementation(libs.nordic.ble.common)
    implementation(libs.nordic.ble.ktx)
    implementation(libs.nordic.uilogger)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
}

wire {
    kotlin {}
}