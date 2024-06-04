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

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.scalars)
    implementation(libs.retrofit.converter.wire)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation("com.squareup.okhttp3:okhttp-tls:5.0.0-alpha.12")
    implementation("org.slf4j:slf4j-api:1.7.36") // TODO replace with libs
}

wire {
    kotlin {}
}