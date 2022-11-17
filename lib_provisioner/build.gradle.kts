plugins {
    alias(libs.plugins.nordic.library)
}

android {
    namespace = "no.nordicsemi.wifi.provisioner.library"
}

dependencies {
    implementation(project(":lib_proto"))

    implementation(libs.nordic.ble.common)
    implementation(libs.nordic.ble.ktx)
    implementation(libs.nordic.uilogger)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
}
