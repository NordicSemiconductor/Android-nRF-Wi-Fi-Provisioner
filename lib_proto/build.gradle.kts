plugins {
    alias(libs.plugins.nordic.library)
    alias(libs.plugins.wire)
}

wire {
    kotlin {}
}

android {
    namespace = "no.nordicsemi.wifi.provisioner.proto"
}
