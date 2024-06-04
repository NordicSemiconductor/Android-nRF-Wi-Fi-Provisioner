plugins {
    alias(libs.plugins.nordic.feature)
}

android {
    namespace = "no.nordicsemi.wifi.provisioner.common"
}

dependencies {
    implementation(project(":lib:domain"))

    implementation(libs.nordic.navigation)
}
