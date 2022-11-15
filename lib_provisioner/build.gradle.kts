plugins {
    alias(libs.plugins.nordic.library)
}

android {
    namespace = "no.nordicsemi.wifi.provisioner.library"
}

dependencies {
    implementation(project(":lib_proto"))

    implementation("no.nordicsemi.android:ble-common:2.5.1")
    implementation("no.nordicsemi.android:ble-ktx:2.5.1")
    implementation("no.nordicsemi.android.common:uilogger:1.0.24")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.6.1")
}
