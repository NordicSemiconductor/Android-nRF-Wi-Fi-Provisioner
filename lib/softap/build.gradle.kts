plugins {
    alias(libs.plugins.nordic.library)
    alias(libs.plugins.nordic.kotlin.android)
    alias(libs.plugins.wire)
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