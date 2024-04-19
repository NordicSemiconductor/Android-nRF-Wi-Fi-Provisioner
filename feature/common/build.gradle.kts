plugins {
    alias(libs.plugins.nordic.library)
    alias(libs.plugins.nordic.kotlin)
}

android {
    namespace = "no.nordicsemi.wifi.provisioner.common"
}

dependencies {
    implementation(project(":lib:domain"))
}
