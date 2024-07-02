plugins {
    alias(libs.plugins.nordic.library)
    alias(libs.plugins.nordic.kotlin.android)
    alias(libs.plugins.nordic.nexus.android)
    alias(libs.plugins.wire)
}

group = "no.nordicsemi.android.wifi"

nordicNexusPublishing {
    POM_ARTIFACT_ID = "provisioning-softap"
    POM_NAME = "Wi-Fi provisioning over Wi-Fi"
    POM_DESCRIPTION = "A library for provisioning nRF700x devices over SoftAP."
    POM_URL = "https://github.com/NordicSemiconductor/Android-nRF-Wi-Fi-Provisioner"
    POM_SCM_URL = "https://github.com/NordicSemiconductor/Android-nRF-Wi-Fi-Provisioner"
    POM_SCM_CONNECTION = "scm:git@github.com:NordicSemiconductorAndroid-nRF-Wi-Fi-Provisioner.git"
    POM_SCM_DEV_CONNECTION = "scm:git@github.com:NordicSemiconductor/Android-nRF-Wi-Fi-Provisioner.git"
}

android {
    namespace = "no.nordicsemi.android.wifi.provisioner.softap"
}

dependencies {
    api(project(":lib:domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.annotation)

    implementation(libs.slf4j)

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.wire)
    implementation(libs.okhttp)
    implementation(libs.okhttp.tls)
    implementation(libs.okhttp.logging)
}

wire {
    kotlin {}
}