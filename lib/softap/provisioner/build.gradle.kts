plugins {
    alias(libs.plugins.nordic.library)
    alias(libs.plugins.nordic.kotlin)
    alias(libs.plugins.wire)
}

android {
    namespace = "no.nordicsemi.android.wifi.provisioner.softap"
}

dependencies {
    api(project(":lib:domain"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.annotation)
    implementation(libs.nordic.core)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.scalars)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation("com.squareup.retrofit2:converter-wire:2.11.0")
    implementation("com.squareup.okhttp3:okhttp-tls:5.0.0-alpha.12")
}

wire {
    kotlin {}
}