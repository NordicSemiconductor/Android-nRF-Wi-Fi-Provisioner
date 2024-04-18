
plugins {
    alias(libs.plugins.nordic.library)
}

android {
    namespace = "no.nordicsemi.android.wifi.provisioner.common"
}

dependencies{
    implementation(project(":lib:domain"))
}