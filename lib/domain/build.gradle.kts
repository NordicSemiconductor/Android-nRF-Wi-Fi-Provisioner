plugins {
    alias(libs.plugins.nordic.library)
    alias(libs.plugins.nordic.kotlin.android)
    alias(libs.plugins.nordic.nexus.android)
    alias(libs.plugins.wire)
}

group = "no.nordicsemi.android.wifi"

nordicNexusPublishing {
    POM_ARTIFACT_ID = "domain"
    POM_NAME = "Wi-Fi provisioning domain library"
    POM_DESCRIPTION = "A domain library for provisioning nRF700x devices."
    POM_URL = "https://github.com/NordicSemiconductor/Android-nRF-Wi-Fi-Provisioner"
    POM_SCM_URL = "https://github.com/NordicSemiconductor/Android-nRF-Wi-Fi-Provisioner"
    POM_SCM_CONNECTION = "scm:git@github.com:NordicSemiconductorAndroid-nRF-Wi-Fi-Provisioner.git"
    POM_SCM_DEV_CONNECTION = "scm:git@github.com:NordicSemiconductor/Android-nRF-Wi-Fi-Provisioner.git"
}

android {
    namespace = "no.nordicsemi.kotlin.wifi.provisioner.domain"
}

wire {
    kotlin {}
}