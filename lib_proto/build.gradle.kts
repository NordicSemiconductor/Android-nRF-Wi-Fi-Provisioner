plugins {
    alias(libs.plugins.nordic.library)
    alias(libs.plugins.wire) version "4.4.3"
}

wire {
    kotlin {}
}

android {
    namespace = "no.nordicsemi.wifi.provisioner.proto"
}
